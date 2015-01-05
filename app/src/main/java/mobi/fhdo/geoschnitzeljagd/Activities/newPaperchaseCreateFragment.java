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
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mobi.fhdo.geoschnitzeljagd.DataManagers.Paperchases;
import mobi.fhdo.geoschnitzeljagd.Model.Mark;
import mobi.fhdo.geoschnitzeljagd.Model.Paperchase;
import mobi.fhdo.geoschnitzeljagd.R;


public class newPaperchaseCreateFragment extends Fragment implements View.OnClickListener {

    private ViewPager viewPager;
    //public Marker chosenMarker;
    // final newpaperchase activity = (newpaperchase) getActivity();
    private Paperchases new_paperchase;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_newpaperchase_create, container, false);
        viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
        newpaperchase activity = (newpaperchase) getActivity();

        Button b_location_one = (Button) rootView.findViewById(R.id.b_set_location_1);
        Button b_location_two = (Button) rootView.findViewById(R.id.b_set_location_2);
        Button b_location_three = (Button) rootView.findViewById(R.id.b_set_location_3);
        Button b_location_four = (Button) rootView.findViewById(R.id.b_set_location_4);
        Button b_location_five = (Button) rootView.findViewById(R.id.b_set_location_5);
        b_location_one.setOnClickListener(this);
        b_location_two.setOnClickListener(this);
        b_location_three.setOnClickListener(this);
        b_location_four.setOnClickListener(this);
        b_location_five.setOnClickListener(this);


        Button b_save_paperchase = (Button) rootView.findViewById(R.id.b_save_paperchase);
        b_save_paperchase.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                newpaperchase activity = (newpaperchase) getActivity();
                for (int i = 0; i < activity.paperchase.getMarks().size(); i++) {
                    Toast.makeText(getActivity().getBaseContext(), "Inhalt paperchase: " + activity.paperchase.getMarks().get(i).getLatitude() + activity.paperchase.getMarks().get(i).getLongitude(), Toast.LENGTH_SHORT).show();
                }

                EditText et_paperchase_name = (EditText) getActivity().findViewById(R.id.et_paperchase_name);
                String paperchase_name = et_paperchase_name.getText().toString();
                List<String> paperchase_hint = new ArrayList<String>();
                EditText et_paperchase_hint1 = (EditText) getActivity().findViewById(R.id.et_paperchase_location_1_hint);
                paperchase_hint.add(et_paperchase_hint1.getText().toString());
                EditText et_paperchase_hint2 = (EditText) getActivity().findViewById(R.id.et_paperchase_location_2_hint);
                paperchase_hint.add(et_paperchase_hint2.getText().toString());
                EditText et_paperchase_hint3 = (EditText) getActivity().findViewById(R.id.et_paperchase_location_3_hint);
                paperchase_hint.add(et_paperchase_hint3.getText().toString());
                EditText et_paperchase_hint4 = (EditText) getActivity().findViewById(R.id.et_paperchase_location_4_hint);
                paperchase_hint.add(et_paperchase_hint4.getText().toString());
                EditText et_paperchase_hint5 = (EditText) getActivity().findViewById(R.id.et_paperchase_location_5_hint);
                paperchase_hint.add(et_paperchase_hint5.getText().toString());


                for (int i = 0; i < activity.paperchase.getMarks().size(); i++) {
                    activity.paperchase.getMarks().get(i).setHint(paperchase_hint.get(i));

                }

                if (paperchase_name.equals("")) {
                    activity.paperchase.setName("Schnitzeljagd");
                } else {
                    activity.paperchase.setName(paperchase_name);
                }
                if (activity.paperchase.getMarks().size() < 2) {
                    Toast.makeText(getActivity().getBaseContext(), "Die Schnitzeljagd muss mindestens 2 Markierungen enthalten!", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    new_paperchase = new Paperchases(getActivity().getApplicationContext());
                    new_paperchase.create(activity.paperchase);
                }

            }
        });

        if (activity.paperchase != null) {
            RefreshPaperchaseData(activity.paperchase, rootView);
        }

        return rootView;
    }

    @Override
    public void onClick(View v) {
        // Perform action on click
        switch (v.getId()) {
            case R.id.b_set_location_1:
                viewPager.setCurrentItem(1);
                break;
            case R.id.b_set_location_2:
                viewPager.setCurrentItem(1);
                break;
            case R.id.b_set_location_3:
                viewPager.setCurrentItem(1);
                break;
            case R.id.b_set_location_4:
                viewPager.setCurrentItem(1);
                break;
            case R.id.b_set_location_5:
                viewPager.setCurrentItem(1);
                break;
        }
    }

    // Todo: Von Marker auf Mark umgebaut, damit es (meiner Meinung nach) beim Editieren nutzbar ist.
    public void SetChosenMarker(Mark currentMark) {
        newpaperchase activity = (newpaperchase) getActivity();
        int currentIndex = activity.paperchase.getMarks().size();
        switch (currentIndex) {
            case 1:
                EditText locationOne = (EditText) getActivity().findViewById(R.id.et_paperchase_location_1);
                locationOne.setText("Longitude: " + currentMark.getLongitude() + " - Latitude: " + currentMark.getLatitude());
                break;
            case 2:
                EditText locationTwo = (EditText) getActivity().findViewById(R.id.et_paperchase_location_2);
                locationTwo.setText("Longitude: " + currentMark.getLongitude() + " - Latitude: " + currentMark.getLatitude());
                break;
            case 3:
                EditText locationThree = (EditText) getActivity().findViewById(R.id.et_paperchase_location_3);
                locationThree.setText("Longitude: " + currentMark.getLongitude() + " - Latitude: " + currentMark.getLatitude());
                break;
            case 4:
                EditText locationFour = (EditText) getActivity().findViewById(R.id.et_paperchase_location_4);
                locationFour.setText("Longitude: " + currentMark.getLongitude() + " - Latitude: " + currentMark.getLatitude());
                break;
            case 5:
                EditText locationFive = (EditText) getActivity().findViewById(R.id.et_paperchase_location_5);
                locationFive.setText("Longitude: " + currentMark.getLongitude() + " - Latitude: " + currentMark.getLatitude());
                break;
        }

    }

    public void RefreshPaperchaseData(Paperchase paperchase, View view) {
        EditText paperchaseName = (EditText) view.findViewById(R.id.et_paperchase_name);
        paperchaseName.setText(paperchase.getName());

        for (int i = 0; i < paperchase.getMarks().size(); i++) {
            Mark currentMark = paperchase.getMarks().get(i);

            switch (i) {
                case 0:
                    EditText locationOne = (EditText) view.findViewById(R.id.et_paperchase_location_1);
                    locationOne.setText("Longitude: " + currentMark.getLongitude() + " - Latitude: " + currentMark.getLatitude());
                    EditText hintOne = (EditText) view.findViewById(R.id.et_paperchase_location_1_hint);
                    hintOne.setText(currentMark.getHint());
                    break;
                case 1:
                    EditText locationTwo = (EditText) view.findViewById(R.id.et_paperchase_location_2);
                    locationTwo.setText("Longitude: " + currentMark.getLongitude() + " - Latitude: " + currentMark.getLatitude());
                    EditText hintTwo = (EditText) view.findViewById(R.id.et_paperchase_location_2_hint);
                    hintTwo.setText(currentMark.getHint());
                    break;
                case 2:
                    EditText locationThree = (EditText) view.findViewById(R.id.et_paperchase_location_3);
                    locationThree.setText("Longitude: " + currentMark.getLongitude() + " - Latitude: " + currentMark.getLatitude());
                    EditText hintThree = (EditText) view.findViewById(R.id.et_paperchase_location_3_hint);
                    hintThree.setText(currentMark.getHint());
                    break;
                case 3:
                    EditText locationFour = (EditText) view.findViewById(R.id.et_paperchase_location_4);
                    locationFour.setText("Longitude: " + currentMark.getLongitude() + " - Latitude: " + currentMark.getLatitude());
                    EditText hintFour = (EditText) view.findViewById(R.id.et_paperchase_location_4_hint);
                    hintFour.setText(currentMark.getHint());
                    break;
                case 4:
                    EditText locationFive = (EditText) view.findViewById(R.id.et_paperchase_location_5);
                    locationFive.setText("Longitude: " + currentMark.getLongitude() + " - Latitude: " + currentMark.getLatitude());
                    EditText hintFive = (EditText) view.findViewById(R.id.et_paperchase_location_5_hint);
                    hintFive.setText(currentMark.getHint());
                    break;
            }
        }
    }
}