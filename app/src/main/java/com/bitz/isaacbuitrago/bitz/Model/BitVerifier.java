package com.bitz.isaacbuitrago.bitz.Model;

import com.spotify.android.appremote.api.PlayerApi;

/**
 * Responsible for verifying a Bit and playing and stopping
 * the media player for the duration of a Bit.
 *
 * @author isaacbuitrago
 */
public class BitVerifier
{
    private Bit bit;

    private PlayerApi playerApi;


    /**
     * Constructor
     * @param bit Bit to verify
     * @param playerApi API to remote player service
     */
    public BitVerifier(Bit bit, PlayerApi playerApi)
    {
        this.bit = bit;

        this.playerApi = playerApi;
    }

    /**
     *
     */
    public void verifyBit()
    {
        long startTime = bit.getStartTime();

        long endTime = bit.getEndTime();

        long waitTime = endTime - startTime;

        playerApi.resume();

        // play track from start time
        playerApi.seekTo(startTime);

        // sleep for the Duration of the Bit

        try
        {
            // TODO, fix this
            Thread.sleep(10000);

        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        // pause the player
        playerApi.pause();
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
