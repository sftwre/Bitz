package com.bitz.isaacbuitrago.bitz.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.bitz.isaacbuitrago.bitz.Model.Bit;
import com.bitz.isaacbuitrago.bitz.Model.WaitingTask;
import com.bitz.isaacbuitrago.bitz.R;
import com.spotify.android.appremote.api.PlayerApi;

import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * Activity used to Verify a Bit after creation
 *
 * @author isaacbuitrago
 */
public class VerifyBit extends AppCompatActivity
{

    private PlayerApi playerApi;    // API in the back ground

    private final ScheduledExecutorService schedualer =
            Executors.newScheduledThreadPool(1);

    private Bit bit;                // Bit to verify

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
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    /**
     * Syncronize with player state in the background
     */
    @Override
    protected void onStart()
    {

        // get the Bit passed from the Music player

        this.bit = (Bit) getIntent().getSerializableExtra("Bit");

        verifyBit();

        super.onStart();
    }

    /**
     * Starts playing the Bit from the start time
     */
    public void verifyBit()
    {
        playerApi.pause();

        long startTime = bit.getStartTime();

        long endTime = bit.getEndTime();

        long waitTime = endTime - startTime;

        // schedual task to stop player after wait time
        schedualer.schedule( () -> playerApi.pause(), waitTime, TimeUnit.MILLISECONDS);

        // play track from start time
        playerApi.seekTo(startTime);

    }


}
