package com.bitz.isaacbuitrago.bitz.Model;

/**
 * Defines how different states of a Bit set their time
 *
 * @author isaacbuitrago
 */
public abstract class BitState
{

    // TODO decouple the Bit from the Spotify API

    /**
     * Used to set the timestamp of a Track
     *
     * @param position
     * @param bit Bit to set the time on
     */
    public abstract void setTime(long position, Bit bit);

}
