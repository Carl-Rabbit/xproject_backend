package com.ooad.xproject.mapper;

import com.ooad.xproject.entity.Resource;

public interface ResourceMapper {
    int deleteByPrimaryKey(Integer srcId);

    int insert(Resource record);

    int insertSelective(Resource record);

    Resource selectByPrimaryKey(Integer srcId);

    int updateByPrimaryKeySelective(Resource record);

    int updateByPrimaryKey(Resource record);
}