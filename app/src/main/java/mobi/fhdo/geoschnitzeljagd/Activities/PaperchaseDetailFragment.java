package mobi.fhdo.geoschnitzeljagd.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import mobi.fhdo.geoschnitzeljagd.DataManagers.Marks;
import mobi.fhdo.geoschnitzeljagd.Model.Mark;
import mobi.fhdo.geoschnitzeljagd.R;

/**
 * A fragment representing a single Paperchase detail screen.
 * This fragment is either contained in a {@link PaperchaseListActivity}
 * in two-pane mode (on tablets) or a {@link PaperchaseDetailActivity}
 * on handsets.
 */
public class PaperchaseDetailFragment extends Fragment {
    private Marks marks;

    public PaperchaseDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        marks = new Marks(getActivity());

        if (getArguments().containsKey("PaperchaseId")) {
            List<Mark> paperchaseMarks = marks.ForPaperchaseId(Integer.getInteger(getArguments().getString("PaperchaseId")));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_paperchase_detail, container, false);

        return rootView;
    }
}
