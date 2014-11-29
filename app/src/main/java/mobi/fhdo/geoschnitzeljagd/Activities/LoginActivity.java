package mobi.fhdo.geoschnitzeljagd.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import mobi.fhdo.geoschnitzeljagd.DataManagers.DataManager;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Users;
import mobi.fhdo.geoschnitzeljagd.Model.Exceptions.UserLoginException;
import mobi.fhdo.geoschnitzeljagd.Model.User;
import mobi.fhdo.geoschnitzeljagd.R;

public class LoginActivity extends Activity
{
    // UI references.
    private AutoCompleteTextView usernameView;
    private EditText passwordView;

    private Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        DataManager dataManager = new DataManager(this);
        users = new Users(this);

        // Set up the login form.
        usernameView = (AutoCompleteTextView) findViewById(R.id.username);

        passwordView = (EditText) findViewById(R.id.password);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                if (id == R.id.login || id == EditorInfo.IME_NULL)
                {
                    ClickLogin();
                    return true;
                }
                return false;
            }
        });

        // Login-Button
        Button SignInButton;
        SignInButton = (Button) findViewById(R.id.sign_in_button);
        SignInButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ClickLogin();
            }
        });

        // Registration-Button
        Button RegistrationButton;
        RegistrationButton = (Button) findViewById(R.id.registration_button);
        RegistrationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(view.getContext(), RegistrationActivity.class);
                startActivity(intent);
            }

        });
    }

    public void ClickLogin()
    {
        try
        {
            // Login versuchen und im positiven Fall zur home activity wechseln.
            User user = new User(usernameView.getText().toString(),
                    passwordView.getText().toString());

            // Das User Objekt noch speichern. Brauchen wir in vielen Activities.
            User loggedInUser = users.Login(user);

            // Die User-ID übergeben, um den User auf anderen Aktivitäten wieder ermitteln zu können.
            Intent intent = new Intent(getBaseContext(), HomeActivity.class);
            intent.putExtra("User", loggedInUser);
            startActivity(intent);
        }
        catch (UserLoginException e)
        {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}