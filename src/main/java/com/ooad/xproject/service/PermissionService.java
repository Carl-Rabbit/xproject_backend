package com.ooad.xproject.service;

import java.util.Set;

public interface PermissionService {
    Set<String> listPmsURLsByUsername(String username);

    boolean needFilter(String requestAPI);
}
