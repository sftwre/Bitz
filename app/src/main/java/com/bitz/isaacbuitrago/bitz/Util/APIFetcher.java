package com.bitz.isaacbuitrago.bitz.Util;


import android.util.Log;
import com.bitz.isaacbuitrago.bitz.Model.PlayListImages;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class responsible for fetching API resources,
 * such as an album cover or song, from a dedicated
 * music streaming service's API.
 *
 * @author isaacbuitrago
 */
public class APIFetcher
{

    private URL apiRequest =  null;

    private HttpURLConnection httpConnection = null;

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
            apiRequest = new URL(APIEndpoints.SEARCH_MUSIC);

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

            Log.i("APIFetcher", String.format("Track found\n %s", content.toString()));

            in.close();
        }
        catch (MalformedURLException e)
        {
            Log.e("APIFetcher", e.getStackTrace().toString());

        } catch (ProtocolException e)
        {
            Log.e("APIFetcher", e.getStackTrace().toString());

        } catch (IOException e)
        {
            Log.e("APIFetcher", e.getStackTrace().toString());
        }
        finally
        {
            httpConnection.disconnect();
        }

    }

    /**
     *
     * Fetches the image for a track, album, or playlist
     * from the remote provider and returns the address of the image.
     *
     * @param uri of an image
     *
     * @return url of the image or null if it is not provided
     */
    public String fetchImage(String uri) throws IOException
    {
        Pattern uriP = Pattern.compile("\\w+:\\w+:(\\w+)$");

        Pattern jsonP = Pattern.compile("\"url\":\"(.+?)\"");

        Matcher uriM = uriP.matcher(uri);

        uriM.find();

        String playlistId = uriM.group(1);

        String endpoint = APIEndpoints.GET_IMAGES.replaceAll("\\?", playlistId);

        try
        {
            apiRequest = new URL(endpoint);

            httpConnection = (HttpURLConnection) apiRequest.openConnection();

            httpConnection.setRequestMethod("GET");

            httpConnection.setDoOutput(true);

            httpConnection.setRequestProperty("Content-Type", "application/json");

            httpConnection.setRequestProperty("Authorization", String.format("%s %s", "Bearer", Properties.accessToken));

            httpConnection.connect();

            // read the response
            String response =  httpConnection.getResponseMessage();

            // TODO use serialization

            Matcher jsonM = jsonP.matcher(response);

            // return url of first image
            if(jsonM.find())
            {
                return jsonM.group(1);
            }
            else
                return null;

        }
        finally
        {
            httpConnection.disconnect();
        }

    }

}
