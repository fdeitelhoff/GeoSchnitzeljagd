package mobi.fhdo.geoschnitzeljagd.adapter;

/**
 * Created by JW on 26.11.2014.
 * This Adapter provides fragment views to tabs
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import mobi.fhdo.geoschnitzeljagd.Activities.newPaperchaseCreateFragment;
import mobi.fhdo.geoschnitzeljagd.Activities.newPaperchaseCreateMapFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private newPaperchaseCreateFragment newPaperchaseCreateFragment;
    private newPaperchaseCreateMapFragment newPaperchaseCreateMapFragment;

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                if (newPaperchaseCreateFragment == null) {
                    newPaperchaseCreateFragment = new newPaperchaseCreateFragment();
                }

                return newPaperchaseCreateFragment;
            case 1:
                // Games fragment activity
                if (newPaperchaseCreateMapFragment == null) {
                    newPaperchaseCreateMapFragment = new newPaperchaseCreateMapFragment();
                }

                return newPaperchaseCreateMapFragment;

        }


        return null;
    }


    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

}
