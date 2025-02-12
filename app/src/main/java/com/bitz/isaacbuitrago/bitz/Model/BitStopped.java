package com.bitz.isaacbuitrago.bitz.Model;

import java.io.Serializable;

/**
 * Bit in the stopped state,
 * user is no longer tracking a region of interest.
 *
 * @author isaacbuitrago
 */
public class BitStopped extends BitState
{

    /**
     * Sets the end time of the Bit
     *
     * @param position
     * @param bit
     */
    @Override
    public void setTime(long position, Bit bit)
    {
        bit.setEndTime(position);
    }

    @Override
    public String toString()
    {
        return "Bit stopped";
    }
}
