package mobi.fhdo.geoschnitzeljagd.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import mobi.fhdo.geoschnitzeljagd.Contexts.UserContext;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Users;
import mobi.fhdo.geoschnitzeljagd.Model.User;
import mobi.fhdo.geoschnitzeljagd.R;

public class RegistrationActivity extends Activity {
    private EditText registration_username;
    private EditText registration_password;

    private Users users;

    private User newUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        users = new Users(this);

        registration_username = (EditText) findViewById(R.id.registration_username);
        registration_password = (EditText) findViewById(R.id.registration_password);

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
        try {
            String username = registration_username.getText().toString();
            String password = registration_password.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getBaseContext(), "Für die Registrierung sind Benutzername und Passwort notwendig.", Toast.LENGTH_LONG).show();
            }

            newUser = new User(username, UserContext.getMd5(password));

            String stringUrl = "http://schnitzeljagd.fabiandeitelhoff.de/api/v1/register";
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                // User Lokal anlegen
                if (users.CreateOrUpdate(newUser)) {
                    UserContext.getInstance().userLogin(users.Login(newUser));

                    // User auf dem Server hinterlegen
                    new DownloadWebpageTask().execute(stringUrl);

                    new AlertDialog.Builder(this)
                            .setTitle("Registrierung erfolgreich")
                            .setMessage("Du wurdest nun automatisch angemeldet.")
                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                                    startActivity(intent);

                                    finish();
                                }
                            })
                            .show();
                } else {
                    Toast.makeText(getBaseContext(), "Fehler", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getBaseContext(), "No network connection available.", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);

                finish();
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);

            // Create 201 erfolg + user
            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            newUser.objectToOutputStream(conn.getOutputStream());

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
            Log.d("Test:", result);
            try {
                InputStream input = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));

                JsonReader reader = new JsonReader(new InputStreamReader(input, "UTF-8"));

                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("data")) {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            name = reader.nextName();
                            if (name.equals("Timestamp")) {
                                try {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                    Date parsedTimeStamp = dateFormat.parse(reader.nextString());
                                    newUser.setTimestamp(new Timestamp(parsedTimeStamp.getTime()));

                                    users.CreateOrUpdate(newUser);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                reader.skipValue();
                            }
                        }
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
