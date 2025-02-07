package com.bitz.isaacbuitrago.bitz.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bitz.isaacbuitrago.bitz.Model.StreamingService;
import com.bitz.isaacbuitrago.bitz.Util.Properties;
import com.bitz.isaacbuitrago.bitz.Model.Bit;
import com.bitz.isaacbuitrago.bitz.Model.BitRecording;
import com.bitz.isaacbuitrago.bitz.Model.BitStopped;
import com.bitz.isaacbuitrago.bitz.Model.StopwatchAdapter;
import com.bitz.isaacbuitrago.bitz.R;
import com.bitz.isaacbuitrago.bitz.Util.StreamingServices;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerContext;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Responsible for rendering the image and data of the currently playing
 * track on the user's phone.
 *
 * @author isaacbuitrago
 */
public class CreateBitActivity extends AppCompatActivity
{
    private SpotifyAppRemote mSpotifyAppRemote;

    private PlayerApi playerApi;

    private SeekBar seekBar;

    private TextView timePlayed;

    private TextView timeRemaining;

    private ImageView albumCover;

    private BottomNavigationView navigation;

    private Timer scheduler;

    private StopwatchAdapter stopwatch;

    private Bit bit;

    private Track track;

    private static final int  DELAY_TIME = 1000;

    private static final int  TIME_STAMPS_UPDATE_PERIOD = 1000;

    private static final int  SEEK_BAR_UPDATE_PERIOD = 800;

    private static final int REQUEST_CODE = 1;


    /**
     * Handles interaction with bottom navigation bar
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener

            = new BottomNavigationView.OnNavigationItemSelectedListener()
    {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            switch (item.getItemId())
            {
                /**
                 * Previous track
                 */
                case R.id.navigation_skip_previous:
                    timePlayed.setText(R.string.title_home);
                    return true;

                /**
                 * Next track
                 */
                case R.id.navigation_skip_next:
                    timePlayed.setText(R.string.title_dashboard);
                    return true;

                /**
                 * Create Bit
                 */
                case R.id.navigation_bit:

                    // change the state of the Bit each time the Disk is tapped
                    bit.transitionState();

                    Context context = getApplicationContext();

