package com.example.godtierandroidapp.login;

/**
 * A small class representing entered user credentials.
 * @author Luke
 */
public class Users {
    private String username;
    private String pwd;

    public Users(String username, String pwd) {
        this.username = username;
        this.pwd = pwd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
