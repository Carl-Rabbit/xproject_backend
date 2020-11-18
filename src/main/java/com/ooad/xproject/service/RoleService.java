package com.ooad.xproject.service;

import com.ooad.xproject.entity.Role;

import java.util.List;

public interface RoleService {
    Role getByUsername(String username);

    void createUser(String type, String username, String password);

    boolean validUsername(String username);

    boolean validPassword(String password);

    List<Role> selectAll(String roleType);
}
