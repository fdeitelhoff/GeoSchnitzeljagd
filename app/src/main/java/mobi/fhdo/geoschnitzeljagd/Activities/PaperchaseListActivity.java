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
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.swipedismiss.SwipeDismissListViewTouchListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Comparator;
import java.util.List;

import mobi.fhdo.geoschnitzeljagd.Contexts.UserContext;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Marks;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Paperchases;
import mobi.fhdo.geoschnitzeljagd.Model.Paperchase;
import mobi.fhdo.geoschnitzeljagd.Model.User;
import mobi.fhdo.geoschnitzeljagd.R;

public class PaperchaseListActivity extends Activity implements AdapterView.OnItemClickListener
{

    private ListView paperchasesListView;

    private Paperchases paperchases;
    private Marks marks;
    private List<Paperchase> ownPaperchases;
    private ArrayAdapter dataAdapter;

    private Paperchase toBeDeletedPaperchase;

    private int lastPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paperchase_list);

        paperchasesListView = (ListView) findViewById(R.id.paperchasesListView);
        paperchasesListView.setOnItemClickListener(this);

        paperchases = new Paperchases(this);
        marks = new Marks(this);

        User loggedInUser = UserContext.getInstance().getLoggedInUser();

        ownPaperchases = paperchases.own(loggedInUser);

        dataAdapter = new ArrayAdapter<Paperchase>(this,
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                ownPaperchases);

        paperchasesListView.setAdapter(dataAdapter);

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        paperchasesListView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks()
                        {
                            @Override
                            public boolean canDismiss(int position)
                            {
                                return true;
                            }

                            public void onDismiss(ListView listView, final int[] reverseSortedPositions)
                            {
                                new AlertDialog.Builder(PaperchaseListActivity.this)
                                        .setTitle("Schnitzeljagd löschen")
                                        .setMessage("Soll die Schnitzeljagd gelöscht werden?")
                                        .setPositiveButton("Ja", new DialogInterface.OnClickListener()
                                        {
                                            public void onClick(DialogInterface dialog, int whichButton)
                                            {
                                                for (int position : reverseSortedPositions)
                                                {
                                                    toBeDeletedPaperchase = ownPaperchases.get(position);

                                                    // TODO: Bitte Prüfen
                                                    // HTTP Anfrage um die SChnitzeljadt zu löschen
                                                    String stringUrl = "http://schnitzeljagd.fabiandeitelhoff.de/api/v1/paperchase/" + toBeDeletedPaperchase.getId();
                                                    ConnectivityManager connMgr = (ConnectivityManager)
                                                            getSystemService(Context.CONNECTIVITY_SERVICE);
                                                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                                                    if (networkInfo != null && networkInfo.isConnected())
                                                    {
                                                        new DownloadWebpageTask().execute(stringUrl);
                                                        // Die lokale Löschung wird bei erfolgreichem Connect durchgeführt
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(getBaseContext(), "No network connection available.", Toast.LENGTH_LONG).show();
                                                    }
                                                }

                                                dataAdapter.notifyDataSetChanged();
                                            }
                                        })
                                        .setNegativeButton("Nein", new DialogInterface.OnClickListener()
                                        {
                                            public void onClick(DialogInterface dialog, int whichButton)
                                            {
                                            }
                                        })
                                        .show();
                            }
                        });
        paperchasesListView.setOnTouchListener(touchListener);
        paperchasesListView.setOnScrollListener(touchListener.makeScrollListener());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        lastPosition = position;

        Paperchase selectedPaperchase = (Paperchase) paperchasesListView.getAdapter().getItem(position);

        selectedPaperchase = marks.forPaperchase(selectedPaperchase);

        Intent editPaperchaseIntent = new Intent(this, PaperchaseActivity.class);
        editPaperchaseIntent.putExtra("Paperchase", selectedPaperchase);
        startActivityForResult(editPaperchaseIntent, 1);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_paperchase_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.strings_activity_paperchase_list:
                Intent newPaperchaseIntent = new Intent(this, PaperchaseActivity.class);
                startActivityForResult(newPaperchaseIntent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                Paperchase paperchase = (Paperchase) data.getSerializableExtra("Paperchase");

                if (lastPosition != -1)
                {
                    ownPaperchases.set(lastPosition, paperchase);
                    dataAdapter.notifyDataSetChanged();
                }
                else
                {
                    dataAdapter.add(paperchase);
                }

                dataAdapter.sort(new Comparator<Paperchase>()
                {
                    @Override
                    public int compare(Paperchase lhs, Paperchase rhs)
                    {
                        return lhs.getName().compareToIgnoreCase(rhs.getName());
                    }
                });
            }
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            try
            {
                return downloadUrl(urls[0]);
            }
            catch (IOException e)
            {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
    }

    private String downloadUrl(String myurl) throws IOException
    {
        InputStream is = null;

        try
        {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String encode = new String(Base64.encode((UserContext.getInstance().getLoggedInUser().getUsername() + ":" + UserContext.getInstance().getLoggedInUser().getPassword()).getBytes(), Base64.DEFAULT));
            conn.setRequestProperty("Authorization", "Basic " + encode);

            conn.setRequestProperty("Content-Type", "application/json");

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);

            conn.setDoInput(true);

            // Delete
            conn.setRequestMethod("DELETE");

            conn.connect();
            int response = conn.getResponseCode();

            if (response == 200)
            {
                try
                {
                    if(toBeDeletedPaperchase!=null)
                    {
                        paperchases.remove(toBeDeletedPaperchase);
                        ownPaperchases.remove(toBeDeletedPaperchase);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            is = conn.getInputStream();
            String contentAsString = UserContext.readIt(is);
            return contentAsString;
        }

        finally
        {
            if (is != null)
            {
                is.close();
            }
        }
    }

}
