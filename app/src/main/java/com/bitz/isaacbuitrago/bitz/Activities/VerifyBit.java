package com.bitz.isaacbuitrago.bitz.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.bitz.isaacbuitrago.bitz.Util.Properties;
import com.bitz.isaacbuitrago.bitz.Model.Bit;
import com.bitz.isaacbuitrago.bitz.R;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 *
 * Activity used to Verify a Bit after creation
 *
 * @author isaacbuitrago
 */
public class VerifyBit extends AppCompatActivity
{

    private PlayerApi playerApi;    // API in the background

    private Bit bit;                // Bit to verify

    private Button verifyButton;    // Button used to verify a Bit

    private SpotifyAppRemote mSpotifyAppRemote; // connection to Spotify

    private final ScheduledExecutorService schedualer =
            Executors.newScheduledThreadPool(1);


    /**
     *
     * Handles interaction with the Verify Button
     */
    View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        // handles interaction with the Verify Button
        @Override
        public void onClick(View v)
        {
            // pause the player
            //playerApi.pause();

            // Start the SendBit activity
            Intent intent = new Intent(VerifyBit.this, SendBitActivity.class);

            startActivity(intent);
        }
    };

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_verify_bit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        verifyButton = (Button) findViewById(R.id.verifyButton);

        verifyButton.setOnClickListener(mOnClickListener);

        setSupportActionBar(toolbar);

        // set the back button
        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * Synchronize with player state in the background
     */
    @Override
    protected void onStart()
    {
        // get the Bit passed from the Music player
        this.bit = (Bit) getIntent().getSerializableExtra("Bit");

        // Set Spotify connection parameters
        ConnectionParams connectionParams = new ConnectionParams.Builder(Properties.CLIENT_ID)
                .setRedirectUri(Properties.REDIRECT_URI)
                .showAuthView(true)
                .build();

        // connect to Spotify
        SpotifyAppRemote.CONNECTOR.connect(this, connectionParams,
                new Connector.ConnectionListener()
                {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote)
                    {
                        mSpotifyAppRemote = spotifyAppRemote;

                        VerifyBit.this.playerApi = mSpotifyAppRemote.getPlayerApi();

                        verifyBit();

                        Log.d("VerifyBit", "Connected to Spotify");
                    }

                    @Override
                    public void onFailure(Throwable throwable)
                    {
                        Log.e("VerifyBit", throwable.getMessage(), throwable);
                    }
                });

        super.onStart();
    }

    /**
     * Called when the activity is stopped
     */
    @Override
    protected void onStop()
    {
        super.onStop();

        mSpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
    }

    /**
     * Starts playing the Bit from the start time
     */
    public void verifyBit()
    {
        // pause the player
        playerApi.pause();

        // determine Bit offsets
        long startTime = bit.getStartTime();

        long endTime = bit.getEndTime();

        long waitTime = endTime - startTime;

        // TODO remove
        if(Build.VERSION.SDK_INT >= 26)
        {
            Instant start = Instant.now();

            // schedule task to stop player after wait time
            ScheduledFuture<?> handler = schedualer.schedule(() ->
                    {
                        playerApi.pause();

                        Instant finish = Instant.now();

                        long timeElapsed = Duration.between(start, finish).toMillis();

                        String message = String.format("TimeElapsed : %d, WaitTime: %d", timeElapsed, waitTime);

                        Log.i("VerifyBit", message);
                    }
                    , waitTime, TimeUnit.MILLISECONDS);

            schedualer.schedule(() -> handler.cancel(false), waitTime, TimeUnit.MILLISECONDS);
        }

        // play track from start time
        playerApi.seekTo(startTime);

        // resume player
        playerApi.resume();
    }


}
