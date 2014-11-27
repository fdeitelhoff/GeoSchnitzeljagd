package mobi.fhdo.geoschnitzeljagd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import mobi.fhdo.geoschnitzeljagd.DataManagers.GPSTracker;

public class NearbyActivity extends Activity
{
    /**
     * Local variables *
     */
    GoogleMap googleMap;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        createMapView();
        addMarker();
        initLocation();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            Intent myIntent = new Intent(this, HomeActivity.class);
            startActivity(myIntent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Initialises the mapview
     */
    private void createMapView()
    {
        /**
         * Catch the null pointer exception that
         * may be thrown when initialising the map
         */
        try
        {
            if (null == googleMap)
            {
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                        R.id.mapView)).getMap();

                /**
                 * If the map is still null after attempted initialisation,
                 * show an error to the user
                 */
                if (null == googleMap)
                {
                    Toast.makeText(getApplicationContext(),
                            "Error creating map", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (NullPointerException exception)
        {
            Log.e("mapApp", exception.toString());
        }
    }

    /**
     * Adds a marker to the map
     */
    private void addMarker()
    {

        /** Make sure that the map has been initialised **/
        /** if(null != googleMap){
         googleMap.addMarker(new MarkerOptions()
         .position(new LatLng(0, 0))
         .title("Marker")
         .draggable(true)
         );
         }
         */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initLocation()
    {
        try
        {
            GPSTracker gpsTracker = new GPSTracker(this);
            Location location = gpsTracker.getLocation();
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView)).getMap();
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .title("aktueller Standort")
                    .draggable(true));

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(location.getLatitude(), location.getLongitude())), 15));
        }
        catch (Exception exception)
        {
            Log.e("mapApp", exception.toString());
        }
    }


}


