package com.bitz.isaacbuitrago.bitz.Activities;

import android.content.res.TypedArray;
import android.graphics.Color;
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
import android.widget.Toast;
import com.bitz.isaacbuitrago.bitz.Database.FriendsData;
import com.bitz.isaacbuitrago.bitz.Model.Friend;
import com.bitz.isaacbuitrago.bitz.R;
import com.bitz.isaacbuitrago.bitz.View.DividerItemDecoration;
import com.bitz.isaacbuitrago.bitz.View.FriendAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.support.constraint.Constraints.TAG;


/**
 *
 * Responsible for populating a recycler view with the friends
 * of a user, managing the selected friends, and sending the bit to the friends.
 *
 * @author isaacbuitrago
 *
 */
public class SendBitActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, FriendAdapter.FriendAdapterListener
{

    private RecyclerView mRecyclerView;

    private FriendAdapter mfriendAdapter;           // binds

    private List<Friend> friendsList;               // list of all friends for the user

    private List<Friend> recipients;                // list of friends that can receive the Bit

    private FriendsData friendsData;                //  Gateway to friend data in the Database

    private SwipeRefreshLayout swipeRefreshLayout;  // defines action when user refreshes the view

    private ActionModeCallback actionModeCallback;

    private ActionMode actionMode;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_send_bit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // initialize Firebase auth
        mAuth = FirebaseAuth.getInstance();

        // set the recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.friendsRecycleViewer);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        // TODO fix
        //mRecyclerView.setLayoutManager(swipeRefreshLayout);

        recipients = new ArrayList<Friend>();

        friendsList = new ArrayList<Friend>();

        mfriendAdapter = new FriendAdapter(this, friendsList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mRecyclerView.setAdapter(mfriendAdapter);

        actionModeCallback = new ActionModeCallback();

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        friendsData = new FriendsData();

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
     * Fetches the friends for the current user into the
     * list  of friends displayed in the RecyclerView
     */
    private void getFriends()
    {
        swipeRefreshLayout.setRefreshing(true);

        DatabaseReference mFriendsDB = FirebaseDatabase.getInstance().getReference("connections");

        DatabaseReference mUsersDB = FirebaseDatabase.getInstance().getReference("users");

        mFriendsDB.child("NnwEQnJGt8cAejwlYTTmKOU1QMY2").child("friends").addValueEventListener(new ValueEventListener()
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

                    mUsersDB.child("users")
                             .child(userId)
                             .addValueEventListener(new ValueEventListener()
                             {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    Friend friend = new Friend();

                                    StringBuilder builder = new StringBuilder();

                                    Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();

                                    friend.setUserName(map.get("username"));

                                    builder.append(map.get("firstName"));
                                    builder.append(" ");
                                    builder.append(map.get("lastName"));

                                    friend.setFullName(builder.toString());

                                    friend.setId(userId);

                                    friendsList.add(friend);

                                    Log.i(TAG, friend.toString());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError)
                                {
                                    Log.e(TAG, databaseError.getMessage());
                                }

                             });
                }

                // refresh the layout
                mRecyclerView.requestLayout();

                Log.i(TAG, "Fetched friends");
            }

            @Override
            public void onCancelled(DatabaseError error)
            {
                // Failed to read value
                Log.e(TAG, "Failed to read value.", error.toException());
            }

        });

        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * chooses a random color from array.xml
     */
    private int getRandomMaterialColor(String typeColor)
    {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
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

    @Override
    public void onIconClicked(int position)
    {
        recipients.add(friendsList.get(position));

        Log.i(TAG, "Recipient selected");
    }

    @Override
    public void onIconImportantClicked(int position)
    {
//        // Star icon is clicked,
//        // mark the message as important
//        Message message = messages.get(position);
//        message.setImportant(!message.isImportant());
//        messages.set(position, message);
//        mfriendAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMessageRowClicked(int position)
    {
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        if (mfriendAdapter.getSelectedItemCount() > 0)
        {
            enableActionMode(position);
        } else
            {

            Friend friend = friendsList.get(position);
            friendsList.set(position, friend);
            mfriendAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRowLongClicked(int position) {
        // long press is performed, enable action mode
        enableActionMode(position);
    }

    private void enableActionMode(int position)
    {
        if (actionMode == null)
        {
            //actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position)
    {
        mfriendAdapter.toggleSelection(position);
        int count = mfriendAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }


    private class ActionModeCallback implements ActionMode.Callback
    {
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
                case R.id.actionDelete:
                    // delete all the selected messages
                    deleteMessages();
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
            swipeRefreshLayout.setEnabled(true);
            actionMode = null;
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run()
                {
                    mfriendAdapter.resetAnimationIndex();
                   //mfriendAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    // deleting the messages from recycler view
    private void deleteMessages()
    {
        mfriendAdapter.resetAnimationIndex();
        List<Integer> selectedItemPositions =
                mfriendAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mfriendAdapter.removeData(selectedItemPositions.get(i));
        }
        mfriendAdapter.notifyDataSetChanged();
    }
}
