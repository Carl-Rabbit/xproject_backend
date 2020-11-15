package com.ooad.xproject.mapper;

import com.ooad.xproject.entity.Permission;

public interface PermissionMapper {
    int deleteByPrimaryKey(Integer pmsId);

    int insert(Permission record);

    int insertSelective(Permission record);

    Permission selectByPrimaryKey(Integer pmsId);

    int updateByPrimaryKeySelective(Permission record);

    int updateByPrimaryKey(Permission record);
}