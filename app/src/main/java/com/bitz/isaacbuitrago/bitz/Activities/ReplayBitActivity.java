package com.bitz.isaacbuitrago.bitz.Activities;


import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import com.bitz.isaacbuitrago.bitz.Model.Bit;
import com.bitz.isaacbuitrago.bitz.Model.BitPlaying;
import com.bitz.isaacbuitrago.bitz.Model.BitRecording;
import com.bitz.isaacbuitrago.bitz.Model.BitState;
import com.bitz.isaacbuitrago.bitz.Model.BitStopped;
import com.bitz.isaacbuitrago.bitz.Model.StopwatchAdapter;
import com.bitz.isaacbuitrago.bitz.R;
import com.bitz.isaacbuitrago.bitz.Util.Properties;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import static android.content.ContentValues.TAG;

/**
 *
 * Replays the Bit received in the inbox
 *
 * @author isaacbuitrago
 */
public class ReplayBitActivity extends AppCompatActivity
{
    // UI references
    private ImageButton playBackButton;

    private SeekBar seekBar;

    private TextView timePlayed;

    private TextView timeRemaining;

    // Spotify references
    private PlayerApi playerApi;

    private SpotifyAppRemote mSpotifyAppRemote;

    // model references
    private Bit bit;

    private StopwatchAdapter stopWatch;

    // Threading Objects
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // constants
    private static final int  DELAY_TIME = 1000;

    private static final int  PERIOD = 800;


    /**
     *
     * Plays the Bit and schedules Task to
     * stop the player after the Bit is done.
     */
    View.OnClickListener mOnClickListener = (i) ->
    {

        // PlayerApi not connected
        if(playerApi == null)
        {
            return;
        }

        if(bit.getState() instanceof BitStopped)
        {

            bit.transitionState();

            // calculate wait time
            long startTime = bit.getStartTime();

            long endTime = bit.getEndTime();

            long waitTime = endTime - startTime;

            // seek to the Bit start time and play the Bit
            playerApi.play(bit.getTrackUri());

            playerApi.seekTo(startTime);

            // schedule task to update the SeekBar
            scheduler.schedule(() ->
            {
                changeSeekBar();
            }, DELAY_TIME, TimeUnit.MILLISECONDS);

            // create task to wait duration of Bit
            if (Build.VERSION.SDK_INT >= 26)
            {
                Instant start = Instant.now();

                // schedule task to stop player after wait time
                ScheduledFuture<?> handler = scheduler.schedule(() -> {
                    playerApi.pause();
                }, waitTime, TimeUnit.MILLISECONDS);

                scheduler.schedule(() -> handler.cancel(false), waitTime, TimeUnit.MILLISECONDS);
            }

        }

        else if(bit.getState() instanceof BitRecording)
        {
            bit.transitionState();

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

        setContentView(R.layout.activity_replay);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        playBackButton = findViewById(R.id.playBackButton);

        playBackButton.setOnClickListener(mOnClickListener);

        seekBar = findViewById(R.id.seekBar);

        timePlayed = findViewById(R.id.timePlayed);

        timeRemaining = findViewById(R.id.timeRemaining);

        // create stop watch for tracking current playing position
        stopWatch = new StopwatchAdapter();

        // retrieve the Bit
        bit = (Bit) getIntent().getSerializableExtra("Bit");

        bit.setState(new BitPlaying());
    }


    @Override
    protected void onStart()
    {
        super.onStart();


        // Connect to Spotify
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(Properties.CLIENT_ID)
                        .setRedirectUri(Properties.REDIRECT_URI)
                        .showAuthView(true)
                        .build();


        SpotifyAppRemote.CONNECTOR.connect(this, connectionParams,
                new Connector.ConnectionListener()
                {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote)
                    {
                        mSpotifyAppRemote = spotifyAppRemote;

                        playerApi = mSpotifyAppRemote.getPlayerApi();

                        Log.i(TAG, "Connected to Spotify");
                    }

                    @Override
                    public void onFailure(Throwable throwable)
                    {
                        Log.e("MainActivity", throwable.getMessage(), throwable);

                    }
                });

        // set upper range of the progress bar
        seekBar.setMax((int) bit.getEndTime());

    }

    /**
     * Moves the seek bar for a visual
     * affect while the Bit is playing.
     */
    private void changeSeekBar()
    {
        seekBar.setProgress((int) stopWatch.getTime());
    }

}


