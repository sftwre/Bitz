package com.bitz.isaacbuitrago.bitz.Database;

import com.google.firebase.database.DatabaseReference;

/**
 *
 * Abstract functionality for each class that interacts with the database.
 *
 * @author isaacbuitrago
 */
public abstract class Gateway
{

    DatabaseReference mDatabase; // reference to database

    public abstract <T> void write(T data);

    public abstract <T> T fetch(String id);

    public abstract boolean update();

    public abstract <T> void delete(T data);

}
