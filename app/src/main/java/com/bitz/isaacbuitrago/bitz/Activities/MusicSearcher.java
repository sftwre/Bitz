package com.bitz.isaacbuitrago.bitz.Activities;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bitz.isaacbuitrago.bitz.Application.Properties;
import com.bitz.isaacbuitrago.bitz.Util.ParameterStringBuilder;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;

/**
 * Responsible for fetching music that matches a user's search criteria.
 *
 * @author isaacbuitrago
 */
public class MusicSearcher extends ListActivity
{

    private URL apiRequest =  null;

    private HttpURLConnection httpConnection = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
            String query = intent.getStringExtra(SearchManager.QUERY);

            search(query);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

    }

    /**
     *
     * //TODO Need to reqturn collection of Tracks, albums, etc...
     *
     * Used to search for music on streaming platform
     *
     * @param query title of a track, album , song, or artist to search for on a
     *              music streaming platform
     */
    public void search(String query)
    {

        try
        {
            apiRequest = new URL("https://api.spotify.com/v1/search");

            httpConnection = (HttpURLConnection) apiRequest.openConnection();

            httpConnection.setRequestMethod("GET");

            httpConnection.setDoOutput(true);

            httpConnection.setRequestProperty("Content-Type", "application/json");

            httpConnection.setRequestProperty("Authorization", String.format("%s %s", "Bearer", Properties.accessToken));

            HashMap<String, String> params = new HashMap<String, String>();

            params.put("q", query);
            params.put("type", "track");
            params.put("limit", "1");

            DataOutputStream out = new DataOutputStream(httpConnection.getOutputStream());

            out.writeBytes(ParameterStringBuilder.getParamsString(params));
            out.flush();
            out.close();

            httpConnection.connect();

            // read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));

            String inputLine;

            StringBuffer content = new StringBuffer();

            while ((inputLine = in.readLine()) != null)
            {
                content.append(inputLine);
            }

            // TODO Deserialize into Gson

            Log.i("MusicSearcher", String.format("Track found\n %s", content.toString()));

            in.close();
        }
        catch (MalformedURLException e)
        {
            Log.e("MusicSearcher", e.getStackTrace().toString());

        } catch (ProtocolException e)
        {
            Log.e("MusicSearcher", e.getStackTrace().toString());

        } catch (IOException e)
        {
            Log.e("MusicSearcher", e.getStackTrace().toString());
        }
        finally
        {
            httpConnection.disconnect();
        }

    }
}
