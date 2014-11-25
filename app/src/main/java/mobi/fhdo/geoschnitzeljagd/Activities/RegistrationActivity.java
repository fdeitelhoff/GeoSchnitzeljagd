package mobi.fhdo.geoschnitzeljagd.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import mobi.fhdo.geoschnitzeljagd.R;

public class RegistrationActivity extends Activity {
    private EditText registration_username;
    private EditText registration_password;
    private EditText registration_mail;

    protected void onCreate(Bundle savedInstanceState) {
        Button SignInButton;
        SignInButton = (Button) findViewById(R.id.registration_button);
        SignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickRegistration();
            }
        });
    }

    public void ClickRegistration() {
        // Weiterleitung an die Startseite
        // Muss später entfernt werden
        setContentView(R.layout.activity_home);

        //Login Logik muss hier implementiert werden
    }
}
