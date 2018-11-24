package com.bitz.isaacbuitrago.bitz.Model;

/**
 * Represents the image for playlist.
 * Used to deserialize responses from an API.
 *
 * @author isaacbuitrago
 */
public class PlayListImage
{
    int height;

    int width;

    String url;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
