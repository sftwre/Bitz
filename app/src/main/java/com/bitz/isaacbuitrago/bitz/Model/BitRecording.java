package com.bitz.isaacbuitrago.bitz.Model;

/**
 * Bit in the recording state, the user is currently
 * capturing the 'Bit' of a track they are interested in.
 *
 * @author isaacbuitrago
 */
public class BitRecording extends Bit implements BitState
{
    /**
     * Sets the start time of the Bit
     *
     * @param position
     */
    @Override
    public void setTime(long position)
    {
        startTime = position;

    }
}
