package com.bitz.isaacbuitrago.bitz.Model;

/**
 * Bit in the recording state, the user is currently
 * capturing the 'Bit' of a track they are interested in.
 *
 * @author isaacbuitrago
 */
public class BitRecording extends BitState
{
    /**
     * Sets the start time of the Bit
     *
     * @param position
     * @param bit
     */
    @Override
    public void setTime(long position, Bit bit)
    {
        bit.setStartTime(position);
    }

    @Override
    public String toString()
    {
        return "Bit Recording";
    }
}
