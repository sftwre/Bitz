package com.bitz.isaacbuitrago.bitz.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import com.bitz.isaacbuitrago.bitz.Model.Bit;
import com.bitz.isaacbuitrago.bitz.R;
import com.bitz.isaacbuitrago.bitz.View.BitzInboxAdapter;
import com.bitz.isaacbuitrago.bitz.View.DividerItemDecoration;
import com.bitz.isaacbuitrago.bitz.View.ItemClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Responsible for displaying the Bitz send to the
 * current user and handling selection of a Bit.
 *
 * @author isaacbuitrago
 */
public class BitzInboxActivity extends AppCompatActivity implements ItemClickListener
{

    // data
    private BitzInboxAdapter mBitzAdapter;
    private List<Bit> bitz;

    // UI references
    private RecyclerView recyclerView;
    private LinearLayout bitzInboxLinearLayout;

    // Firbase references
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bitz_inbox);

        Toolbar toolbar = findViewById(R.id.toolbar);

        bitz = new ArrayList<Bit>();

        mBitzAdapter = new BitzInboxAdapter(this, bitz, this);

        bitzInboxLinearLayout = findViewById(R.id.bitzInboxLinearLayout);

        // Setup the RecyclerView
        recyclerView = findViewById(R.id.bitzInboxRecyclerView);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(mBitzAdapter);

        setSupportActionBar(toolbar);

        // setup firebase reference
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... voids)
            {
                getBitz();

                return null;
            }
        };

        task.execute();
    }

    /**
     * Fetch Bitz sent to the current user
     */
    private void getBitz()
    {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mDatabase
                .child(getString(R.string.dbname_bitzInbox))
                .child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        // Collection of Bit id's
                        Map<String, String> bitIds = (HashMap<String, String>) dataSnapshot.getValue();

                        // Read Bit from it's path
                        for(String id : bitIds.values())
                        {
                            mDatabase.child(getString(R.string.dbname_bitz))
                                    .child(id)
                                    .addValueEventListener(new ValueEventListener()
                                    {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                        {
                                            Bit b = dataSnapshot.getValue(Bit.class);

                                            bitz.add(b);

                                            mBitzAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError)
                                        {

                                        }
                                    });
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });
    }

    /**
     * Handles selection of a Card
     *
     * @param view
     * @param position
     */
    @Override
    public void onItemRowClicked(View view, int position)
    {
        // get the selected bit and play it
        Bit bit = bitz.get(position);

        Intent intent = new Intent(BitzInboxActivity.this, ReplayBitActivity.class);

        // pass the Bit as a parceable
        intent.putExtra("Bit", bit);

        startActivity(intent);

        finish();
    }
}
