package mobi.fhdo.geoschnitzeljagd.Activities;

import android.app.ActionBar;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import mobi.fhdo.geoschnitzeljagd.DataManagers.GPSTracker;
import mobi.fhdo.geoschnitzeljagd.R;

public class NearbyActivity extends ActionBarActivity {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle drawerToggle;
    public ListView listView;
    GoogleMap googleMap;
    ArrayList<Location> locations = new ArrayList<Location>();

    /*
    Bochum
        Latitude : 51.4925
        Longitude : 7.3106
    Dortmund
        Latitude : 51.3000
        Longitude : 7.2800
    Essen
        Latitude : 51.2800
        Longitude : 7.0200
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        ActionBar actionBar = getActionBar();

        Location l1 = new Location("Bochumn");
        l1.setLatitude(51.4925);
        l1.setLongitude(7.3106);
        locations.add(l1);
        Location l2 = new Location("Dortmund");
        l2.setLatitude(51.3000);
        l2.setLongitude(7.2800);
        locations.add(l2);
        Location l3 = new Location("Essen");
        l3.setLatitude(51.2800);
        l3.setLongitude(7.0200);
        locations.add(l3);


        drawerLayout = (DrawerLayout) findViewById(R.id.ganzesLayout);
        drawerToggle = new ActionBarDrawerToggle(NearbyActivity.this, drawerLayout, R.drawable.ic_drawer, R.string.open, R.string.close);
        // Navigationsleiste -> Drawer Öffnen
        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        listView = (ListView) findViewById(R.id.routenListe);
        ArrayAdapter<Location> routenListAdapater = new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1, locations);
        listView.setAdapter(routenListAdapater);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (listView.getPositionForView(view)) {
                    case 0: {
                        Toast.makeText(getApplicationContext(), "Bochum ausgewählt", Toast.LENGTH_SHORT).show();
                        newLocation(locations.get(0));
                        drawerLayout.closeDrawers();
                        break;
                    }
                    case 1: {
                        Toast.makeText(getApplicationContext(), "Dortmund ausgewählt", Toast.LENGTH_SHORT).show();
                        newLocation(locations.get(1));
                        drawerLayout.closeDrawers();
                        break;
                    }
                    case 2: {
                        Toast.makeText(getApplicationContext(), "Essen ausgewählt", Toast.LENGTH_SHORT).show();
                        newLocation(locations.get(2));
                        drawerLayout.closeDrawers();
                        break;
                    }

                }
            }
        });


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent myIntent = new Intent(this, HomeActivity.class);
            startActivity(myIntent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Initialises the mapview
     */
    private void createMapView() {
        /**
         * Catch the null pointer exception that
         * may be thrown when initialising the map
         */
        try {
            if (null == googleMap) {
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                        R.id.mapView)).getMap();

                /**
                 * If the map is still null after attempted initialisation,
                 * show an error to the user
                 */
                if (null == googleMap) {
                    Toast.makeText(getApplicationContext(),
                            "Error creating map", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NullPointerException exception) {
            Log.e("mapApp", exception.toString());
        }
    }

    /**
     * Adds a marker to the map
     */
    private void addMarker() {

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


   /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */

    private void initLocation() {
        GPSTracker gpsTracker = new GPSTracker(this);
        Location location = gpsTracker.getLocation();
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("aktueller Standort")
                .draggable(true));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(location.getLatitude(), location.getLongitude())), 15));
    }

    private void newLocation(Location l) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(l.getLatitude(), l.getLongitude()))
                .draggable(false));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(l.getLatitude(), l.getLongitude())), 15));

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        drawerToggle.syncState();
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       /* int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
        */

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }


}


