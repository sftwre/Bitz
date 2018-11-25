package com.bitz.isaacbuitrago.bitz.Database;

import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.FirebaseDatabase;
import static android.support.constraint.Constraints.TAG;

/**
 *
 * Gateway to the friend data
 *
 * @author isaacbuitrago
 */
public class FriendsData extends Gateway
{
    /**
     * constructor
     */
    public FriendsData()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference("friends");
    }

    @Override
    public <T> void write(T data)
    {
        // Write a message to the database
        mDatabase.setValue(data.toString());
    }

    @Override
    public <T> T fetch()
    {
        Boolean success = true;

        // Read from the database
        mDatabase.addValueEventListener(new ValueEventListener()
        {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                String value = dataSnapshot.getValue(String.class);

                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error)
            {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }

        });

        return (T) (success);
    }

    @Override
    public boolean update()
    {
        return false;
    }

    @Override
    public <T> void delete(T data) {

    }



}
