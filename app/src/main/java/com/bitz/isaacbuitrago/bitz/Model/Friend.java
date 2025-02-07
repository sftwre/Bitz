package com.bitz.isaacbuitrago.bitz.Model;


/**
 * Friends of a user
 *
 * @author isaacbuitrago
 */
public class Friend
{

    private String id;

    private String userName;

    private String fullName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString()
    {
        return String.format("%s %s %s", id, userName, fullName);
    }
}
