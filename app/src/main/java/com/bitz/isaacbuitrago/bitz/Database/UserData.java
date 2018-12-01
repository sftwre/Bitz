package com.bitz.isaacbuitrago.bitz.Database;

import android.util.Log;
import com.bitz.isaacbuitrago.bitz.Model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static android.support.constraint.Constraints.TAG;


/**
 *
 * @author isaacbuitrago
 */
public class UserData
{

    private DatabaseReference mDatabase;

    public UserData()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
    }

    /**
     *
     * Creates a new user account in the database.
     *
     * @param data <type>User</type> to store
     *
     * @param <T>
     */
    public <T> void write(T data)
    {
        // cast data to user
        User user = (User) data;

        mDatabase.child(((User) data).getId()).setValue(data);

        Log.i(TAG, String.format("Created new account for '%s' ", ((User) data).getUsername()));
    }

    public <T> T fetch(String id)
    {
        return null;
    }

    public boolean update() {
        return false;
    }

    public <T> void delete(T data) {

    }
}
