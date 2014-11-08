package mobi.fhdo.geoschnitzeljagd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import mobi.fhdo.geoschnitzeljagd.DataManagers.Users;
import mobi.fhdo.geoschnitzeljagd.Model.Exceptions.UserNotExistsException;
import mobi.fhdo.geoschnitzeljagd.Model.User;

public class HomeActivity extends Activity {

    private Users users;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);

            users = new Users(this);

            // Die UserID ermitteln.
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                Integer id = extras.getInt("UserID");

                user = users.Get(id);

                TextView userName = (TextView) findViewById(R.id.textViewUser);
                userName.setText("Hallo '" + user.getUsername() + "'!");
            }

            // User Profile Button.
            Button userProfile = (Button) findViewById(R.id.buton_user_profile);
            userProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setContentView(R.layout.activity_user_profile);
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

        } catch (UserNotExistsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
}
