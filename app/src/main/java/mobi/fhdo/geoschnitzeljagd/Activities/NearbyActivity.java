package mobi.fhdo.geoschnitzeljagd.Activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.ClusterManager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mobi.fhdo.geoschnitzeljagd.Contexts.UserContext;
import mobi.fhdo.geoschnitzeljagd.DataManagers.GPSTracker;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Paperchases;
import mobi.fhdo.geoschnitzeljagd.Model.Mark;
import mobi.fhdo.geoschnitzeljagd.Model.Paperchase;
import mobi.fhdo.geoschnitzeljagd.Model.PaperchaseCompleted;
import mobi.fhdo.geoschnitzeljagd.Model.User;
import mobi.fhdo.geoschnitzeljagd.R;

public class NearbyActivity extends ActionBarActivity implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMyLocationChangeListener, GoogleMap.OnMarkerDragListener {
    // ActionBarActivity

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle drawerToggle;
    public ListView listView;
    GoogleMap googleMap;
    ArrayList<Location> locations = new ArrayList<Location>();
    private User loggedInUser;
    private Paperchases paperchases;
    private List<Paperchase> paperchaselist;
    private ClusterManager<Mark> markClusterManager;
    private List<Marker> paperchaseLocation = new ArrayList<Marker>();
    private List<Marker> paperchaseMarksLocation = new ArrayList<Marker>();
    private SphericalUtil sphericalUtil;
    private Paperchase aktiveJagd;

    private PaperchaseCompleted paperchaseCompleted;

    private boolean jagdaktive;
    private int aktuelleMarkierung;
    private int anzMarkierung;
    private boolean paperchaseFinished;

    private GPSTracker gpsTracker;

    private double DISTANCE_TO_MARK = 20.0;


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
        jagdaktive = false;
        aktuelleMarkierung = 0;
        paperchaseFinished = false;

        loggedInUser = UserContext.getInstance().getLoggedInUser();

        gpsTracker = new GPSTracker(this);


       /* Location l1 = new Location("Paperchase 1");
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
        */


        drawerLayout = (DrawerLayout) findViewById(R.id.ganzesLayout);
        drawerToggle = new ActionBarDrawerToggle(NearbyActivity.this, drawerLayout, R.drawable.ic_drawer, R.string.open, R.string.close);
        // Navigationsleiste -> Drawer Öffnen
        drawerLayout.setDrawerListener(drawerToggle);

        createMapView();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        markClusterManager = new ClusterManager<Mark>(this, googleMap);
        googleMap.setOnCameraChangeListener(markClusterManager);
        //googleMap.setOnMarkerClickListener(markClusterManager);

        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMyLocationChangeListener(this);

        googleMap.setOnMarkerDragListener(this);


        paperchases = new Paperchases(this);
        paperchaselist = paperchases.All();
        List<Mark> marks;
        Mark mark;
        for (int i = 0; i < paperchaselist.size(); i++) {
            Paperchase paperchase = paperchaselist.get(i);
            String paperchaseName = paperchase.getName();
            marks = paperchase.getMarks();
            if (marks.size() > 0) {
                mark = marks.get(0);
                Location l = new Location(paperchaseName);
                l.setLatitude(mark.getLatitude());
                l.setLongitude(mark.getLongitude());
                paperchaseLocation.add(googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(mark.getLatitude(), mark.getLongitude()))
                        .title(paperchaseName)
                        .snippet("Anzahl Wegpunkte: " + marks.size())
                        .draggable(false)));

