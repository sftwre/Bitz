package com.bitz.isaacbuitrago.bitz.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.bitz.isaacbuitrago.bitz.R;

/**
 *
 * Replays the Bit received in the inbox
 * @author isaacbuitrago
 */
public class ReplayBitActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

}