                    CharSequence text = bit.getState().toString();

                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);

                    toast.show();

                    if(bit.getState() instanceof BitRecording)
                    {
                        bit.setTime(stopwatch.getTime());
                    }
                    else if(bit.getState() instanceof BitStopped)
                    {
                        bit.setTime(stopwatch.getTime());

                        // go to start time
                        playerApi.seekTo(bit.getStartTime());

                        // pause the player
                        playerApi.pause();

                        // Start the new Activity
                        Intent intent = new Intent(CreateBitActivity.this, VerifyBit.class);

                        intent.putExtra("Bit", bit);

                        startActivity(intent);

                        return true;
                    }
            }

            return false;
        }
    };


    /**
     * Listener that responds to changes on the seek bar.
     */
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener =

            new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                {
                    // seek the current track to the position
                    if(fromUser && playerApi != null)
                    {
                        stopwatch.setTime((long) progress);

                        playerApi.seekTo((long) progress);
                    }
                }

                /**
                 * When the user starts moving the progress handler
                 * @param seekBar
                 */
                @Override
                public void onStartTrackingTouch(SeekBar seekBar)
                {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar)
                {
                }
            };

    /**
     * Creates the activity, binds GUI fields to instance variables,
     * and sets event listeners for the seek and navigation bar.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_bit);

        timePlayed = (TextView) findViewById(R.id.timePlayed);

        timeRemaining = (TextView) findViewById(R.id.timeRemaining);

        albumCover = (ImageView) findViewById(R.id.albumCover);

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }


    /**
     * handles results returned userName Activities launched by this Activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE)
        {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

            switch (response.getType())
            {
                // Response was successful and contains auth token
                case TOKEN:

                    // save the auth token
                    Properties.accessToken = response.getAccessToken();

                    Log.i("CreateBitActivity", "Authenticated to Spotify");

                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response

                    Log.e("CreateBitActivity", "Error authenticating to Spotify");

                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases

                    Log.e("CreateBitActivity", "Error authenticating to Spotify");

            }
        }
    }

    /**
     * Starts the activity, connects to the Spotify process,
     * and starts activity with the remote process.
     *
     */
    @Override
    protected void onStart()
    {
        super.onStart();

        // create the Bit for the activity
        bit = new Bit();

        // create the StopwatchAdapter
        stopwatch = new StopwatchAdapter();

        // create the scheduler
        scheduler = new Timer();

        // connect to Spotify App remote
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

                        Log.d("MainActivity", "Connected to Spotify");

                        // start interacting with API
                        spotifyConnected();
                    }

                    @Override
                    public void onFailure(Throwable throwable)
                    {
                        Log.e("MainActivity", throwable.getMessage(), throwable);
                    }
                });



        Log.i("SignInActivity", "Connected to spotify");

    }


    @Override
    protected void onStop()
    {
        super.onStop();

        SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);

        Log.i("CreateBitActivity", "Disconnected userName Spotify");
    }

    /**
     * Used to interact with remote Spotify process and subscribe to it's player state.
     */
    private void spotifyConnected()
    {
        // play the track

        // Subscribe to PlayerState
        playerApi.subscribeToPlayerState().setEventCallback(new Subscription.EventCallback<PlayerState>()
        {
            // TODO determine how many times this is called
            @Override
            public void onEvent(PlayerState playerState)
            {
                // first time bit has been modified
                if (! bit.isDirty())
                {
                    track = playerState.track;

                    bit.setTrackTitle(track.name);
                    bit.setArtist(track.artist.name);
                    bit.setPlatform(StreamingServices.SPOTIFY);
                    bit.setDirty(true);

                    // TODO download image on separate thread
                    // new DownloadImageTask().execute(track.uri);

                    // set upper range of the progress bar
                    seekBar.setMax((int) track.duration);
                    seekBar.setProgress((int) playerState.playbackPosition);

                    // synchronize timer with current track time
                    stopwatch.start();
                    stopwatch.setTime(playerState.playbackPosition);

                    // update the SeekBar
                    scheduler.schedule(new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            changeSeekBar();
                        }

                    }, DELAY_TIME, SEEK_BAR_UPDATE_PERIOD);

                    // update timestamps
                    scheduler.schedule(new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            // update UI element on main thread
                            timePlayed.post(changeTimePlayed());
                        }

                    }, DELAY_TIME, TIME_STAMPS_UPDATE_PERIOD);

                    scheduler.schedule(new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            // update UI element on main thread
                            timeRemaining.post(changeTimeRemaining());
                        }
                    }, DELAY_TIME, TIME_STAMPS_UPDATE_PERIOD);


                    StringBuilder builder = new StringBuilder()
                                    .append("Playing '")
                                    .append(track.name)
                                    .append("' by ")
                                    .append(track.artist.name);

                    Log.i("MainActivity", builder.toString());
                }
            }

        });
    }

    /**
     * Used to change SeekBar progress.
     *
     * @implNote cancels the scheduler and stops the timer
     *           once elapsed stopwatch time exceeds the duration
     *           of the track.
     *
     */
    private void changeSeekBar()
    {
        // If the stopwatch has not exceeded track duration, set progress.
        if(Build.VERSION.SDK_INT >= 21 &&
                stopwatch.getTime() <= track.duration)
        {
            seekBar.setProgress((int) stopwatch.getTime());
        }
        else
        {
            stopwatch.stop();

            scheduler.cancel();
        }
    }

    /**
     *
     * Updates timestamp for the time
     * that the current track has been playing.
     *
     * @return Runnable to execute on the main thread
     */
    private Runnable changeTimePlayed()
    {
        return () ->
        {
            long ms  = stopwatch.getTime();  // milliseconds played
            long sec = TimeUnit.MILLISECONDS.toSeconds(ms); // seconds played
            long min = TimeUnit.MILLISECONDS.toMinutes(ms); // minutes played

            String time = String.format("%02d:%02d", min, sec - TimeUnit.MINUTES.toSeconds(min));

            timePlayed.setText(time);
        };
    }

    /**
     *
     * Updates timestamp for the time
     * remaining in the current track.
     *
     * @return Runnable to execute on the main thread
     */
    private Runnable changeTimeRemaining()
    {
        return () ->
        {
            long ms = track.duration - stopwatch.getTime(); // milliseconds remaining
            long min = TimeUnit.MILLISECONDS.toMinutes(ms); // minutes played
            long sec = TimeUnit.MILLISECONDS.toSeconds(ms); // seconds played

            String time = String.format("%d:%d", min, sec -  TimeUnit.MINUTES.toSeconds(min));

            timeRemaining.setText(time);
        };

    }

}
