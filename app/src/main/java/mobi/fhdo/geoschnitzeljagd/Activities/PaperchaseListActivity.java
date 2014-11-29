package mobi.fhdo.geoschnitzeljagd.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.swipedismiss.SwipeDismissListViewTouchListener;

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
    private ArrayAdapter dataAdapter;

    private Users users;
    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paperchase_list);

        paperchasesListView = (ListView) findViewById(R.id.paperchasesListView);
        paperchasesListView.setOnItemClickListener(this);

        users = new Users(this);
        paperchases = new Paperchases(this);

        // Die UserID ermitteln.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            loggedInUser = (User) extras.getSerializable("User");
        }

        ownPaperchases = paperchases.Own(loggedInUser);
        dataAdapter = new ArrayAdapter<Paperchase>(this,
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                ownPaperchases);

        paperchasesListView.setAdapter(dataAdapter);

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        paperchasesListView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    ownPaperchases.remove(position);
                                }

                                dataAdapter.notifyDataSetChanged();
                            }
                        });
        paperchasesListView.setOnTouchListener(touchListener);
        paperchasesListView.setOnScrollListener(touchListener.makeScrollListener());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.w("item", position + "");

        Paperchase selectedPaperchase = (Paperchase) paperchasesListView.getAdapter().getItem(position);

        /*Intent detailIntent = new Intent(this, PaperchaseDetailActivity_alt.class);
        detailIntent.putExtra("PaperchaseId", selectedPaperchase);
        startActivity(detailIntent);*/
    }
}
