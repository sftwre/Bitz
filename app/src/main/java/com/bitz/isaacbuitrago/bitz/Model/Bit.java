package com.bitz.isaacbuitrago.bitz.Model;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.EventListener;

/**
 * Copyright Isaac Buitrago 2018, All rights reserved
 *
 * Represents data about a track which can be shared
 */
public class Bit
{
    private Duration startTime;    // start time of the Bit

    private Duration endTime;      // end time of the Bit

    private  String trackTitle;     // title of the Track

    private  String artist;         // name of the artist who preformed the work

    private int year;               // year track was published

    private  String platform;       // streaming platform the track is hosted on


    /*
    * Getters and Setters
    */
    public Duration getStartTime() {
        return startTime;
    }

    public void setStartTime(Duration startTime) {
        this.startTime = startTime;
    }

    public Duration getEndTime() {
        return endTime;
    }

    public void setEndTime(Duration endTime) {
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
}
