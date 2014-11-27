package mobi.fhdo.geoschnitzeljagd.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import mobi.fhdo.geoschnitzeljagd.DataManagers.Paperchases;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Users;
import mobi.fhdo.geoschnitzeljagd.Model.Exceptions.UserNotExistsException;
import mobi.fhdo.geoschnitzeljagd.Model.Paperchase;
import mobi.fhdo.geoschnitzeljagd.Model.User;
import mobi.fhdo.geoschnitzeljagd.R;

public class PaperchaseListActivity extends Activity implements AdapterView.OnItemClickListener { // extends ActionBarActivity {

    private ListView paperchasesListView;

    private Paperchases paperchases;
    private List<Paperchase> ownPaperchases;

    private Users users;
    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_paperchase_list);

            paperchasesListView = (ListView) findViewById(R.id.paperchasesListView);
            paperchasesListView.setOnItemClickListener(this);

            users = new Users(this);
            paperchases = new Paperchases(this);

            // Die UserID ermitteln.
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                Integer id = extras.getInt("UserID");

                loggedInUser = users.Get(id);
            }

            ownPaperchases = paperchases.Own(loggedInUser);

            paperchasesListView.setAdapter(new ArrayAdapter<Paperchase>(
                    this,
                    android.R.layout.simple_list_item_activated_1,
                    android.R.id.text1,
                    ownPaperchases));

        } catch (UserNotExistsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.w("item", position + "");

        Paperchase selectedPaperchase = (Paperchase) paperchasesListView.getAdapter().getItem(position);

        Intent detailIntent = new Intent(this, PaperchaseDetailActivity_alt.class);
        detailIntent.putExtra("PaperchaseId", selectedPaperchase);
        startActivity(detailIntent);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_paperchase_list, menu);
        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
