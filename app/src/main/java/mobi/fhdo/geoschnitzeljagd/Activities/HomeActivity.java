package mobi.fhdo.geoschnitzeljagd.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import mobi.fhdo.geoschnitzeljagd.Contexts.UserContext;
import mobi.fhdo.geoschnitzeljagd.Model.User;
import mobi.fhdo.geoschnitzeljagd.R;

public class HomeActivity extends Activity {
    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        loggedInUser = UserContext.getInstance().getLoggedInUser();

        TextView userName = (TextView) findViewById(R.id.textViewUser);
        userName.setText("Hallo '" + loggedInUser.getUsername() + "'!");

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
                startActivity(intent);
            }
        });

        // New paperchase.
        Button newPaperchase = (Button) findViewById(R.id.button_new_paperchases);
        newPaperchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PaperchaseActivity.class);
                startActivity(intent);
            }
        });

        // Logout button.
        Button logout = (Button) findViewById(R.id.button_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserContext.getInstance().userLogout();

                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                startActivity(intent);
                finish();
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
    }
}
