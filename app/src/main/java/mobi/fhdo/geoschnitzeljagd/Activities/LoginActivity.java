package mobi.fhdo.geoschnitzeljagd.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.JsonWriter;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import mobi.fhdo.geoschnitzeljagd.Contexts.UserContext;
import mobi.fhdo.geoschnitzeljagd.DataManagers.DataManager;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Users;
import mobi.fhdo.geoschnitzeljagd.Model.Exceptions.UserLoginException;
import mobi.fhdo.geoschnitzeljagd.Model.User;
import mobi.fhdo.geoschnitzeljagd.R;

public class LoginActivity extends Activity
{
    private AutoCompleteTextView usernameView;
    private EditText passwordView;

    private Users users;
    private DataManager dataManager;


    // Given a URL, establishes an HttpUrlConnection and retrieves
// the web page content as a InputStream, which it returns as
// a string.
    private String downloadUrl(String myurl) throws IOException
    {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try
        {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String encode = new String(Base64.encode("marcel:test".getBytes(), Base64.DEFAULT));
            conn.setRequestProperty("Authorization", "Basic " + encode);

            conn.setRequestProperty("Content-Type","application/json");

            Log.d("Test",encode);

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);



            // Holen
            //conn.setRequestMethod("GET");
            // Update
            //conn.setRequestMethod("PUT");
            // Create
            conn.setRequestMethod("POST");
            // Delete
            //conn.setRequestMethod("DELETE");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStreamWriter bw = new OutputStreamWriter(conn.getOutputStream());
            JsonWriter writer = new JsonWriter(bw);

            writer.beginObject();
            writer.name("name").value("test");
            writer.endObject();
            writer.flush();
            bw.flush();
            writer.close();
            bw.close();


            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();


            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        }
        finally
        {
            if (is != null)
            {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException
    {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {

            // params comes from the execute() call: params[0] is the url.
            try
            {
                return downloadUrl(urls[0]);
            }
            catch (IOException e)
            {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result)
        {
            Log.d("Test:", result);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        String stringUrl = "http://schnitzeljagd.fabiandeitelhoff.de/api/v1/paperchases/22";
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
        {
            new DownloadWebpageTask().execute(stringUrl);
        } else
        {
            Log.d("No network connection available.", "No network connection available.");
        }


        dataManager = new DataManager(this);
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
            User user = new User(usernameView.getText().toString(),
                    passwordView.getText().toString());

            UserContext.getInstance().userLoggedIn(users.Login(user));

            Intent intent = new Intent(getBaseContext(), HomeActivity.class);
            startActivity(intent);
        }
        catch (UserLoginException e)
        {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
