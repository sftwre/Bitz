package com.bitz.isaacbuitrago.bitz.Model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
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

    private Context context;            // context used to display the AlertDialog

    private Handler handler;            // handler to manage work on current thread


    /**
     * Constructor
     * @param bit Bit to verify
     * @param playerApi API to remote player service
     * @param context  application data for displaying the AlertDialog
     */
    public BitVerifier(Bit bit, PlayerApi playerApi, Context context)
    {
        this.bit = bit;

        this.playerApi = playerApi;

        this.context = context;
    }

    /**
     * verifies the Bit
     */



    /**
     * Used to create an AlertDialog that
     * prompts the user to verify a Bit.
     *
     * @param context
     *
     * @return AlertDialog
     */
    private AlertDialog createVerifyAlertDialog(Context context)
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
    private void handleUserVerify()
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
