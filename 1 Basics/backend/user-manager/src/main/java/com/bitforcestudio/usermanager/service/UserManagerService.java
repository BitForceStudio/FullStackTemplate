package com.bitforcestudio.usermanager.service;

import com.bitforcestudio.usermanager.entities.User;

public interface UserManagerService {

    public String signup(String username, String password);

    public User login(String username, String password);

    public String logout(String username);
}