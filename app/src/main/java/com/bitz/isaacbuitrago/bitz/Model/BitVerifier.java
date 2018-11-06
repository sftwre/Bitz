package com.bitz.isaacbuitrago.bitz.Model;

import android.app.AlertDialog;
import android.arch.core.executor.TaskExecutor;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.bitz.isaacbuitrago.bitz.R;
import com.spotify.android.appremote.api.PlayerApi;

/**
 * Responsible for verifying a Bit and playing and stopping
 * the media player for the duration of a Bit.
 *
 * @author isaacbuitrago
 */
public class BitVerifier extends FragmentActivity
{
    private Bit bit;

    private PlayerApi playerApi;

    private AlertDialog alertDialog;    // Dialog to prompt the user for verification

    private Context context;            // context used to display the AlertDialog


    /**
     * Constructor
     * @param bit Bit to verify
     * @param playerApi API to remote player service
     */
    public BitVerifier(Bit bit, PlayerApi playerApi, Context context)
    {
        this.bit = bit;

        this.playerApi = playerApi;

        this.context = context;
    }

    /**
     *
     */
    public void verifyBit()
    {

        playerApi.pause();

        long startTime = bit.getStartTime();

        long endTime = bit.getEndTime();

        long waitTime = endTime - startTime;

        // play track from start time
        playerApi.seekTo(startTime);

        playerApi.resume();

        // prompt the user once the Bit complete

        AlertDialog dialog = createVerifyDialog(context);

        dialog.show();

        // pause the player
        playerApi.pause();
    }


    /**
     * Used to create an AlertDialog that
     * prompts the user to verify a Bit.
     *
     * @param context
     *
     * @return AlertDialog
     */
    private AlertDialog createVerifyDialog(Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(R.string.verify_bit_message)

                .setPositiveButton(R.string.verify_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Log.i(getClass().getName(),"User verified Bit ");
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Log.i(getClass().getName(),"User canceled Bit ");

                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    /**
     *
     */
    private void handleUserCancel()
    {

    }

    /**
     *
     */
    private void handeUserVerify()
    {

    }

    /**
     * Getters and Setters
     */
    public Bit getBit()
    {
        return bit;
    }

    public void setBit(Bit bit)
    {
        this.bit = bit;
    }

    public void setPlayerApi(PlayerApi playerApi)
    {
        this.playerApi = playerApi;
    }

    public PlayerApi getPlayerApi()
    {
        return playerApi;
    }

}
