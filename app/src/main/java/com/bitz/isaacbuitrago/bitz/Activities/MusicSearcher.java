package com.bitz.isaacbuitrago.bitz.Activities;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;

/**
 * Responsible for querying music from music streaming services and rendering the result.
 *
 * @author isaacbuitrago
 */
public class MusicSearcher extends ListActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
            String query = intent.getStringExtra(SearchManager.QUERY);

            searchMusic(query);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    /**
     *
     * Used to search for music on streaming platform
     *
     * @param query to search for on Music streaming platform
     */
    public void searchMusic(String query)
    {


    }
}
