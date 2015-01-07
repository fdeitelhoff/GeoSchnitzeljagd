package mobi.fhdo.geoschnitzeljagd.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Base64;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
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
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener
{

    private EditText paperchaseNameEditText;
    private TextView waypointCountEditText;

    private GoogleMap googleMap;

    private GPSTracker gpsTracker;

    private User loggedInUser;

    private LinkedHashMap<Marker, UUID> waypoints;
    private Marker currentWaypoint;

    private HashMap<Integer, Float> waypointColors;

    private Paperchases paperchases;

    private Paperchase paperchase;

    private LatLngBounds bounds;

    private boolean isCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        gpsTracker = new GPSTracker(this);

        //Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_paperchase);

        // Save the paperchase.
        Button savePaperchaseButton = (Button) findViewById(R.id.button_save_waypoints);
        savePaperchaseButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                if (paperchaseNameEditText.getText().toString().trim().isEmpty())
                {
                    new AlertDialog.Builder(PaperchaseActivity.this)
                            .setTitle("Fehlender Name")
                            .setMessage("Die Schnitzeljagd kann nicht ohne Namen gespeichert werden!")
                            .setNeutralButton("Ok", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int whichButton)
                                {
                                }
                            })
                            .show();
                }
                else
                {
                    savePaperchase();

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Paperchase", paperchase);
                    setResult(RESULT_OK, returnIntent);

                    finish();
                }
            }
        });

        // Add a new waypoint.
        Button addWaypointButton = (Button) findViewById(R.id.button_add_waypoint);
        addWaypointButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                addMarker(true);
            }
        });

        // Remove a new waypoint.
        Button removeWaypoint = (Button) findViewById(R.id.button_remove_waypoint);
        removeWaypoint.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                if (currentWaypoint != null)
                {
                    waypoints.remove(currentWaypoint);

                    currentWaypoint.remove();
                    currentWaypoint = null;

                    int waypointNumber = 0;
                    for (Marker waypoint : waypoints.keySet())
                    {
                        waypoint.setTitle(++waypointNumber + ". Wegpunkt");
                        waypoint.setIcon(BitmapDescriptorFactory.defaultMarker(waypointColors.get(waypointNumber)));
                    }

                    waypointCountEditText.setText("Aktuell " + waypoints.size() + "/5 Wegpunkten");
                }
            }
        });

        paperchaseNameEditText = (EditText) findViewById(R.id.editText_paperchase_name);
        waypointCountEditText = (TextView) findViewById(R.id.textViewWayPointCount);

        loggedInUser = UserContext.getInstance().getLoggedInUser();

        waypoints = new LinkedHashMap<Marker, UUID>();
        waypointColors = new HashMap<Integer, Float>();
        waypointColors.put(1, BitmapDescriptorFactory.HUE_GREEN);
        waypointColors.put(2, BitmapDescriptorFactory.HUE_ORANGE);
        waypointColors.put(3, BitmapDescriptorFactory.HUE_CYAN);
        waypointColors.put(4, BitmapDescriptorFactory.HUE_BLUE);
        waypointColors.put(5, BitmapDescriptorFactory.HUE_RED);

        paperchases = new Paperchases(this);

        createMapView();

        // If there's a paperchase present we want to display its data.
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            paperchase = (Paperchase) extras.getSerializable("Paperchase");

            paperchaseNameEditText.setText(paperchase.getName());

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (Mark mark : paperchase.getMarks())
            {
                addMarker(mark.getPosition(), false,
                        mark.getId(), mark.getHint());

                builder.include(mark.getPosition());
            }

            bounds = builder.build();

            waypointCountEditText.setText("Aktuell " + waypoints.size() + "/5 Wegpunkten");
        }
        else
        {
            // Without a paperchase we add an initial marker on the map.
            addMarker(true);
        }
    }

    private void createMapView()
    {
        try
        {
            if (googleMap == null)
            {
                // Get the Google maps fragment.
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                        R.id.mapView)).getMap();

                // Some UI adjustments.
                googleMap.getUiSettings().setZoomControlsEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(false);
                googleMap.getUiSettings().setTiltGesturesEnabled(false);
                googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback()
                {
                    @Override
                    public void onMapLoaded()
                    {
                        if (bounds != null)
                        {
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250));
                            bounds = null;
                        }
                    }
                });

                // Some listener for important events.
                googleMap.setOnInfoWindowClickListener(this);
                googleMap.setOnMapLongClickListener(this);
                googleMap.setOnMarkerClickListener(this);
                googleMap.setOnMarkerDragListener(this);
            }
        }
        catch (NullPointerException exception)
        {
            Log.e("mapApp", exception.toString());
        }
    }

    @Override
    public void onInfoWindowClick(final Marker marker)
    {
        String waypointText = marker.getSnippet();

        if (waypointText.equals("Tippen um Hinweis zu setzen..."))
        {
            waypointText = "";
        }

        marker.hideInfoWindow();

        final EditText input = new EditText(this);
        input.setText(waypointText, TextView.BufferType.EDITABLE);

        new AlertDialog.Builder(this)
                .setTitle("Hinweis eingeben")
                .setMessage("Hinweis für den aktuellen Wegpunkt:")
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        Editable value = input.getText();

                        marker.setSnippet(value.toString());
                        marker.showInfoWindow();
                    }
                })
                .setNegativeButton("Abbrechen",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                // Abbrechen...
                                marker.showInfoWindow();
                            }
                        })
                .show();
    }

    @Override
    public void onMapLongClick(LatLng position)
    {
        if (waypoints.size() < 5)
        {
            addMarker(position, false);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        currentWaypoint = marker;
        marker.showInfoWindow();
        return true;
    }

    private void addMarker(boolean focusMap)
    {
        Location location = gpsTracker.getLocation();

        addMarker(new LatLng(location.getLatitude(), location.getLongitude()), focusMap);
    }

    private void addMarker(LatLng position, boolean focusMap)
    {
        addMarker(position, focusMap, UUID.randomUUID(), "Tippen um Hinweis zu setzen...");
    }

    private void addMarker(LatLng position, boolean focusMap, UUID uuid, String hint)
    {
        if (waypoints.size() < 5)
        {
            int waypointNumber = waypoints.size() + 1;

            Marker waypoint = googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(waypointNumber + ". Wegpunkt")
                    .snippet(hint)
                    .icon(BitmapDescriptorFactory.defaultMarker(waypointColors.get(waypointNumber)))
                    .draggable(true));

            waypoints.put(waypoint, uuid);

            currentWaypoint = waypoint;

            waypoint.showInfoWindow();

            if (focusMap)
            {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
            }

            waypointCountEditText.setText("Aktuell " + waypoints.size() + "/5 Wegpunkten");

            Log.w("Marker Added ", waypoint.getPosition().toString());
        }
        else
        {
            new AlertDialog.Builder(this)
                    .setTitle("Maximum erreichet")
                    .setMessage("Das Maximum von fünf Wegpunkten pro Schnitzeljagd in der Version wurde erreicht.")
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int whichButton)
                        {
                        }
                    })
                    .show();
        }
    }

    private void savePaperchase()
    {
        if (paperchase == null)
        {
            // New paperchase!
            createPaperchase();
        }
        else
        {
            // Paperchase already exists. Update it!
            updatePaperchase();
        }
    }

    private void createPaperchase()
    {
        UUID paperchaseUID = UUID.randomUUID();

        // Collect the marks for the waypoints.
        List<Mark> marks = new ArrayList<Mark>();
        int sequence = 0;
        for (Marker waypoint : waypoints.keySet())
        {
            Mark mark = new Mark(waypoints.get(waypoint),
                    paperchaseUID,
                    waypoint.getPosition().latitude,
                    waypoint.getPosition().longitude,
                    waypoint.getSnippet(),
                    ++sequence);

            marks.add(mark);

            Log.w("Marker For Create", mark.getPosition().toString());
        }

        // The timestamp is needed from the server!
        Paperchase paperchase = new Paperchase(paperchaseUID, loggedInUser,
                paperchaseNameEditText.getText().toString().trim(), new Timestamp(555), marks);

        this.paperchase = paperchase;

        paperchases.CreateOrUpdate(paperchase);

        // TODO: bitte Prüfen
        isCreate = true;
        String stringUrl = "http://schnitzeljagd.fabiandeitelhoff.de/api/v1/paperchase";
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
        {
            new DownloadWebpageTask().execute(stringUrl);
        }
        else
        {
            Log.d("No network connection available.", "No network connection available.");
        }
    }

    private void updatePaperchase()
    {
        // Collect the marks for the waypoints.
        List<Mark> marks = new ArrayList<Mark>();
        int sequence = 0;
        for (Marker waypoint : waypoints.keySet())
        {
            Mark mark = new Mark(waypoints.get(waypoint),
                    paperchase.getId(),
                    waypoint.getPosition().latitude,
                    waypoint.getPosition().longitude,
                    waypoint.getSnippet(),
                    ++sequence);

            marks.add(mark);

            Log.w("Marker For Update", mark.getPosition().toString());
        }

        paperchase.setName(paperchaseNameEditText.getText().toString().trim());
        paperchase.setMarks(marks);

        paperchases.update(paperchase);

        // TODO: Bitte Prüfen
        isCreate = false;
        String stringUrl = "http://schnitzeljagd.fabiandeitelhoff.de/api/v1/paperchase";
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
        {
            new DownloadWebpageTask().execute(stringUrl);
        }
        else
        {
            Log.d("No network connection available.", "No network connection available.");
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker)
    {
    }

    @Override
    public void onMarkerDrag(Marker marker)
    {
    }

    @Override
    public void onMarkerDragEnd(Marker marker)
    {
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String downloadUrl(String myurl) throws IOException
    {
        InputStream is = null;

        try
        {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String encode = new String(Base64.encode((UserContext.getInstance().getLoggedInUser().getUsername() + ":" + UserContext.getInstance().getLoggedInUser().getPassword()).getBytes(), Base64.DEFAULT));
            conn.setRequestProperty("Authorization", "Basic " + encode);

            conn.setRequestProperty("Content-Type", "application/json");

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);

            conn.setDoInput(true);
            conn.setDoOutput(true);

            if (isCreate)
            {
                conn.setRequestMethod("POST");
            }
            else
            {
                conn.setRequestMethod("PUT");
            }

            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();

            if(response == 200)
            {
                if(isCreate)
                {
                    // Save the paperchase with waypoints into the local database.
                    //paperchases.CreateOrUpdate(paperchase);
                }
                else
                {
                    //paperchases.update(paperchase);
                }
                Toast.makeText(getBaseContext(), "paperchase wurde angelegt bzw. geändert.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getBaseContext(), "paperchase wurde nicht geändert bzw. angelegt! Versuchen Sie es später nochmal.", Toast.LENGTH_LONG).show();
            }


            // Convert the InputStream into a string
            String contentAsString = UserContext.readIt(is);
            return contentAsString;
        }

        finally
        {
            if (is != null)
            {
                is.close();
            }
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            try
            {
                return downloadUrl(urls[0]);
            }
            catch (IOException e)
            {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
    }
}
