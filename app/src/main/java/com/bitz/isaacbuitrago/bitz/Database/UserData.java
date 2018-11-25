package com.bitz.isaacbuitrago.bitz.Database;

import com.bitz.isaacbuitrago.bitz.Model.User;
import com.google.firebase.database.FirebaseDatabase;

/**
 *
 * @author isaacbuitrago
 */
public class UserData extends Gateway
{

    public UserData()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
    }

    @Override
    public <T> void write(T data)
    {
        User user = (User) data;

        mDatabase.child(((User) data).getUsername()).setValue(data);
    }

    @Override
    public <T> T fetch() {
        return null;
    }

    @Override
    public boolean update() {
        return false;
    }

    @Override
    public <T> void delete(T data) {

    }
}
