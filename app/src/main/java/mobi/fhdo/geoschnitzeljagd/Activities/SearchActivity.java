package mobi.fhdo.geoschnitzeljagd.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import mobi.fhdo.geoschnitzeljagd.DataManagers.Paperchases;
import mobi.fhdo.geoschnitzeljagd.Model.Paperchase;
import mobi.fhdo.geoschnitzeljagd.R;

public class SearchActivity extends Activity
{
    private Button seachButton;
    private EditText searchText;
    private ListView searchList;

    private Paperchases paperchases;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        seachButton = (Button) findViewById(R.id.button_search);
        searchText = (EditText) findViewById(R.id.searchText);
        searchList = (ListView) findViewById(R.id.searchList);

        paperchases = new Paperchases(this);

        List<Paperchase> list = paperchases.Search(searchText.getText().toString());




    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            Intent myIntent = new Intent(this, HomeActivity.class);
            startActivity(myIntent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
