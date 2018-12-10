package com.bitz.isaacbuitrago.bitz.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bitz.isaacbuitrago.bitz.Model.Bit;
import com.bitz.isaacbuitrago.bitz.Model.Friend;
import com.bitz.isaacbuitrago.bitz.Model.User;
import com.bitz.isaacbuitrago.bitz.R;
import com.bitz.isaacbuitrago.bitz.View.DividerItemDecoration;
import com.bitz.isaacbuitrago.bitz.View.FriendAdapter;
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

import static android.support.constraint.Constraints.TAG;


/**
 *
 * Responsible for displaying friends to the user,
 * managing selected friends in a Contextual Action Bar,
 * and allowing the user to send the bit.
 *
 * @author isaacbuitrago
 *
 */
public class SendBitActivity extends AppCompatActivity implements FriendAdapter.ItemClickListener
{


    // UI references
    private RecyclerView mRecyclerView;

    private FriendAdapter mfriendAdapter;

    private BottomNavigationView recipientsNavigationView;

    private LinearLayout recipientsLinearLayout;

    private Button sendButton;

    // data
    private List<Friend> friendsList;               // list of friends for current user

    private List<Friend> recipients;                // recipients that will receive the Bit

    private  Map<Integer, Integer> recipientsMapping;   // mapping of item position in recyclerview to position in a LinearLayout

    private Bit bit;                                    // bit to send

