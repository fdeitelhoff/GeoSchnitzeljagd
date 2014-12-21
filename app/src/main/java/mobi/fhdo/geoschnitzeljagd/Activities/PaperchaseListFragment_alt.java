package mobi.fhdo.geoschnitzeljagd.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;
import java.util.UUID;

import mobi.fhdo.geoschnitzeljagd.DataManagers.Paperchases;
import mobi.fhdo.geoschnitzeljagd.Model.Paperchase;
import mobi.fhdo.geoschnitzeljagd.Model.User;

public class PaperchaseListFragment_alt extends ListFragment {

    private Callbacks callbacks;
    private Paperchases paperchases;
    private List<Paperchase> ownPaperchases;

    public PaperchaseListFragment_alt() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        paperchases = new Paperchases(getActivity());

        //ownPaperchases = paperchases.Own(new User(1, "Fabian", "test", ));

        setListAdapter(new ArrayAdapter<Paperchase>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                ownPaperchases));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        callbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        callbacks = null;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        callbacks.onItemSelected(ownPaperchases.get(position).getId());
    }

    public interface Callbacks {
        public void onItemSelected(UUID id);
    }
}
