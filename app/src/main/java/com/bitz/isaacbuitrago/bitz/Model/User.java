package com.bitz.isaacbuitrago.bitz.Model;

/**
 *
 * Account information for a user.
 *
 * @author isaacbuitrago
 */
public class User
{

    private String id;      // unique id

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private String password;

    public User(String firstName, String lastName, String username)
    {
        this.firstName = firstName;

        this.lastName = lastName;

        this.username = username;
    }


    /**
     * getters and setters
     * @return
     */
    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        // TODO hash

        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
