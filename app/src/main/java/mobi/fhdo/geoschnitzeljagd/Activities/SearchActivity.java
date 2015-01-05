package mobi.fhdo.geoschnitzeljagd.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import mobi.fhdo.geoschnitzeljagd.DataManagers.Paperchases;
import mobi.fhdo.geoschnitzeljagd.Model.Paperchase;
import mobi.fhdo.geoschnitzeljagd.R;

public class SearchActivity extends Activity {
    private Button seachButton;
    private EditText searchText;
    private ListView searchList;

    private Paperchases paperchases;
    private List<Paperchase> list;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        seachButton = (Button) findViewById(R.id.button_search);
        searchText = (EditText) findViewById(R.id.searchText);
        searchList = (ListView) findViewById(R.id.searchList);

        paperchases = new Paperchases(this);

        seachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list = paperchases.search(searchText.getText().toString());

                ArrayAdapter<Paperchase> adapter = new ArrayAdapter<Paperchase>(view.getContext(), android.R.layout.simple_list_item_1, list);

                searchList.setAdapter(adapter);
            }
        });


        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                if (list.get(position) != null) {
                    Intent intent = new Intent(view.getContext(), PaperchaseStart.class);
                    intent.putExtra("PaperchaseID", list.get(position).getId());
                    startActivity(intent);
                }
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
