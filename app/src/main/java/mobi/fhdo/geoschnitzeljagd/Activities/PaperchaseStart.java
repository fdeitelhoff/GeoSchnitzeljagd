package mobi.fhdo.geoschnitzeljagd.Activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.UUID;

import mobi.fhdo.geoschnitzeljagd.DataManagers.DataManager;
import mobi.fhdo.geoschnitzeljagd.DataManagers.GPSTracker;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Marks;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Paperchases;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Users;
import mobi.fhdo.geoschnitzeljagd.Model.Exceptions.UserLoginException;
import mobi.fhdo.geoschnitzeljagd.Model.Mark;
import mobi.fhdo.geoschnitzeljagd.Model.Paperchase;
import mobi.fhdo.geoschnitzeljagd.Model.User;
import mobi.fhdo.geoschnitzeljagd.R;

public class PaperchaseStart extends Activity
{
    private Button ratingButton;

    private UUID id;
    private User loggedInUser;

    GoogleMap googleMap;

    private GPSTracker gpsTracker = new GPSTracker(this);

    private Paperchase aktuellePaperchase;
    private List<Mark> aktuellePaperchaseMarks;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paperchase_start);

        createMapView();

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            id = (UUID) extras.getSerializable("PaperchaseID");
            Log.w("Id", String.valueOf(id));
            loggedInUser = (User) extras.getSerializable("User");

            //Toast.makeText(this, id, Toast.LENGTH_SHORT).show();

            Paperchases paperchases = new Paperchases(this);
            Marks marks = new Marks(this);

            aktuellePaperchase = paperchases.Get(id);
            aktuellePaperchase = marks.forPaperchase(aktuellePaperchase);


            if (aktuellePaperchase != null){
               aktuellePaperchaseMarks =  aktuellePaperchase.getMarks();
                for(int i = 0; i<aktuellePaperchaseMarks.size(); i++){
                    Mark m = aktuellePaperchaseMarks.get(i);
                    Marker mark = googleMap.addMarker(new MarkerOptions()
                    .position(m.getPosition())
                    .title(i+1 + ". Wegpunkt")
                    .snippet("Hinweis: " + m.getHint() )
                    .draggable(false));
                    mark.setVisible(false);

                    if(i == 0){
                        mark.setVisible(true);
                        mark.showInfoWindow();
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mark.getPosition(), 15));
                    }
                }


            }


        }

        ratingButton = (Button) findViewById(R.id.review_button);
        ratingButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(view.getContext(), PaperchaseReview.class);
                intent.putExtra("PaperchaseID", id);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            Intent myIntent = new Intent(this, SearchActivity.class);
            myIntent.putExtra("User", loggedInUser);
            startActivity(myIntent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void initLocation() {
        Location location = gpsTracker.getLocation();
        if(location != null){
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .title("aktueller Standort")
                    .draggable(true));

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(location.getLatitude(), location.getLongitude())), 15));

        }

    }

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




}
