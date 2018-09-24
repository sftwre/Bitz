package com.bitz.isaacbuitrago.bitz.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

import com.bitz.isaacbuitrago.bitz.Model.Bit;
import com.bitz.isaacbuitrago.bitz.R;

public class MusicPlayer extends AppCompatActivity {

    private TextView timePlayed;

    private Bit bit;

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
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create__bit);

        timePlayed = (TextView) findViewById(R.id.message);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

}
