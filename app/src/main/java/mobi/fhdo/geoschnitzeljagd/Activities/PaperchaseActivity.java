package mobi.fhdo.geoschnitzeljagd.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import mobi.fhdo.geoschnitzeljagd.Contexts.UserContext;
import mobi.fhdo.geoschnitzeljagd.DataManagers.GPSTracker;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Paperchases;
import mobi.fhdo.geoschnitzeljagd.Model.Mark;
import mobi.fhdo.geoschnitzeljagd.Model.Paperchase;
import mobi.fhdo.geoschnitzeljagd.Model.User;
import mobi.fhdo.geoschnitzeljagd.R;

public class PaperchaseActivity extends Activity implements GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {

    private GPSTracker gpsTracker;

    private GoogleMap googleMap;

    private User loggedInUser;

    private LinkedHashMap<Marker, UUID> waypoints;
    private Marker currentWaypoint;

    private Paperchases paperchases;

    private Paperchase paperchase;

    private EditText paperchaseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gpsTracker = new GPSTracker(this);

        //Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_paperchase);

        // Save all waypoints.
        Button saveWaypoints = (Button) findViewById(R.id.button_save_waypoints);
        saveWaypoints.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (paperchaseName.getText().toString().trim().isEmpty()) {
                    new AlertDialog.Builder(PaperchaseActivity.this)
                            .setTitle("Fehlender Name")
                            .setMessage("Die Schnitzeljagd kann nicht ohne Namen gespeichert werden!")
                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            })
                            .show();
                } else {
                    savePaperchase();

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Paperchase", paperchase);
                    setResult(RESULT_OK, returnIntent);

                    finish();
                }
            }
        });

        // Add a new waypoint.
        Button addWaypoint = (Button) findViewById(R.id.button_add_waypoint);
        addWaypoint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                addMarker(true);
            }
        });

        // Remove a new waypoint.
        Button removeWaypoint = (Button) findViewById(R.id.button_remove_waypoint);
        removeWaypoint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (currentWaypoint != null) {
                    waypoints.remove(currentWaypoint);

                    currentWaypoint.remove();
                    currentWaypoint = null;

                    int waypointNumber = 0;
                    for (Marker waypoint : waypoints.keySet()) {
                        waypoint.setTitle(++waypointNumber + ". Wegpunkt");
                    }
                }
            }
        });

        paperchaseName = (EditText) findViewById(R.id.editText_paperchase_name);

        loggedInUser = UserContext.getInstance().getLoggedInUser();

        waypoints = new LinkedHashMap<Marker, UUID>();

        paperchases = new Paperchases(this);

        createMapView();

        // If there's a paperchase present we want to display its data.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            paperchase = (Paperchase) extras.getSerializable("Paperchase");

            paperchaseName.setText(paperchase.getName());

            int marks = 0;
            for (Mark mark : paperchase.getMarks()) {
                addMarker(new LatLng(mark.getLatitude(), mark.getLongitude()),
                        ++marks >= paperchase.getMarks().size(),        // Last marker should be focused.
                        mark.getId(), mark.getHint());
            }
        } else {
            // Without a paperchase we add an initial marker on the map.
            addMarker(true);
        }
    }

    private void createMapView() {
        try {
            if (googleMap == null) {
                // Get the Google maps fragment.
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                        R.id.mapView)).getMap();

                // Some UI adjustments.
                googleMap.getUiSettings().setZoomControlsEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(false);
                googleMap.getUiSettings().setTiltGesturesEnabled(false);

                // Some listener for important events.
                googleMap.setOnInfoWindowClickListener(this);
                googleMap.setOnMapLongClickListener(this);
                googleMap.setOnMarkerClickListener(this);
            }
        } catch (NullPointerException exception) {
            Log.e("mapApp", exception.toString());
        }
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        marker.hideInfoWindow();

        final EditText input = new EditText(this);
        input.setText(marker.getSnippet(), TextView.BufferType.EDITABLE);

        new AlertDialog.Builder(this)
                .setTitle("Hinweis für den Wegpunkt")
                .setMessage("Hinweis für den aktuellen Wegpunkt eingeben.")
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();

                        marker.setSnippet(value.toString());
                        marker.showInfoWindow();
                    }
                })
                .setNegativeButton("Abbrechen",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Abbrechen...
                                marker.showInfoWindow();
                            }
                        })
                .show();
    }

    @Override
    public void onMapLongClick(LatLng position) {
        addMarker(position, false);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        currentWaypoint = marker;
        marker.showInfoWindow();
        return true;
    }

    private void addMarker(boolean focusMap) {
        Location location = gpsTracker.getLocation();

        addMarker(new LatLng(location.getLatitude(), location.getLongitude()), focusMap);
    }

    private void addMarker(LatLng position, boolean focusMap) {
        addMarker(position, focusMap, UUID.randomUUID(), "Tippen um Hinweis zu setzen...");
    }

    private void addMarker(LatLng position, boolean focusMap, UUID uuid, String hint) {
        Marker waypoint = googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title((waypoints.size() + 1) + ". Wegpunkt")
                .snippet(hint)
                .draggable(true));

        waypoints.put(waypoint, uuid);

        waypoint.showInfoWindow();

        if (focusMap) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
        }
    }

    private void savePaperchase() {
        if (paperchase == null) {
            // New paperchase!
            createPaperchase();
        } else {
            // Paperchase already exists. Update it!
            updatePaperchase();
        }
    }

    private void createPaperchase() {
        UUID paperchaseUID = UUID.randomUUID();

        // TODO: An dieser Stelle an den Server schicken und bei Erfolg die lokale Datenbank aktualisieren.

        // Collect the marks for the waypoints.
        List<Mark> marks = new ArrayList<Mark>();
        int sequence = 0;
        for (Marker waypoint : waypoints.keySet()) {
            Mark mark = new Mark(waypoints.get(waypoint),
                    paperchaseUID,
                    waypoint.getPosition().latitude,
                    waypoint.getPosition().longitude,
                    waypoint.getSnippet(),
                    ++sequence);

            marks.add(mark);
        }

        // The timestamp is needed from the server!
        Paperchase paperchase = new Paperchase(paperchaseUID, loggedInUser,
                paperchaseName.getText().toString().trim(), new Timestamp(555), marks);

        // Save the paperchase with waypoints into the local database.
        paperchases.create(paperchase);

        this.paperchase = paperchase;
    }

    private void updatePaperchase() {
        // TODO: An dieser Stelle an den Server ein Update schicken Erfolg die lokale Datenbank aktualisieren.

        // Collect the marks for the waypoints.
        List<Mark> marks = new ArrayList<Mark>();
        int sequence = 0;
        for (Marker waypoint : waypoints.keySet()) {
            Mark mark = new Mark(waypoints.get(waypoint),
                    paperchase.getId(),
                    waypoint.getPosition().latitude,
                    waypoint.getPosition().longitude,
                    waypoint.getSnippet(),
                    ++sequence);

            marks.add(mark);
        }

        paperchase.setName(paperchaseName.getText().toString().trim());
        paperchase.setMarks(marks);

        paperchases.update(paperchase);
    }
}
