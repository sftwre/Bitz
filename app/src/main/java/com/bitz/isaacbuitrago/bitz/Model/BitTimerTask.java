package com.bitz.isaacbuitrago.bitz.Model;

import android.media.MediaPlayer;

import java.util.TimerTask;

/**
 * MediaPlayer observer that waits for a dictated time
 * to execute a unit of work.
 *
 * @author isaacbuitrago
 */
public class BitTimerTask extends TimerTask implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener
{
    public static long WAIT_TIME;   // time to wait for an action

    /**
     * Creates a BitTimerTask with a wait time.
     */
    public BitTimerTask(long waitTime)
    {
        WAIT_TIME = waitTime;
    }

    /**
     * starts the timer and synchronizes with the Player
     */
    @Override
    public void run()
    {
    }

    /**
     *
     */
    private void completeTask()
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
