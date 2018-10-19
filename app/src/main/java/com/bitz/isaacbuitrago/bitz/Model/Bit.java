package com.bitz.isaacbuitrago.bitz.Model;

/**
 * States of a Bit, either recording or stopped.
 */
 enum BitState{

     STOPPED,

    RECORDING
};

/**
 * Copyright Isaac Buitrago 2018, All rights reserved
 *
 * Represents data about a track which can be shared
 */
public class Bit
{
    private long startTime;         // start time of the Bit

    private long endTime;           // end time of the Bit

    private  String trackTitle;     // title of the Track

    private  String artist;         // name of artist

    private int year;               // year track was published

    private  String platform;       // streaming platform the track is on

    private BitState state;         // Bit state


    /**
     * Constructor, creates a new Bit in the default stopped state
     */
    public Bit()
    {
        this.state = BitState.STOPPED;
    }

    /**
     * Used to set a position of interest to the user.
     * This can be either the starting or ending time of the Bit.
     *
     * @param position in milliseconds of the user's favorite part of a track.
     */
    public void setTime(long position)
    {
        transitionState();

        if(state == BitState.STOPPED)
            endTime = position;

        else
            startTime = position;
    }

    /**
     * Used to change the state of a Bit from Stopped to
     * playing or vice versus.
     */
    private void transitionState()
    {
        if(state == BitState.STOPPED)
            state = BitState.RECORDING;

        else
            state = BitState.STOPPED;
    }

    /*
    * Getters and Setters
    */
    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getTrackTitle() {
        return trackTitle;
    }

    public void setTrackTitle(String trackTitle) {
        this.trackTitle = trackTitle;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public BitState getState() {
        return state;
    }

    public void setState(BitState state) {
        this.state = state;
    }

    /**
     *
     * @return String representation of the Bit
     */
    public String toString()
    {
        return (String.format("StartTime %d EndTime %d", startTime, endTime));
    }
}
