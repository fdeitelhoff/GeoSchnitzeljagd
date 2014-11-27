package mobi.fhdo.geoschnitzeljagd.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import mobi.fhdo.geoschnitzeljagd.R;

public class PaperchaseListActivity extends FragmentActivity
        implements PaperchaseListFragment.Callbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paperchase_list);
    }

    @Override
    public void onItemSelected(int id) {
        Intent detailIntent = new Intent(this, PaperchaseDetailActivity.class);
        detailIntent.putExtra("PaperchaseId", id);
        startActivity(detailIntent);
    }
}
