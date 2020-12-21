package com.ooad.xproject.mapper;

import com.ooad.xproject.entity.Resource;
import org.springframework.stereotype.Repository;

@Repository

public interface ResourceMapper {
    int deleteByPrimaryKey(Integer srcId);

    int insert(Resource record);

    int insertSelective(Resource record);

    Resource selectByPrimaryKey(Integer srcId);

    int updateByPrimaryKeySelective(Resource record);

    int updateByPrimaryKey(Resource record);
}