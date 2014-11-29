package mobi.fhdo.geoschnitzeljagd.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mobi.fhdo.geoschnitzeljagd.DataManagers.DataManager;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Users;
import mobi.fhdo.geoschnitzeljagd.Model.Exceptions.UserLoginException;
import mobi.fhdo.geoschnitzeljagd.Model.User;
import mobi.fhdo.geoschnitzeljagd.R;

public class RegistrationActivity extends Activity
{
    private EditText registration_username;
    private EditText registration_password;

    private Users users;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        users = new Users(this);

        registration_username = (EditText) findViewById(R.id.registration_username);
        registration_password = (EditText) findViewById(R.id.registration_password);

        Button SignInButton;
        SignInButton = (Button) findViewById(R.id.registration_button);
        SignInButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ClickRegistration();
            }
        });
    }

    public void ClickRegistration()
    {
        try
        {
            User user = new User(registration_username.getText().toString(), registration_password.getText().toString());

            if (users.Create(user))
            {
                Toast.makeText(getBaseContext(), "Erfolgreich", Toast.LENGTH_LONG).show();
            } else
            {
                Toast.makeText(getBaseContext(), "Fehler", Toast.LENGTH_LONG).show();
            }


            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
        }
        catch (Exception e)
        {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
