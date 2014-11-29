package mobi.fhdo.geoschnitzeljagd.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mobi.fhdo.geoschnitzeljagd.DataManagers.DataManager;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Users;
import mobi.fhdo.geoschnitzeljagd.Model.Exceptions.UserLoginException;
import mobi.fhdo.geoschnitzeljagd.Model.User;
import mobi.fhdo.geoschnitzeljagd.R;

public class PaperchaseStart extends Activity
{
    private Button ratingButton;

    private int id;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paperchase_start);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            id = extras.getInt("PaperchaseID");
            Log.w("Id", String.valueOf(id));
            //Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
        }

        ratingButton = (Button) findViewById(R.id.review_button);
        ratingButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(view.getContext(), PaperchaseReview.class);
                intent.putExtra("PaperchaseID", id);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            Intent myIntent = new Intent(this, SearchActivity.class);
            startActivity(myIntent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
