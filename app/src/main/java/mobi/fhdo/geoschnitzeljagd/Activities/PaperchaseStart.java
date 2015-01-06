package mobi.fhdo.geoschnitzeljagd.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
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
import mobi.fhdo.geoschnitzeljagd.Model.PaperchaseCompleted;
import mobi.fhdo.geoschnitzeljagd.Model.User;
import mobi.fhdo.geoschnitzeljagd.R;

public class PaperchaseStart extends Activity implements GoogleMap.OnMyLocationChangeListener
{
    private Button ratingButton;

    private UUID id;
    private User loggedInUser;

    GoogleMap googleMap;

    private GPSTracker gpsTracker = new GPSTracker(this);

    private Paperchase aktuellePaperchase;
    private List<Mark> aktuellePaperchaseMarks;

    private List<Marker> paperchaseMarksLocation = new ArrayList<Marker>();
    private Paperchase aktiveJagd;
    private PaperchaseCompleted paperchaseCompleted;
    private boolean jagdaktive;
    private double DISTANCE_TO_MARK = 20.0;
    private SphericalUtil sphericalUtil;
    private int aktuelleMarkierung;
    private int anzMarkierung;
    private boolean paperchaseFinished;
    private boolean bewertungAbgegeben;

    TextView distance2NextMark;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paperchase_start);

        distance2NextMark = (TextView) findViewById(R.id.textView_distanceToNextMark);
        ratingButton = (Button) findViewById(R.id.review_button);
        ratingButton.setClickable(false);
        aktuelleMarkierung = 0;
        paperchaseFinished = false;

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
                anzMarkierung = aktuellePaperchaseMarks.size();
                for(int i = 0; i<aktuellePaperchaseMarks.size(); i++){
                    Mark m = aktuellePaperchaseMarks.get(i);
                    Log.w("Paperchase Suche", "Markierungen: Lat:" + m.getPosition().latitude + " Long: "+ m.getPosition().longitude);
                    Marker mark = googleMap.addMarker(new MarkerOptions()
                    .position(m.getPosition())
                    .title(i+1 + ". Wegpunkt")
                    .snippet("Hinweis: " + m.getHint() )
                    .draggable(false));
                    //mark.setVisible(false);

                    if(i == 0){
                        mark.setVisible(true);
                        mark.showInfoWindow();
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mark.getPosition(), 15));
                    }
                    paperchaseMarksLocation.add(mark);
                }

                startHunt(aktuellePaperchase);


            }


        }


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

    public void startHunt(final Paperchase paperchase){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Schnitzeljagd starten")
                .setMessage("Soll die Schnitzeljagd gestartet werden?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Yes button clicked, do something
                        jagdaktive = true;
                        aktiveJagd = paperchase;
                        Date date = new Date();
                        Timestamp now = new Timestamp(date.getTime());
                        paperchaseCompleted = new PaperchaseCompleted(loggedInUser, paperchase, now);
                    }
                })
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        jagdaktive = false;
                    }
                })                        //Do nothing on no
                .show();


    }

    @Override
    public void onMyLocationChange(Location location) {

        if (jagdaktive) {
            LatLng latLngmyLocation = new LatLng(location.getLatitude(), location.getLongitude());
            double displayDistanceMyNextMarker = sphericalUtil.computeDistanceBetween(latLngmyLocation, aktiveJagd.getMarks().get(aktuelleMarkierung).getPosition());
            distance2NextMark.setText("Entfernung zw. Standort und nächster Markierung:" +  new DecimalFormat("#.##").format(displayDistanceMyNextMarker));
            double distanceToNextMark = 0.0;
            if (aktuelleMarkierung < anzMarkierung) {
                if (sphericalUtil.computeDistanceBetween(latLngmyLocation, aktiveJagd.getMarks().get(aktuelleMarkierung).getPosition()) < DISTANCE_TO_MARK) {
                    paperchaseMarksLocation.get(aktuelleMarkierung).setVisible(true);
                    if (aktuelleMarkierung != anzMarkierung - 1) {
                        distanceToNextMark = sphericalUtil.computeDistanceBetween(aktiveJagd.getMarks().get(aktuelleMarkierung).getPosition(), aktiveJagd.getMarks().get(aktuelleMarkierung + 1).getPosition());
                        distance2NextMark.setText("Entfernung zum nächsten Wegpunkt: " + distanceToNextMark);
                    } else {
                        paperchaseFinished = true;
                    }
                    Toast.makeText(getApplicationContext(), "Hinweis: " + aktiveJagd.getMarks().get(aktuelleMarkierung).getHint() + "\n Entfernung zum nächsten Wegpunkt: " + new DecimalFormat("#.##").format(distanceToNextMark) + " in m",
                            Toast.LENGTH_SHORT).show();
                    String markSnippet = paperchaseMarksLocation.get(aktuelleMarkierung).getSnippet();
                    paperchaseMarksLocation.get(aktuelleMarkierung).setSnippet(markSnippet + "\n Entfernung zum nächsten Wegpunkt: " + new DecimalFormat("#.##").format(distanceToNextMark) + " in m");
                    paperchaseMarksLocation.get(aktuelleMarkierung).showInfoWindow();
                    aktuelleMarkierung++;
                }

            }
            if (paperchaseFinished && !bewertungAbgegeben) {
                distance2NextMark.setVisibility(View.INVISIBLE);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setTitle("Schnitzeljagd beendet")
                        .setMessage("Bewertung abgeben!")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Yes button clicked, do something
                                bewertungAbgegeben = true;
                                ratingButton.setClickable(true);
                                Intent myIntent = new Intent(getApplicationContext(), PaperchaseReview.class);
                                myIntent.putExtra("PaperchaseID", aktiveJagd.getId());
                                startActivity(myIntent);
                            }
                        })
                        .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                bewertungAbgegeben = true;
                            }
                        })                        //Do nothing on no
                        .show();

            }
        }


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
