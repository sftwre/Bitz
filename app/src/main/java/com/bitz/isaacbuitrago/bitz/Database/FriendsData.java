package com.bitz.isaacbuitrago.bitz.Database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.bitz.isaacbuitrago.bitz.Model.Friend;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static android.support.constraint.Constraints.TAG;

/**
 *
 * Gateway to friends of the user
 *
 * @author isaacbuitrago
 */
public class FriendsData
{

    private DatabaseReference mDatabase;    // reference to the database

    private String userId;                  // Id of the current user

    /**
     * constructor
     */
    public FriendsData(String userId)
    {
        // set the database reference to read/write to the connections node
        mDatabase = FirebaseDatabase.getInstance().getReference("connections");
        this.userId = userId;
    }

    /**
     *
     */
    public FriendsData()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference("connections");
    }

    public <T> void write(T data)
    {
        // Write a message to the database
        mDatabase.setValue(data.toString());
    }

    /**
     * fetches the friends for the current user
     * @param id of the user
     * @return List of Friends
     */
    public void fetch(String id, List<Friend> friendList)
    {
        mDatabase.child(id).child("friends").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                Friend friend;
                // TODO optimize so only single node is received when data changes

                friendList.clear();

                Map<String, Boolean> map = (Map<String, Boolean>) dataSnapshot.getValue();

                //iterate through each user, ignoring their UID
                for (Map.Entry<String, Boolean> entry : map.entrySet())
                {
                    friend = new Friend();

                    // get the id of the friend
                    String userId = (String) entry.getKey();

                    // TODO fetch friend profiles efficiently
                    friend.setId(userId);

                    friend.setFullName("Bob Smith");

                    friend.setUserName("@Bob");

                    //Get phone field and append to list
                    friendList.add(friend);
                }

                Log.i(TAG, "Fetched friend for current user");
            }

            @Override
            public void onCancelled(DatabaseError error)
            {
                // Failed to read value
                Log.e(TAG, "Failed to read value.", error.toException());
            }

        });
    }


    /**
     * Maps a user ID to their name and username.
     *
     * @param uID of the profile to map
     * @return Friend belonging to the uID
     */
    public Friend mapProfile(String uID) throws ExecutionException, InterruptedException
    {

        final Friend friend = new Friend();

        FutureTask<ValueEventListener> users = (FutureTask<ValueEventListener>) mDatabase
                .child("users")
                .child(uID)
                .addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                StringBuilder builder = new StringBuilder();

                Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();

                friend.setUserName(map.get("username"));

                builder.append(map.get("firstName"));
                builder.append(" ");
                builder.append(map.get("lastName"));

                friend.setFullName(builder.toString());

                friend.setId(uID);

                Log.i(TAG, friend.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Log.e(TAG, databaseError.getMessage());
            }

        });


        users.get();

        return friend;
    }


    public boolean update()
    {
        return false;
    }


    public <T> void delete(T data)
    {

    }

}
