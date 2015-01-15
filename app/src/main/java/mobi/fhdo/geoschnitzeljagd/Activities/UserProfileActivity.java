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
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import mobi.fhdo.geoschnitzeljagd.Contexts.UserContext;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Users;
import mobi.fhdo.geoschnitzeljagd.R;

public class UserProfileActivity extends Activity {
    private Button deleteButton;
    private Button saveButton;
    private EditText usernameText;

    private Users users;

    private boolean isSaveAktion;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        users = new Users(this);

        usernameText = (EditText) findViewById(R.id.username);
        usernameText.setText(UserContext.getInstance().getLoggedInUser().getUsername());

        Log.d("UserId", UserContext.getInstance().getLoggedInUser().getId().toString());

        // Delete-Button
        deleteButton = (Button) findViewById(R.id.delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSaveAktion = false;

                // HTTP Anfrage um Benutzer zu löschen
                String stringUrl = "http://schnitzeljagd.fabiandeitelhoff.de/api/v1/user/" + UserContext.getInstance().getLoggedInUser().getId().toString();
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    new DownloadWebpageTask().execute(stringUrl);
                } else {
                    Log.d("No network connection available.", "No network connection available.");
                }
            }
        });


        // Save-Button
        saveButton = (Button) findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSaveAktion = true;
                // HTTP Anfrage um den Namen zu ändern
                String stringUrl = "http://schnitzeljagd.fabiandeitelhoff.de/api/v1/user";
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    new DownloadWebpageTask().execute(stringUrl);
                } else {
                    Log.d("No network connection available.", "No network connection available.");
                }
            }
        });

        context = this;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent myIntent = new Intent(this, HomeActivity.class);
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
            String encode = new String(Base64.encode((UserContext.getInstance().getLoggedInUser().getUsername() + ":" + UserContext.getInstance().getLoggedInUser().getPassword()).getBytes(), Base64.DEFAULT));
            conn.setRequestProperty("Authorization", "Basic " + encode);

            conn.setRequestProperty("Content-Type", "application/json");

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);

            conn.setDoInput(true);

            if (isSaveAktion) {
                // Update
                conn.setRequestMethod("PUT");
                conn.setDoOutput(true);
                try {
                    UserContext.getInstance().getLoggedInUser().setUsername(usernameText.getText().toString());
                    users.CreateOrUpdate(UserContext.getInstance().getLoggedInUser());
                    UserContext.getInstance().getLoggedInUser().objectToOutputStream(conn.getOutputStream());
                } catch (Exception e) {
                    //Toast.makeText(context, "Benutzername wurde nicht geändert! Versuchen Sie es später nochmal.", Toast.LENGTH_LONG).show();
                }
            } else {
                // Delete
                conn.setRequestMethod("DELETE");
            }

            conn.connect();
            int response = conn.getResponseCode();

            if (isSaveAktion) {
                if (response == 200) {
                    //Toast.makeText(context, "Benutzername wurde erfolgreich geändert!", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(context, "Benutzername wurde nicht geändert! Versuchen Sie es später nochmal.", Toast.LENGTH_LONG).show();
                }
            } else {
                // Delete
                if (response == 200) {
                    try {
                        users.Delete(UserContext.getInstance().getLoggedInUser());
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


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
        }
    }
}
