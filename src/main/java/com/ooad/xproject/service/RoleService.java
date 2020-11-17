package com.ooad.xproject.service;

import com.ooad.xproject.bo.SvRoleInfo;
import com.ooad.xproject.entity.Role;

public interface RoleService {


    Role getByUsername(String username);

    SvRoleInfo getRoleInfo(Integer rid);

    void createUser(String type, String username, String password);

    boolean validUsername(String username);

    boolean validPassword(String password);
}
