package com.bitz.isaacbuitrago.bitz.Model;

/**
 * Defines how different states of a Bit set their time
 *
 * @author isaacbuitrago
 */
public interface BitState
{

    // TODO decouple the Bit from the Spotify API

    /**
     * Used to set the timestamp of a Track
     *
     * @param position
     */
    void setTime(long position);

}
