package mobi.fhdo.geoschnitzeljagd.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import mobi.fhdo.geoschnitzeljagd.DataManagers.GPSTracker;
import mobi.fhdo.geoschnitzeljagd.R;

public class MarkMapActivity extends Activity implements GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_mark_map);

        createMapView();
        setLocation();
    }

    private void createMapView() {
        /**
         * Catch the null pointer exception that
         * may be thrown when initialising the map
         */
        try {
            if (null == googleMap) {


                //googleMap = (GoogleMap) findViewById(R.id.mapView).;

                googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                        R.id.mapView)).getMap();

                googleMap.getUiSettings().setZoomControlsEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(false);
                googleMap.getUiSettings().setTiltGesturesEnabled(false);
                /**/
                /**
                 * If the map is still null after attempted initialisation,
                 * show an error to the user
                 */
                googleMap.setOnMapClickListener(this);
                googleMap.setOnMarkerDragListener(this);
                googleMap.setOnInfoWindowClickListener(this);

                if (null == googleMap) {
                    Toast.makeText(this,
                            "Error creating map", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NullPointerException exception) {
            Log.e("mapApp", exception.toString());
        }
    }

    private void setLocation() {
        GPSTracker gpsTracker = new GPSTracker(this);
        Location location = gpsTracker.getLocation();

        /*if (currentMarker != null) {
            currentMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        } else {*/
            /*Latitude : 51.3000
            Longitude : 7.2800*/
        Marker currentWaypoint = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        //.position(new LatLng(51.3000, 7.2800))
                .title("Aktueller Wegpunkt")
                .snippet("Aktuell kein Hinweis hinterlegt.")
                .draggable(true));

        currentWaypoint.showInfoWindow();
        //}

        //Log.d("aktuelles Standort", "aktuelles Standort: " + "Lat: " + location.getLatitude() + "Long: " + location.getLongitude());

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(location.getLatitude(), location.getLongitude())), 15));
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.3000, 7.2800), 15));
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        marker.hideInfoWindow();

        final EditText input = new EditText(this);
        input.setText(marker.getSnippet(), TextView.BufferType.EDITABLE);

        new AlertDialog.Builder(this)
                .setTitle("Hinweis für den Wegpunkt")
                .setMessage("Hinweis für den Suchenden beim aktuellen Wegpunkt.")
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
                            }
                        })
                .show();
    }
}
