package mobi.fhdo.geoschnitzeljagd.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.util.UUID;

import mobi.fhdo.geoschnitzeljagd.R;

public class PaperchaseListActivity_alt extends FragmentActivity
        implements PaperchaseListFragment_alt.Callbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paperchase_list_alt);
    }

    @Override
    public void onItemSelected(UUID id) {
        Intent detailIntent = new Intent(this, PaperchaseDetailActivity_alt.class);
        detailIntent.putExtra("PaperchaseId", id);
        startActivity(detailIntent);
    }
}
