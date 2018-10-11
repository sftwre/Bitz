package com.bitz.isaacbuitrago.bitz.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.bitz.isaacbuitrago.bitz.Model.Bit;
import com.bitz.isaacbuitrago.bitz.R;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

public class MusicPlayer extends AppCompatActivity
{
    private TextView timePlayed;

    private Bit bit;

    private static final String CLIENT_ID = "53845a988d744ff98262bbd027bfa2c7";

    private static final String REDIRECT_URI = "http://acm-utsa.org/members/electrolove/";

    private SpotifyAppRemote mSpotifyAppRemote;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_skip_previous:
                    timePlayed.setText(R.string.title_home);
                    return true;
                case R.id.navigation_skip_next:
                    timePlayed.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_bit:
                    timePlayed.setText("Bit");

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_bit);

        timePlayed = (TextView) findViewById(R.id.message);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    @Override
    protected void onStart()
    {
        super.onStart();

//        // Set the connection parameters
//        ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID)
//                .setRedirectUri(REDIRECT_URI)
//                .showAuthView(true)
//                .build();
//
//        Log.i("LoginActivity", "Set connection params for spotify");
//
//        // Connect to Spotify
//        SpotifyAppRemote.CONNECTOR.connect(this, connectionParams,
//                new Connector.ConnectionListener()
//                {
//
//                    @Override
//                    public void onConnected(SpotifyAppRemote spotifyAppRemote)
//                    {
//                        mSpotifyAppRemote = spotifyAppRemote;
//
//                        Log.d("MainActivity", "Connected to Spotify");
//
//                        // start interacting with App Remote
//                        spotifyConnected();
//                    }
//
//                    @Override
//                    public void onFailure(Throwable throwable) {
//                        Log.e("MainActivity", throwable.getMessage(), throwable);
//
//                    }
//                });
//
//        Log.i("LoginActivity", "Connected to spotify");

    }




    @Override
    protected void onStop() {
        super.onStop();

        SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);

        Log.i("MusicPlayer", "Disconnected from Spotify");
    }

    /**
     * Handler when Spotify is connected
     */
    private void spotifyConnected()
    {
        mSpotifyAppRemote.getPlayerApi().play("spotify:user:spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");

        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState().setEventCallback(new Subscription.EventCallback<PlayerState>() {
            @Override
            public void onEvent(PlayerState playerState) {
                final Track track = playerState.track;
                if (track != null) {
                    Log.d("MainActivity", track.name + " by " + track.artist.name);
                }
            }
        });
    }


}
