package com.ooad.xproject.service.impl;

import com.ooad.xproject.entity.Permission;
import com.ooad.xproject.mapper.PermissionMapper;
import com.ooad.xproject.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;

    public PermissionServiceImpl(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public Set<String> listPmsURLsByUsername(String username) {
        List<Permission> pmsList = permissionMapper.getAllPmsByUsername(username);
        return pmsList.stream().map(Permission::getUrl).collect(Collectors.toSet());
    }

    @Override
    public boolean needFilter(String requestAPI) {
        return permissionMapper.containPmsUrl(requestAPI);
    }
}
