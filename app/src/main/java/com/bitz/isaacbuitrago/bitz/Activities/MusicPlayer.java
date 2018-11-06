package com.bitz.isaacbuitrago.bitz.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bitz.isaacbuitrago.bitz.Application.Properties;
import com.bitz.isaacbuitrago.bitz.Model.Bit;
import com.bitz.isaacbuitrago.bitz.Model.BitRecording;
import com.bitz.isaacbuitrago.bitz.Model.BitStopped;
import com.bitz.isaacbuitrago.bitz.Model.BitVerifier;
import com.bitz.isaacbuitrago.bitz.R;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import java.net.URL;

/**
 * Responsible for rendering the image and data of the currently playing
 * track on the user's phone.
 *
 * @author isaacbuitrago
 */
public class MusicPlayer extends AppCompatActivity
{

    private SpotifyAppRemote mSpotifyAppRemote;

    private PlayerApi playerApi;

    public static PlayerState playerState;

    private URL apiRequest;

    private SeekBar seekBar;

    private TextView timePlayed;

    private TextView timeRemaining;

    private ImageView albumCover;

    private BottomNavigationView navigation;

    private Handler handler;

    private Runnable runnable;

    private Bit bit;

    private static final int  SLEEP_TIME = 1000;

    private static final int CALLBACK_WAIT_TIME = 1000;


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
                        playerApi.getPlayerState().setResultCallback(e -> handleCallBack(e));

                    }
                    else if(bit.getState() instanceof BitStopped)
                    {

                        CallResult<PlayerState> result = playerApi.getPlayerState().setResultCallback(e -> handleCallBack(e));

                       // result.await();

                        BitVerifier bitVerifier = new BitVerifier(bit, playerApi, MusicPlayer.this);

                        bitVerifier.verifyBit();

//                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MusicPlayer.this);
//
//                        mBuilder.setMessage("Can you read me ?");
//
//                        mBuilder.setPositiveButton("Ok", null).
//                                setNegativeButton("Cancel", null);
//
//                        AlertDialog dialog = mBuilder.create();
//
//                        dialog.show();

                        return true;
                    }
            }

            return false;
        }
    };

    /**
     *
     * @param playerState
     *
     * @return
     */
    public void handleCallBack(PlayerState playerState)
    {
       bit.setTime(playerState.playbackPosition);
    }

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
                    handler.removeCallbacks(runnable);

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar)
                {

                    handler.removeCallbacks(runnable);
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

        handler = new Handler();

        timePlayed = (TextView) findViewById(R.id.timePlayed);

        timeRemaining = (TextView) findViewById(R.id.timeRemaining);

        albumCover = (ImageView) findViewById(R.id.albumCover);

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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

        // Set Spotify the connection parameters
        ConnectionParams connectionParams = new ConnectionParams.Builder(Properties.CLIENT_ID)
                .setRedirectUri(Properties.REDIRECT_URI)
                .showAuthView(true)
                .build();

        Log.i("LoginActivity", "Set connection params for Spotify");

        // Connect to Spotify
        SpotifyAppRemote.CONNECTOR.connect(this, connectionParams,
                new Connector.ConnectionListener()
                {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote)
                    {
                        mSpotifyAppRemote = spotifyAppRemote;

                        playerApi = mSpotifyAppRemote.getPlayerApi();

                        Log.d("MainActivity", "Connected to Spotify");

                        // start interacting with App Remote
                        spotifyConnected();
                    }

                    @Override
                    public void onFailure(Throwable throwable)
                    {
                        Log.e("MainActivity", throwable.getMessage(), throwable);

                    }
                });

        Log.i("LoginActivity", "Connected to spotify");

    }


    @Override
    protected void onStop()
    {
        super.onStop();

        SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);

        Log.i("MusicPlayer", "Disconnected from Spotify");
    }

    /**
     * Used to interact with remote Spotify process and subscribe to it's player state.
     */
    private void spotifyConnected()
    {
        // play the track
        playerApi.play("spotify:user:spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");

        // Subscribe to PlayerState
        playerApi.subscribeToPlayerState().setEventCallback(new Subscription.EventCallback<PlayerState>()
        {
            // Called when the Subscription receives a new event

            @Override
            public void onEvent(PlayerState playerState)
            {
                final Track track = playerState.track;

                if (track != null)
                {
                    bit.setTrackTitle(track.name);

                    bit.setArtist(track.artist.toString());

                    bit.setPlatform("spotify");

                    // TODO make async
                    // set the image for the current track
                    // renderImage(currentTrack.imageUri);

                    // set upper range of the progress bar
                    seekBar.setMax((int) track.duration);

                    // change the seek bar
                    changeSeekBar(playerState);

                    // TODO make async
                    // set the time stamps
                    timePlayed.setText(String.format("0:00"));

                    timeRemaining.setText(String.format("1:00"));

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
     * Used to set up property listeners for the seek bar
     *
     * @param playerState state of the spotify player in the background
     */
    private void changeSeekBar(final PlayerState playerState)
    {
        if(!(playerState.isPaused))
        {
            runnable = new Runnable()
            {
                @Override
                public void run()
                {
                    if(Build.VERSION.SDK_INT >= 24)
                    {
                        seekBar.setProgress((int) playerState.playbackPosition);
                    }
                }
            };

            handler.postDelayed(runnable, SLEEP_TIME);
        }
    }

    /**
     * Renders an album or track image on the album cover by fetching
     * the image from the specified URI.
     *
     * @param imageUri URI of the image to display on the album cover
     */
    private void renderImage(ImageUri imageUri)
    {

    }

    /**
     * Stops the music, and replays the track from the start time of the Bit.
     */
    private void verifyBit()
    {

    }

}
