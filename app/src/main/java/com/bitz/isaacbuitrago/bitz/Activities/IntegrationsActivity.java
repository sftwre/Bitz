package com.bitz.isaacbuitrago.bitz.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import com.bitz.isaacbuitrago.bitz.R;
import com.bitz.isaacbuitrago.bitz.View.ArrayImageAdapter;
import java.util.ArrayList;

/**
 * Activity responsible for managing integration and authentication with
 * streaming service providers.
 *
 * @author isaacbuitrago
 */
public class IntegrationsActivity extends AppCompatActivity implements View.OnClickListener
{

    private ListView servicesList;
    private ArrayList<Integer> logoIdentifiers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integrations);

        servicesList = findViewById(R.id.servicesList);

        logoIdentifiers.add(R.drawable.ic_spotify_logo);

        ArrayImageAdapter adapter = new ArrayImageAdapter(this, R.layout.service_list_row, logoIdentifiers, this);

        servicesList.setAdapter(adapter);
    }

    /**
     * Attempts the authenticate the user with
     * the selected streaming service.
     *
     * @param v Checkbox that was clicked
     */
    @Override
    public void onClick(View v)
    {
        if(((CheckBox) v).isChecked())
        {
            ((CheckBox) v).setText(R.string.title_connected);
        }
        else
        {
            ((CheckBox) v).setText(R.string.title_connect);
        }
    }
}
