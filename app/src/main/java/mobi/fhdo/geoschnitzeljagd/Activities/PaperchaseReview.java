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
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import mobi.fhdo.geoschnitzeljagd.DataManagers.DataManager;
import mobi.fhdo.geoschnitzeljagd.DataManagers.Users;
import mobi.fhdo.geoschnitzeljagd.Model.Exceptions.UserLoginException;
import mobi.fhdo.geoschnitzeljagd.Model.User;
import mobi.fhdo.geoschnitzeljagd.R;

public class PaperchaseReview extends Activity
{
    RatingBar ratingDifficulty;
    RatingBar ratingExciting;
    RatingBar ratingEnvironment;
    RatingBar ratingLength;
    EditText comment;
    Button ratingButton;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paperchase_review);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            Integer id = extras.getInt("PaperchaseID");
            Log.w("Id", id.toString());
            //Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
        }

        ratingDifficulty = (RatingBar) findViewById(R.id.ratingDifficulty);
        ratingExciting = (RatingBar) findViewById(R.id.ratingExciting);
        ratingEnvironment = (RatingBar) findViewById(R.id.ratingEnvironment);
        ratingLength = (RatingBar) findViewById(R.id.ratingLength);

        comment = (EditText) findViewById(R.id.editText_Comment);
        ratingButton = (Button) findViewById(R.id.review_button);
        ratingButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {


            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            Intent myIntent = new Intent(this, PaperchaseStart.class);
            startActivity(myIntent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
