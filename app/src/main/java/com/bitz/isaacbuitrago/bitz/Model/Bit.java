package com.bitz.isaacbuitrago.bitz.Model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Copyright Isaac Buitrago 2018, All rights reserved
 *
 * Represents data about a track which can be shared
 */
public class Bit implements Serializable
{

    private long startTime;         // start time of a Bit

    private long endTime;           // end time of a Bit

    private  String trackTitle;     // title of the Track

    private  String artist;         // name of artist

    private  String platform;       // streaming platform the track is on

    private BitState state;         // Bit state, either recording or stopped

    private boolean dirty;          // dirty flag


    // set the active Bit states in a Bit

    private static HashMap<String, BitState> states =

            new HashMap<String, BitState>();


    /**
     * Creates a new Bit in the default stopped state
     */
    public Bit()
    {
        this.state = new BitStopped();

        this.dirty = false;

        // create the states for the Bit
        initStates();
    }

    /**
     * Used to set a position of interest to the user.
     * This can either be the starting or ending time of the Bit.
     *
     * @param position in milliseconds of the user's favorite part of a track.
     */
    public void setTime(long position)
    {
        state.setTime(position, this);
    }

    /**
     * Used to transition the state of a Bit from
     * Recording to Stopped and vice versus.
     *
     */
    public void transitionState()
    {
        // Switch between Recording and Stopped
        if(state instanceof BitRecording)
        {
            state = states.get("BitStopped");
        }
        else
        {
            state = states.get("BitRecording");
        }
    }


    /**
     * Creates a Recording and Stopped State for the Bit
     * if they are not already present.
     */
    private void initStates()
    {
        if((states.get("BitStopped")) == null)
        {
            states.put("BitStopped", new BitStopped());
        }

        if((states.get("BitRecording")) == null)
        {
            states.put("BitRecording", new BitRecording());
        }
    }

    /*
    * Getters and Setters
    */

    public String getTrackTitle()
    {
        return trackTitle;
    }

    public void setTrackTitle(String trackTitle)
    {
        this.trackTitle = trackTitle;
    }

    public String getArtist()
    {
        return artist;
    }

    public void setArtist(String artist)
    {
        this.artist = artist;
    }

    public String getPlatform()
    {
        return platform;
    }

    public void setPlatform(String platform)
    {
        this.platform = platform;
    }

    public BitState getState()
    {
        return state;
    }

    public void setState(BitState state)
    {
        this.state = state;
    }

    public long getStartTime()
    {
        return startTime;
    }

    public void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }

    public long getEndTime()
    {
        return endTime;
    }

    public void setEndTime(long endTime)
    {
        this.endTime = endTime;
    }

    public boolean isDirty()
    {
        return dirty;
    }

    public void setDirty(boolean dirty)
    {
        this.dirty = dirty;
    }

    /**
     *
     * @return String representation of the Bit
     */
    public String toString()
    {
        return (String.format("Track %s, Artist %s, platform %s, StartTime %d, EndTime %d",
                trackTitle, artist, platform, startTime, endTime));
    }
}
