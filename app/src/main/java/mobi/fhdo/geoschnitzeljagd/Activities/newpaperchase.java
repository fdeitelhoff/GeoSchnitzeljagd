package mobi.fhdo.geoschnitzeljagd.Activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import mobi.fhdo.geoschnitzeljagd.Contexts.UserContext;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Marks;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Users;
import mobi.fhdo.geoschnitzeljagd.Model.Paperchase;
import mobi.fhdo.geoschnitzeljagd.Model.User;
import mobi.fhdo.geoschnitzeljagd.R;
import mobi.fhdo.geoschnitzeljagd.adapter.TabsPagerAdapter;


public class newpaperchase extends FragmentActivity implements ActionBar.TabListener {

    public Paperchase paperchase;
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    private Users users;
    private User loggedInUser;


    private String[] tabs = {"Bearbeiten", "Karte"};

    private Marks marks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpaperchase);

        // Initalisierung
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
        actionBar.show();

        users = new Users(this);
        marks = new Marks(this);

        loggedInUser = UserContext.getInstance().getLoggedInUser();

        // Die UserID ermitteln.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            paperchase = (Paperchase) extras.getSerializable("Paperchase");
        }

        if (paperchase != null) {
            paperchase = marks.ForPaperchase(paperchase);
        } else {
            paperchase = new Paperchase(loggedInUser.getId(), loggedInUser, "");
        }

        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }

    // Create the adapter that will return a fragment for each of the three
    // primary sections of the activity.
    // mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

    // Set up the ViewPager with the sections adapter.
    // mViewPager = (ViewPager) findViewById(R.id.pager);
    // mViewPager.setAdapter(mSectionsPagerAdapter);


    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.newpaperchase, menu);
        return true;
    }


}