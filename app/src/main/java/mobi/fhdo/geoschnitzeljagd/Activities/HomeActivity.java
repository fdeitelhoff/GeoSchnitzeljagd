package mobi.fhdo.geoschnitzeljagd.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import mobi.fhdo.geoschnitzeljagd.DataManagers.Users;
import mobi.fhdo.geoschnitzeljagd.Model.User;
import mobi.fhdo.geoschnitzeljagd.R;

public class HomeActivity extends Activity {
    private Users users;
    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        users = new Users(this);

        // Den übergebenen User ermitteln.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            loggedInUser = (User) extras.getSerializable("User");

            TextView userName = (TextView) findViewById(R.id.textViewUser);
            userName.setText("Hallo '" + loggedInUser.getUsername() + "'!");
        }

        // TODO: Nur ein Beispiel für das Anlegen von einer Schnitzeljagd und drei zugehörigen Markierungen.
        // Rausnehmen, wenn kein Bedarf mehr ist.
        /*Paperchase p = new Paperchase(loggedInUser, "neues paperchase");
        p.addMark(new Mark(1.11, 1.22, "Hinweis 1", 1));
        p.addMark(new Mark(2.22, 3.33, "Hinweis 2", 2));
        p.addMark(new Mark(3.33, 4.44, "Hinweis 3", 3));
        new Paperchases(this).Create(p);*/

        // User Profile Button.
        Button userProfile = (Button) findViewById(R.id.buton_user_profile);
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), UserProfileActivity.class);
                startActivity(myIntent);
            }
        });

        // Settings Button.
        Button settings = (Button) findViewById(R.id.button_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), SettingsActivity.class);
                startActivity(myIntent);
            }
        });

        // Search Button.
        Button search = (Button) findViewById(R.id.button_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), SearchActivity.class);
                startActivity(myIntent);
            }
        });

        // Nearby Button.
        Button nearby = (Button) findViewById(R.id.button_nearby);
        nearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), NearbyActivity.class);
                startActivity(myIntent);
            }
        });

        // Eigene Schnitzeljagd button.
        Button ownPaperchases = (Button) findViewById(R.id.button_own_paperchases);
        ownPaperchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PaperchaseListActivity.class);
                intent.putExtra("User", loggedInUser);
                startActivity(intent);
            }
        });

        // Exit button.
        Button exit = (Button) findViewById(R.id.button_exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        Button newPaperchase = (Button) findViewById(R.id.button_new_paperchases);
        newPaperchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), newpaperchase.class);
                intent.putExtra("User", loggedInUser);
                startActivity(intent);
            }
        });
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }*/
}
