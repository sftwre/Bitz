package com.bitz.isaacbuitrago.bitz.Model;

import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

/**
 * MediaPlayer observer that waits for a dictated time
 * to execute a unit of work and sends message to
 * the calling thread.
 *
 *
 * @author isaacbuitrago
 */
public class WaitingTask implements Runnable, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener
{

    private static long SLEEP_TIME;   // time to wait for an action

    private Handler handler;        // handler of calling thread

    /**
     * Creates a BitTimerTask with a wait time.
     */
    public WaitingTask(long waitTime, Handler handler)
    {
        SLEEP_TIME = waitTime;

        this.handler = handler;
    }

    /**
     * starts the timer and synchronizes with the Player
     */
    @Override
    public void run()
    {

        try
        {
            Log.e("WaitingTask", "Before sleep");


            Thread.sleep(SLEEP_TIME);

            // post the a message to calling thread

        } catch (InterruptedException e)
        {
            Log.e("WaitingTask", e.getMessage());
        }
    }

    /**
     *
     */
    public void completeTask()
    {

    }

    @Override
    public void onCompletion(MediaPlayer mp)
    {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra)
    {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp)
    {

    }
}
