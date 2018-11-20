package com.bitz.isaacbuitrago.bitz.Model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import com.bitz.isaacbuitrago.bitz.Util.APIFetcher;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

/**
 * Asynchronous task to download an image file and store
 * it in the file system.
 *
 * @author isaacbuitrago
 */
public class DownloadImageTask extends AsyncTask<String, Integer, Boolean>
{
    /**
     * Downloads the
     * @param strings
     * @return
     */
    @Override
    protected Boolean doInBackground(String [] strings)
    {
        APIFetcher apiFetcher = new APIFetcher();

        if(strings.length > 1)
        {
            for(int i = 0; i < strings.length; i++)
            {
                try
                {
                    String url = apiFetcher.fetchImage(strings[i]);

                    Picasso.get().load(url).into(new Target()
                    {

                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                        {

                            // once the image is loaded, store on disk

                            String root = Environment.getExternalStorageDirectory().toString();

                            File myDir = new File(root + "/Bitz");

                            if (!myDir.exists())
                            {
                                myDir.mkdirs();
                            }

                            String name = new Date() + ".jpg";

                            myDir = new File(myDir, name);

                            try
                            {
                                FileOutputStream out = new FileOutputStream(myDir);

                                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);

                                out.flush();

                                out.close();

                            } catch (IOException e)
                            {
                                Log.e("DownloadImageTask", e.getMessage());
                            }
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });

                } catch (IOException e)
                {
                    Log.e("DownloadImageTask", e.getMessage());

                    return false;
                }
            }
        }

        return true;
    }
}
