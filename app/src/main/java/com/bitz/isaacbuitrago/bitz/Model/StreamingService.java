package com.bitz.isaacbuitrago.bitz.Model;

/**
 *
 * Responsible for storing about a streaming service.
 *
 * @author isaacbuitrago
 */
public class StreamingService
{

    private String name;            // name of streaming service

    private int imageResourceId;    // id of logo for service

    public StreamingService(String name, int imageResourceId)
    {
        this.name = name;

        this.imageResourceId = imageResourceId;
    }

    public String getName() {
        return name;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}
