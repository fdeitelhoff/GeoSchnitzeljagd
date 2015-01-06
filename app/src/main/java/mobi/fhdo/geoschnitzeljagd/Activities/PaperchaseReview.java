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
import android.widget.RatingBar;
import android.widget.Toast;

import mobi.fhdo.geoschnitzeljagd.DataManagers.PaperchaseReviews;
import mobi.fhdo.geoschnitzeljagd.R;

public class PaperchaseReview extends Activity
{
    RatingBar ratingDifficulty;
    RatingBar ratingExciting;
    RatingBar ratingEnvironment;
    RatingBar ratingLength;
    EditText comment;
    Button ratingButton;

    private PaperchaseReviews reviews;

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

        reviews = new PaperchaseReviews(this);

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
                //TODO: Noch die passenden IDs übergeben
                //Review r = new Review(0,null,null,(int)ratingDifficulty.getRating(),(int)ratingExciting.getRating(),(int)ratingEnvironment.getRating(),(int)ratingLength.getRating(), comment.getText().toString());
                //reviews.Creaete(r);
                Toast.makeText(getBaseContext(), "Angelegt", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            this.finish();
            //Intent myIntent = new Intent(this, PaperchaseStart.class);
            //startActivity(myIntent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