    // Firbase references
    private DatabaseReference mFriendsReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser sendingUser;


    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_send_bit);

        // bind UI references
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        recipientsNavigationView = (BottomNavigationView) findViewById(R.id.recipientsNavigationView);

        recipientsLinearLayout = (LinearLayout) findViewById(R.id.recipientsLinearLayout);

        sendButton = (Button) findViewById(R.id.sendButton);

        sendButton.setOnClickListener(mOnClickListener);

        mRecyclerView = (RecyclerView) findViewById(R.id.friendsRecycleViewer);

        mRecyclerView.setHasFixedSize(true);

        recipients = new ArrayList<Friend>();

        friendsList = new ArrayList<Friend>();

        recipientsMapping = new HashMap<Integer, Integer>();

        mfriendAdapter = new FriendAdapter(this, friendsList, this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mRecyclerView.setAdapter(mfriendAdapter);

        // Initialize the Database
        mFriendsReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbname_connections));

        setSupportActionBar(toolbar);

    }

    /**
     *
     *
     * Called when activity started
     */
    @Override
    protected void onStart()
    {
        super.onStart();

        // get the Bit from the Intent
        this.bit = (Bit) getIntent().getSerializableExtra("Bit");

        // set up Firebase auth and the auth state listener
        setupFirebaseAuth();

        mAuth.addAuthStateListener(mAuthListener);

        // show loader and fetch friends
        AsyncTask<Void, Void, Void> getFriendsTask = new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... voids)
            {
                getFriends();

                return null;
            }
        };

        // fetch the friends in the background
        getFriendsTask.execute();

        recipientsNavigationView.setVisibility(View.INVISIBLE);

    }

    /**
     * Fetches friends of the current user
     * and populates a list of friends to display
     * in the recycler view.
     */
    private void getFriends()
    {

        mFriendsReference.child("NnwEQnJGt8cAejwlYTTmKOU1QMY2")
                .child(getString(R.string.dbname_friends))
                .addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                friendsList.clear();

                Map<String, Boolean> map = (Map<String, Boolean>) dataSnapshot.getValue();

                // Iterate through each user, ignoring their UID
                for (Map.Entry<String, Boolean> entry : map.entrySet())
                {
                    // get the id of the friend
                    String userId = (String) entry.getKey();

                    FirebaseDatabase.getInstance().getReference()
                            .child(getString(R.string.dbname_users))
                            .child(userId)
                            .addListenerForSingleValueEvent(new ValueEventListener()
                             {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    Friend friend = new Friend();

                                    StringBuilder builder = new StringBuilder();

                                    User user = dataSnapshot.getValue(User.class);

                                    friend.setUserName(user.getUsername());

                                    builder.append(user.getFirstName());
                                    builder.append(" ");
                                    builder.append(user.getLastName());

                                    friend.setFullName(builder.toString());

                                    friend.setId(user.getId());

                                    friendsList.add(friend);

                                    mfriendAdapter.notifyDataSetChanged();

                                    Log.i(TAG, friend.toString());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError)
                                {
                                    Log.e(TAG, databaseError.getMessage());
                                }

                             });
                }


                Log.i(TAG, "Fetched all friends");
            }

            @Override
            public void onCancelled(DatabaseError error)
            {
                // Failed to read value
                Log.e(TAG, "Failed to read friends for user: ", error.toException());
            }

        });

    }

    /**
     * Stores the Bit in the Database, sends it to each listed recipient,
     * and closes the BottomNavigationView.
     *
     */
    private void sendBit()
    {
        final String bitzPath;

        // create a new node for the Bit in the database
        String bitId = mFriendsReference.child(getString(R.string.dbname_bitz)).push().getKey();

        String senderId = sendingUser.getUid();

        // store Bit in collection of Bitz
        bitzPath = String.format("/%s/%s", getString(R.string.dbname_bitz), bitId);

        mFriendsReference.child(bitzPath).setValue(bit);

        // update Bitz collection and inbox for each recipient
        Map<String, Object> childUpdates = new HashMap<String, Object>();

        for (Friend recipient : recipients)
        {
            String recipientPath = String.format("/%s/%s/%s", getString(R.string.dbname_bitzInbox), recipient.getId(), senderId);

            childUpdates.put(recipientPath, bitId);
        }

        mFriendsReference.updateChildren(childUpdates);

        // hide the bottom navigation view
        recipientsNavigationView.setVisibility(View.INVISIBLE);

        // TODO transition to another activity
    }

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth()
    {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user is logged in
                checkCurrentUser(user);

                // set the current user

                sendingUser = user;
            }
        };
    }

    /**
     * checks to see if the @param 'user' is logged in
     * and starts the Login activity if they are not.
     *
     * @param user
     */
    private void checkCurrentUser(FirebaseUser user)
    {
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

        if (user == null)
        {
            Log.d(TAG, "checkCurrentUser: Opening Login Activity");

            Intent intent = new Intent(SendBitActivity.this, LoginActivity.class);

            startActivity(intent);

            this.finish();
        }
    }

    /**
     *
     * Handles interaction with Send Button
     */
    View.OnClickListener mOnClickListener = (view) -> {sendBit();};


    /**
     * Handels selection of items in RecyclerView
     * @param view
     * @param position
     */
    @Override
    public void onItemRowClicked(View view, int position)
    {
        // get the friend and add it to the recipients
        Friend friend = mfriendAdapter.getItem(position);

        // item already selected, remove from recipients view and list
        if(mfriendAdapter.inSelectionArray(position))
        {
           View v = recipientsLinearLayout.findViewById(position);

            recipientsLinearLayout.removeView(v);

            Log.i(TAG, String.format("%s removed", friend.toString()));
        }
        else
        {
            // item has just been selected, add it to the recipients view and list

            TextView textView = new TextView(this);

            if(recipientsLinearLayout.getChildCount() > 0)
            {
                textView.setText( String.format(", %s", friend.getUserName()));
            }
            else {
                textView.setText(friend.getUserName());
            }

            textView.setTextColor(Color.WHITE);

            textView.setTypeface(Typeface.DEFAULT_BOLD);

            textView.setId(position);

            recipientsLinearLayout.addView(textView);

            recipients.add(friend);

            Log.i(TAG, String.format("Added %s as a recipient" , friend.toString()));
        }

        // add/ remove the friend from the Contextual Action Bar
        toggleSelection(position);
    }

    /**
     *
     * Calls the adapter to move
     * items in between a list of selected items
     * and highlights the selection if it not already selected.
     *
     * @param position
     */
    private void toggleSelection(int position)
    {
        mfriendAdapter.toggleSelection(position);

        if(recipientsNavigationView.getVisibility() == View.INVISIBLE)
        {
            recipientsNavigationView.setVisibility(View.VISIBLE);
        }
        else if(mfriendAdapter.getSelectedItemCount() == 0)
        {
            recipientsNavigationView.setVisibility(View.INVISIBLE);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionSearch)
        {
            Toast.makeText(getApplicationContext(), "Search...", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * Callback for handling ActionMode setup and usage
     */
    private class ActionModeCallback implements ActionMode.Callback
    {

        // startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu)
        {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);

            // disable swipe refresh if action mode is enabled
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.actionSend:
                    // delete all the selected messages
                    sendBit();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode)
        {
            mfriendAdapter.clearSelections();

        }
    }

}