                Circle circle = googleMap.addCircle(new CircleOptions()
                        .center(new LatLng(mark.getLatitude(), mark.getLongitude()))
                        .radius(20.0)
                        .strokeColor(Color.RED)
                        .fillColor(Color.TRANSPARENT));
                markClusterManager.addItem(mark);
                locations.add(l);
            }

        }

        initLocation();

        listView = (ListView) findViewById(R.id.routenListe);
        ArrayAdapter<Location> routenListAdapater = new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1, locations);
        listView.setAdapter(routenListAdapater);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (listView.getPositionForView(view)) {
                    case 0: {
                        Toast.makeText(getApplicationContext(), locations.get(0).getProvider(), Toast.LENGTH_SHORT).show();
                        newLocation(locations.get(0));
                        drawerLayout.closeDrawers();
                        break;
                    }
                    case 1: {
                        Toast.makeText(getApplicationContext(), locations.get(1).getProvider(), Toast.LENGTH_SHORT).show();
                        newLocation(locations.get(1));
                        drawerLayout.closeDrawers();
                        break;
                    }
                    case 2: {
                        Toast.makeText(getApplicationContext(), locations.get(2).getProvider(), Toast.LENGTH_SHORT).show();
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


    @Override
    public boolean onMarkerClick(Marker marker) {

        Location myLocation = gpsTracker.getLocation();

        LatLng latLngmyLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

        Toast.makeText(getBaseContext(), "Die Entfernung zu der Schnitzeljagd beträgt: " + sphericalUtil.computeDistanceBetween(latLngmyLocation, marker.getPosition()) + " Meter", Toast.LENGTH_SHORT).show();

        return false;
    }

    @Override
    public void onMyLocationChange(Location location) {


        if (!jagdaktive) {
            LatLng latLngmyLocation = new LatLng(location.getLatitude(), location.getLongitude());
            List<Mark> marks;
            Mark mark;
            for (int i = 0; i < paperchaselist.size(); i++) {
                final Paperchase paperchase = paperchaselist.get(i);
                marks = paperchase.getMarks();
                if (marks.size() > 0) {
                    mark = marks.get(0);

                    if (sphericalUtil.computeDistanceBetween(latLngmyLocation, new LatLng(mark.getLatitude(), mark.getLongitude())) < DISTANCE_TO_MARK) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder
                                .setTitle("Schnitzeljagd starten")
                                .setMessage("Soll die Schnitzeljagd gestartet werden?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Yes button clicked, do something

                                        jagdaktive = true;
                                        googleMap.clear();
                                        aktiveJagd = paperchase;
                                        Date date = new Date();
                                        Timestamp now = new Timestamp(date.getTime());
                                        paperchaseCompleted = new PaperchaseCompleted(loggedInUser, paperchase, now);

                                        List<Mark> markierungen = paperchase.getMarks();
                                        anzMarkierung = markierungen.size();
                                        for (int j = 0; j < anzMarkierung; j++) {

                                            MarkerOptions mo = new MarkerOptions();
                                            mo.position(markierungen.get(j).getPosition());
                                            mo.title((j + 1) + ". Wegpunkt");
                                            mo.snippet("Hinweis: " + markierungen.get(j).getHint());
                                            mo.draggable(false);

                                            Marker m = googleMap.addMarker(mo);
                                            paperchaseMarksLocation.add(m);

                                            if (j == 0) {
                                                m.showInfoWindow();
                                            }

                                        }

                                        googleMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(gpsTracker.getLocation().getLatitude(), gpsTracker.getLocation().getLongitude()))
                                                .draggable(true));
                                        Toast.makeText(getApplicationContext(), markierungen.get(aktuelleMarkierung).getHint(),
                                                Toast.LENGTH_SHORT).show();
                                        aktuelleMarkierung++;

                                    }
                                })
                                .setNegativeButton("Abbrechen", null)                        //Do nothing on no
                                .show();


                    }


                }
            }
        } else {
            LatLng latLngmyLocation = new LatLng(location.getLatitude(), location.getLongitude());
            double distanceToNextMark = 0.0;
            if (aktuelleMarkierung < anzMarkierung) {
                if (sphericalUtil.computeDistanceBetween(latLngmyLocation, aktiveJagd.getMarks().get(aktuelleMarkierung).getPosition()) < DISTANCE_TO_MARK) {
                    if (aktuelleMarkierung != anzMarkierung - 1) {
                        distanceToNextMark = sphericalUtil.computeDistanceBetween(aktiveJagd.getMarks().get(aktuelleMarkierung).getPosition(), aktiveJagd.getMarks().get(aktuelleMarkierung + 1).getPosition());
                    } else {
                        paperchaseFinished = true;
                    }
                    Toast.makeText(getApplicationContext(), "Hinweis: " + aktiveJagd.getMarks().get(aktuelleMarkierung).getHint() + "\n Entfernung zum nächsten Wegpunkt: " + distanceToNextMark + " in m",
                            Toast.LENGTH_SHORT).show();
                    String markSnippet = paperchaseMarksLocation.get(aktuelleMarkierung).getSnippet();
                    paperchaseMarksLocation.get(aktuelleMarkierung).setSnippet(markSnippet + "\n Entfernung zum nächsten Wegpunkt: " + distanceToNextMark + " in m");
                    paperchaseMarksLocation.get(aktuelleMarkierung).showInfoWindow();
                    aktuelleMarkierung++;
                }

            }
            if (paperchaseFinished) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setTitle("Schnitzeljagd beendet")
                        .setMessage("Bewertung abgeben!")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Yes button clicked, do something
                                Intent myIntent = new Intent(getApplicationContext(), PaperchaseReview.class);
                                myIntent.putExtra("PaperchaseID", aktiveJagd.getId());
                                startActivity(myIntent);
                            }
                        })
                        .setNegativeButton("Abbrechen", null)                        //Do nothing on no
                        .show();


            }
        }


    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {


        if (!jagdaktive) {
            LatLng latLngmyLocation = marker.getPosition();
            List<Mark> marks;
            Mark mark;
            for (int i = 0; i < paperchaselist.size(); i++) {
                final Paperchase paperchase = paperchaselist.get(i);
                marks = paperchase.getMarks();
                if (marks.size() > 0) {
                    mark = marks.get(0);

                    if (sphericalUtil.computeDistanceBetween(latLngmyLocation, new LatLng(mark.getLatitude(), mark.getLongitude())) < DISTANCE_TO_MARK) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder
                                .setTitle("Schnitzeljagd starten")
                                .setMessage("Soll die Schnitzeljagd gestartet werden?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Yes button clicked, do something

                                        jagdaktive = true;
                                        googleMap.clear();
                                        aktiveJagd = paperchase;
                                        Date date = new Date();
                                        Timestamp now = new Timestamp(date.getTime());
                                        paperchaseCompleted = new PaperchaseCompleted(loggedInUser, paperchase, now);

                                        List<Mark> markierungen = paperchase.getMarks();
                                        anzMarkierung = markierungen.size();
                                        for (int j = 0; j < anzMarkierung; j++) {

                                            MarkerOptions mo = new MarkerOptions();
                                            mo.position(markierungen.get(j).getPosition());
                                            mo.title((j + 1) + ". Wegpunkt");
                                            mo.snippet("Hinweis: " + markierungen.get(j).getHint());
                                            mo.draggable(false);

                                            Marker m = googleMap.addMarker(mo);
                                            paperchaseMarksLocation.add(m);

                                            if (j == 0) {
                                                m.showInfoWindow();
                                            }

                                        }

                                        googleMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(gpsTracker.getLocation().getLatitude(), gpsTracker.getLocation().getLongitude()))
                                                .draggable(true));
                                        Toast.makeText(getApplicationContext(), markierungen.get(aktuelleMarkierung).getHint(),
                                                Toast.LENGTH_SHORT).show();
                                        aktuelleMarkierung++;

                                    }
                                })
                                .setNegativeButton("Abbrechen", null)                        //Do nothing on no
                                .show();


                    }


                }
            }
        } else {
            LatLng latLngmyLocation = marker.getPosition();
            double distanceToNextMark = 0.0;
            if (aktuelleMarkierung < anzMarkierung) {
                if (sphericalUtil.computeDistanceBetween(latLngmyLocation, aktiveJagd.getMarks().get(aktuelleMarkierung).getPosition()) < DISTANCE_TO_MARK) {
                    if (aktuelleMarkierung != anzMarkierung - 1) {
                        distanceToNextMark = sphericalUtil.computeDistanceBetween(aktiveJagd.getMarks().get(aktuelleMarkierung).getPosition(), aktiveJagd.getMarks().get(aktuelleMarkierung + 1).getPosition());
                    } else {
                        paperchaseFinished = true;
                    }
                    Toast.makeText(getApplicationContext(), "Hinweis: " + aktiveJagd.getMarks().get(aktuelleMarkierung).getHint() + "\n Entfernung zum nächsten Wegpunkt: " + distanceToNextMark + " in m",
                            Toast.LENGTH_SHORT).show();
                    String markSnippet = paperchaseMarksLocation.get(aktuelleMarkierung).getSnippet();
                    paperchaseMarksLocation.get(aktuelleMarkierung).setSnippet(markSnippet + "\n Entfernung zum nächsten Wegpunkt: " + distanceToNextMark + " in m");
                    paperchaseMarksLocation.get(aktuelleMarkierung).showInfoWindow();
                    aktuelleMarkierung++;
                }

            }
            if (paperchaseFinished) {
                Date date = new Date();
                paperchaseCompleted.setEndTime(new Timestamp(date.getTime()));

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setTitle("Schnitzeljagd beendet")
                        .setMessage("Bewertung abgeben!")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Yes button clicked, do something
                                Intent myIntent = new Intent(getApplicationContext(), PaperchaseReview.class);
                                myIntent.putExtra("PaperchaseID", aktiveJagd.getId());
                                startActivity(myIntent);
                        }
                        })
                        .setNegativeButton("Abbrechen", null)                        //Do nothing on no
                        .show();


            }
        }
    }
}


