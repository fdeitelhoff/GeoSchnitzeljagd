package mobi.fhdo.geoschnitzeljagd.Activities;

import android.app.Activity;
import android.os.Bundle;

import mobi.fhdo.geoschnitzeljagd.Activities.util.SystemUiHider;
import mobi.fhdo.geoschnitzeljagd.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MarkMapActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mark_map);
    }
}
