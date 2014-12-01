package mobi.fhdo.geoschnitzeljagd.Activities;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import mobi.fhdo.geoschnitzeljagd.DataManagers.GPSTracker;
import mobi.fhdo.geoschnitzeljagd.Model.Mark;
import mobi.fhdo.geoschnitzeljagd.R;
import mobi.fhdo.geoschnitzeljagd.adapter.TabsPagerAdapter;


/**
 * Created by JW on 26.11.2014.
 */
public class newPaperchaseCreateMapFragment extends Fragment implements GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {

    GoogleMap googleMap;
    Marker currentMarker;
    private ViewPager viewPager;
    private List<Marker> markers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_newpaperchase_map, container, false);

        viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
        markers = new ArrayList<Marker>();

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 1) {
                    if (markers != null && markers.size() > 0) {
                        for (int l = 0; l < markers.size(); l++) {
                            setCreatedMarker(markers.get(l), l);
                        }
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


        Button b_save_location = (Button) rootView.findViewById(R.id.b_save_location);
        b_save_location.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Die aktuelle Markierung der Activity übergeben.
                // Dann kann sie auch vom anderen Fragment genutzt werden.

                final newpaperchase activity = (newpaperchase) getActivity();

                //Mark mark = new Mark(currentMarker.getPosition().latitude, currentMarker.getPosition().longitude);
                Mark mark = new Mark(1.11, 2.22);
                activity.paperchase.addMark(mark);
                markers.add(currentMarker);

                TabsPagerAdapter adapter = (TabsPagerAdapter) viewPager.getAdapter();
                newPaperchaseCreateFragment fragment = (newPaperchaseCreateFragment) adapter.getItem(0);
                //fragment.chosenMarker = currentMarker;
                fragment.SetChosenMarker(currentMarker);
                // Perform action on click
                viewPager.setCurrentItem(0);

            }
        });

        Button b_refresh_location = (Button) rootView.findViewById(R.id.b_refresh_location);
        b_refresh_location.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                initLocation();
            }
        });

        // TODO: Von mir (Fabian) rausgenommen. Habe immer noch nicht die Maps am Laufen.
        //createMapView();
        //initLocation();

        return rootView;

    }

    @Override
    public void onResume() {

        if (markers != null && markers.size() > 0) {
            for (int i = 0; i < markers.size(); i++) {
                setCreatedMarker(markers.get(i), i);
            }
        }
        super.onResume();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.i("GoogleMapActivity", "onMarkerClick");
        Toast.makeText(getActivity(),
                "Marker Clicked: " + marker.getTitle() + marker.getPosition(), Toast.LENGTH_LONG)
                .show();
        currentMarker = marker;

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


                googleMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(
                        R.id.mapView)).getMap();
                /*
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                        R.id.mapView)).getMap();
                */
                /**
                 * If the map is still null after attempted initialisation,
                 * show an error to the user
                 */
                googleMap.setOnMapClickListener(this);
                googleMap.setOnMarkerDragListener(this);


                if (null == googleMap) {
                    Toast.makeText(getActivity(),
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

        GPSTracker gpsTracker = new GPSTracker(getActivity());
        Location location = gpsTracker.getLocation();
        if (currentMarker != null) {
            currentMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        } else {
            currentMarker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .title("aktueller Standort")
                    .draggable(true));
        }

        Log.d("aktuelles Standort", "aktuelles Standort: " + "Lat: " + location.getLatitude() + "Long: " + location.getLongitude());

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(location.getLatitude(), location.getLongitude())), 15));
    }

    private void setCreatedMarker(Marker m, int i) {

        //m.setPosition(latLng);
        m = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(m.getPosition().latitude, m.getPosition().longitude))
                .title("" + i)
                .draggable(false));
        m.showInfoWindow();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(m.getPosition().latitude, m.getPosition().longitude)), 15));
    }

    private void newLocation(Location l) {
        createMapView();
        currentMarker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(l.getLatitude(), l.getLongitude()))
                .draggable(false));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(l.getLatitude(), l.getLongitude())), 15));


    }


    @Override
    public void onMapClick(LatLng latLng) {

    }
}