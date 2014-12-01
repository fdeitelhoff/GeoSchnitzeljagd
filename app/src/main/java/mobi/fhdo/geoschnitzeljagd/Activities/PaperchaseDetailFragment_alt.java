package mobi.fhdo.geoschnitzeljagd.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mobi.fhdo.geoschnitzeljagd.DataManagers.Marks;
import mobi.fhdo.geoschnitzeljagd.R;

/**
 * A fragment representing a single Paperchase detail screen.
 * This fragment is either contained in a {@link PaperchaseListActivity_alt}
 * in two-pane mode (on tablets) or a {@link PaperchaseDetailActivity_alt}
 * on handsets.
 */
public class PaperchaseDetailFragment_alt extends Fragment {
    private Marks marks;

    public PaperchaseDetailFragment_alt() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        marks = new Marks(getActivity());

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            Integer id = extras.getInt("PaperchaseId");

            //List<Mark> paperchaseMarks = marks.ForPaperchaseId(id);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_paperchase_detail_alt, container, false);

        return rootView;
    }
}
