package mobi.fhdo.geoschnitzeljagd.Activities;
/**
 * Created by JW on 26.11.2014.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import mobi.fhdo.geoschnitzeljagd.R;


public class newPaperchaseCreateFragment extends Fragment {

    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_newpaperchase_create, container, false);

        viewPager = (ViewPager) getActivity().findViewById(R.id.pager);

        Button b_location_one = (Button) rootView.findViewById(R.id.b_set_location_one);
        b_location_one.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                 viewPager.setCurrentItem(1);
            }
        });


        return rootView;
    }

}