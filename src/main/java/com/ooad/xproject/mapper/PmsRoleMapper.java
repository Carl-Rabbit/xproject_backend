package com.ooad.xproject.mapper;

import com.ooad.xproject.entity.PmsRole;

public interface PmsRoleMapper {
    int deleteByPrimaryKey(Integer pmsRoleId);

    int insert(PmsRole record);

    int insertSelective(PmsRole record);

    PmsRole selectByPrimaryKey(Integer pmsRoleId);

    int updateByPrimaryKeySelective(PmsRole record);

    int updateByPrimaryKey(PmsRole record);
}