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
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import mobi.fhdo.geoschnitzeljagd.Contexts.UserContext;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Marks;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Paperchases;
import mobi.fhdo.geoschnitzeljagd.Model.Paperchase;
import mobi.fhdo.geoschnitzeljagd.Model.User;
import mobi.fhdo.geoschnitzeljagd.R;

public class HomeActivity extends Activity {
    private User loggedInUser;
    private Paperchases paperchases;
    private Marks marks;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        paperchases = new Paperchases(this);
        marks = new Marks(this);
        context = this;

        // Alle SChnitzeljadten vom Server holen!
        String stringUrl = "http://schnitzeljagd.fabiandeitelhoff.de/api/v1/paperchases";
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
            Log.d("No network connection available.", "No network connection available.");
        }


        //Paperchase g = paperchases.Get(UUID.fromString("c2fd1e18-2f7b-4933-b312-dfe2130b48bd"));
        //Paperchase h = paperchases.Get(UUID.fromString("5d6e455c-91ea-46d2-aadd-eb0ee0d2ee9b"));
        //Paperchase j = paperchases.Get(UUID.fromString("d5caf4ae-889a-11e4-ad4b-000c2905dce6"));

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

            conn.setRequestMethod("GET");

            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = UserContext.readIt(is);
            return contentAsString;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
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
                reader = new JsonReader(new InputStreamReader(input, "UTF-8"));

                // reader -> Paperchases Array
                readMessagesArray(reader);
            } catch (Exception e) {
                Log.d("Paperchases Array:", e.getMessage());
            } finally {
                try {
                    if (reader != null)
                        reader.close();
                } catch (Exception e) {
                    Log.d("Paperchases Array Close:", e.getMessage());
                }
            }
        }

        public List readMessagesArray(JsonReader reader) throws IOException {
            // Das Json Array auseinander nehmen!
            List messages = new ArrayList();

            try {
                // Alle vorhandenen Löschen!
                paperchases.DeleteAllPaperchases();
                marks.DeleteAllMarks();

                reader.beginArray();
                while (reader.hasNext()) {
                    // zur Datenbank hinzufügen
                    Paperchase buffer = Paperchase.jsonToObject(reader, context);
                    if (buffer != null) {
                        try {
                            paperchases.CreateOrUpdate(buffer);
                        } catch (Exception e) {
                            Log.d("DB Fehler:", e.getMessage());
                            break;
                        }
                    }

                }
                reader.endArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return messages;
        }
    }
}
