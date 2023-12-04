package com.example.godtierandroidapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.example.godtierandroidapp.login.Users;

import org.junit.Test;

public class UsersTest {

    @Test
    public void getUsername_returnsCorrectUsername() {
        Users user = new Users("testUser", "password123");
        assertEquals("testUser", user.getUsername());
    }

    @Test
    public void setUsername_changesUsername() {
        Users user = new Users("oldUser", "password123");
        user.setUsername("newUser");
        assertEquals("newUser", user.getUsername());
    }

    @Test
    public void getPwd_returnsCorrectPassword() {
        Users user = new Users("testUser", "password123");
        assertEquals("password123", user.getPwd());
    }

    @Test
    public void setPwd_changesPassword() {
        Users user = new Users("testUser", "oldPassword");
        user.setPwd("newPassword");
        assertEquals("newPassword", user.getPwd());
    }

    @Test
    public void constructor_setsInitialValues() {
        Users user = new Users("testUser", "password123");
        assertEquals("testUser", user.getUsername());
        assertEquals("password123", user.getPwd());
    }

    @Test
    public void setUsername_withNull_doesNotThrowException() {
        Users user = new Users("testUser", "password123");
        // Attempting to set null should not throw an exception
        user.setUsername(null);
        assertNull(user.getUsername());
    }

    @Test
    public void setPwd_withNull_doesNotThrowException() {
        Users user = new Users("testUser", "password123");
        // Attempting to set null should not throw an exception
        user.setPwd(null);
        assertNull(user.getPwd());
    }

}
