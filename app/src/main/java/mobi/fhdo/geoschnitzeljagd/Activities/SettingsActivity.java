package mobi.fhdo.geoschnitzeljagd.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import mobi.fhdo.geoschnitzeljagd.R;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        Log.d("MyApp", "I am here");
        Toast.makeText(getBaseContext(), "Test", Toast.LENGTH_LONG).show();

        // GPS-Switch
        Switch mySwitch;
        mySwitch = (Switch) findViewById(R.id.switch_gps);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("MyApp", "I am here");
                // do something, the isChecked will be
                // true if the switch is in the On position
            }
        });


        // Benachrichtigungs-Switch
        mySwitch = (Switch) findViewById(R.id.switch_notification);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("MyApp", "I am here");
                // do something, the isChecked will be
                // true if the switch is in the On position
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent myIntent = new Intent(this, HomeActivity.class);
            startActivity(myIntent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
