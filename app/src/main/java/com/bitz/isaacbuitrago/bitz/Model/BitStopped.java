package com.bitz.isaacbuitrago.bitz.Model;

/**
 * Bit in the stopped state,
 * user is no longer tracking a region of interest.
 *
 * @author isaacbuitrago
 */
public class BitStopped extends Bit implements BitState
{

    /**
     * Sets the end time of the Bit
     *
     * @param position
     */
    @Override
    public void setTime(long position)
    {
        endTime = position;
    }
}
