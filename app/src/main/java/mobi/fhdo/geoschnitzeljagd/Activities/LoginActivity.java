package mobi.fhdo.geoschnitzeljagd.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import mobi.fhdo.geoschnitzeljagd.Contexts.UserContext;
import mobi.fhdo.geoschnitzeljagd.DataManagers.DataManager;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Users;
import mobi.fhdo.geoschnitzeljagd.Model.Exceptions.UserLoginException;
import mobi.fhdo.geoschnitzeljagd.Model.User;
import mobi.fhdo.geoschnitzeljagd.R;

public class LoginActivity extends Activity {
    private AutoCompleteTextView usernameView;
    private EditText passwordView;

    private Users users;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // HTTP Anfrage um alle Benutzer zu bekommen
        String stringUrl = "http://schnitzeljagd.fabiandeitelhoff.de/api/v1/users";
        //String stringUrl = "http://schnitzeljagd.fabiandeitelhoff.de/schnitzeljagd/api/v1/echo-body";
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
            Log.d("No network connection available.", "No network connection available.");
        }

        dataManager = new DataManager(this);
        users = new Users(this);

        // Set up the login form.
        usernameView = (AutoCompleteTextView) findViewById(R.id.username);

        passwordView = (EditText) findViewById(R.id.password);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    ClickLogin();
                    return true;
                }
                return false;
            }
        });

        // Login-Button
        Button SignInButton;
        SignInButton = (Button) findViewById(R.id.sign_in_button);
        SignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickLogin();
            }
        });

        // Registration-Button
        Button RegistrationButton;
        RegistrationButton = (Button) findViewById(R.id.registration_button);
        RegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RegistrationActivity.class);
                startActivity(intent);
            }

        });
    }

    public void ClickLogin() {
        try {
            User user = new User(usernameView.getText().toString(), UserContext.getMd5(passwordView.getText().toString()));

            UserContext.getInstance().userLogin(users.Login(user));

            Intent intent = new Intent(getBaseContext(), HomeActivity.class);
            startActivity(intent);

            finish();
        } catch (UserLoginException e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);


            // Holen
            conn.setRequestMethod("GET");
            // Update
            //conn.setRequestMethod("PUT");
            // Create 201 erfolg + user
            //conn.setRequestMethod("POST");
            // Delete
            //conn.setRequestMethod("DELETE");

            conn.setDoInput(true);


            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();


            // Convert the InputStream into a string
            String contentAsString = UserContext.readIt(is);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String result) {
            // Hier bekommt man das Ergebnis der HTTP anfrage
            Log.d("Test:", result);
            JsonReader reader = null;
            try {
                // String to JsonReader
                InputStream input = new ByteArrayInputStream(result.getBytes());
                // TODO: ich musste das folgende rausnehmen, da sonst die Anwendung bei mir nicht Lief
                //StandardCharsets.UTF_8

                reader = new JsonReader(new InputStreamReader(input, "UTF-8"));

                // reader -> User Array
                readMessagesArray(reader);
            } catch (Exception e) {
                Log.d("Exception1:", e.getStackTrace().toString());
            } finally {
                try {
                    if (reader != null)
                        reader.close();
                } catch (Exception e) {
                    Log.d("Exception2:", e.getStackTrace().toString());
                }
            }
        }

        public List readMessagesArray(JsonReader reader) throws IOException {
            // Das Json Array auseinander nehmen!
            List messages = new ArrayList();

            try {
                users.DeleteAllUsers();
            } catch (Exception e) {
                e.printStackTrace();
            }

            reader.beginArray();
            while (reader.hasNext()) {

                // User zur Datenbank hinzufügen
                User buffer = User.jsonToObject(reader);
                if (buffer != null) {
                    try {
                        users.CreateOrUpdate(buffer);
                    } catch (Exception e) {
                        Log.d("DB Fehler:", e.getMessage());
                        break;
                    }
                }

            }
            reader.endArray();
            return messages;
        }
    }
}
