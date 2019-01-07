package com.bitz.isaacbuitrago.bitz.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitz.isaacbuitrago.bitz.Model.StreamingService;
import com.bitz.isaacbuitrago.bitz.R;
import com.bitz.isaacbuitrago.bitz.Util.Properties;
import com.bitz.isaacbuitrago.bitz.Util.StreamingServices;
import com.bitz.isaacbuitrago.bitz.View.ArrayImageAdapter;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import java.util.ArrayList;
import static android.content.ContentValues.TAG;


/**
 * Activity responsible for managing integration and authentication with
 * streaming service providers.
 *
 * @author isaacbuitrago
 */
public class IntegrationsActivity extends AppCompatActivity implements View.OnClickListener
{

    private ListView servicesList;
    private ArrayList<StreamingService> streamingServices = new ArrayList<>();

    private CheckBox checkBox;      // check box of selected streaming service

    private static final int REQUEST_CODE = 1337;


    /**
     * Creates references to the UI components and sets up the list view
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integrations);

        servicesList = findViewById(R.id.servicesList);

        // add all supported streaming services to the ListView

        StreamingService service = new StreamingService(StreamingServices.SPOTIFY, R.drawable.ic_spotify_logo);

        streamingServices.add(service);

        ArrayImageAdapter adapter = new ArrayImageAdapter(this, R.layout.service_list_row, streamingServices, this);

        servicesList.setAdapter(adapter);
    }

    /**
     *
     * Determines which services are already integrated with Bitz
     * and marks them as Connected
     */
    @Override
    protected void onStart()
    {
        super.onStart();
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
        checkBox = ((CheckBox) v);
        // authenticate with the selected streaming service
        if(checkBox.isChecked())
        {
            // get the name of the streaming service to integrate
            TextView textView = ((LinearLayout) checkBox.getParent()).findViewById(R.id.serviceName);

            String serviceName = textView.getText().toString();


            switch (serviceName)
            {
                case StreamingServices.SPOTIFY:
                    spotifyLogin();
            }

        }
        else
        {
            // remove integration

            ((CheckBox) v).setText(R.string.title_connect);
        }
    }

    /**
     * Authenticate with remote spotify app
     */
    private void spotifyLogin()
    {
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(Properties.CLIENT_ID, AuthenticationResponse.Type.TOKEN, Properties.REDIRECT_URI);

        builder.setScopes(new String[]{"streaming", "app-remote-control"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }


    /**
     * Receives the authentication result from the Spotify remote
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE)
        {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType())
            {
                // Response was successful and contains an auth token
                case TOKEN:

                    String message = String.format("Received token : %s", response.getCode());

                    Properties.accessToken = response.getAccessToken();

                    checkBox.setText(R.string.title_connected);

                    Log.i(TAG, message);
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.e(TAG, response.getError());

                    Toast.makeText(this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();

                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.d(TAG, "Could not authenticate user");
            }
        }
    }



}
