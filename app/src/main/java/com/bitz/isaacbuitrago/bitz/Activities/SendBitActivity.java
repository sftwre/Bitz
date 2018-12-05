package com.bitz.isaacbuitrago.bitz.Activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.Toast;
import com.bitz.isaacbuitrago.bitz.Model.Friend;
import com.bitz.isaacbuitrago.bitz.Model.User;
import com.bitz.isaacbuitrago.bitz.R;
import com.bitz.isaacbuitrago.bitz.View.DividerItemDecoration;
import com.bitz.isaacbuitrago.bitz.View.FriendAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static android.support.constraint.Constraints.TAG;


/**
 *
 * Responsible for displaying
 * friends the user can send the
 * bit to, managing the selected friends,
 * and sending the bit.
 *
 * @author isaacbuitrago
 *
 */
public class SendBitActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, FriendAdapter.ItemClickListener
{


    // UI
    private RecyclerView mRecyclerView;

    private FriendAdapter mfriendAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;  // defines action when user refreshes the view

    // data lists
    private List<Friend> friendsList;               // list of all friends for the user

    private List<Friend> recipients;                // list of friends that can receive the Bit

    private ActionModeCallback actionModeCallback;

    private ActionMode actionMode;


    // database
    private DatabaseReference mFriendsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_send_bit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // set the recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.friendsRecycleViewer);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        recipients = new ArrayList<Friend>();

        friendsList = new ArrayList<Friend>();

        mfriendAdapter = new FriendAdapter(this, friendsList, this);

        // Initialize the recycler view
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mRecyclerView.setAdapter(mfriendAdapter);

        // create the action mode
        actionModeCallback = new ActionModeCallback();

        // Initialize the Database
        mFriendsReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbname_connections));

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // show loader and fetch friends
        swipeRefreshLayout.post(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        getFriends();
                    }
                });
    }

    /**
     * Fetches friends of the current user
     * and populates a list of friends to display
     * in the recycler view.
     */
    private void getFriends()
    {
        swipeRefreshLayout.setRefreshing(true);


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

        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     *
     */
    private void sendBit()
    {

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onItemRowClicked(View view, int position)
    {
        // get the friend and add it to the recipients
        Friend friend = mfriendAdapter.getItem(position);

        this.recipients.add(friend);

        // start the action mode if not already started
        if(actionMode == null)
        {
            actionMode = startActionMode(actionModeCallback);
        }

        // if the view has already been selected, remove the highlighting
        if(mfriendAdapter.inSelectionArray(position))
        {
            view.setSelected(false);

            view.setBackgroundColor(0);
        }
        else
        {
            view.setSelected(true);

            view.setBackgroundColor(getColor(R.color.text_color));
        }

        // toggle the selection
        toggleSelection(position);

        Log.i(TAG, String.format("Added %s as a recipient" , friend.toString()));
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

        if(mfriendAdapter.getSelectedItemCount() == 0)
        {
            actionMode.finish();
        }
        else
        {
            actionMode.setTitle("");
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

    @Override
    public void onRefresh()
    {
        // swipe refresh is performed, fetch the messages again
        getFriends();
    }

//    @Override
//    public void onIconClicked(int position)
//    {
//        if (actionMode == null)
//        {
//            actionMode = startSupportActionMode(actionModeCallback);
//        }
//
//        toggleSelection(position);
//    }

    private class ActionModeCallback implements ActionMode.Callback
    {

        // startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu)
        {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);

            // disable swipe refresh if action mode is enabled
            swipeRefreshLayout.setEnabled(false);
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

            actionMode = null;
        }
    }

}
