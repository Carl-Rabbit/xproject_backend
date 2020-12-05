package com.ooad.xproject.mapper;

import com.ooad.xproject.entity.School;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolMapper {
    int deleteByPrimaryKey(Integer schId);

    int insert(School record);

    int insertSelective(School record);

    School selectByPrimaryKey(Integer schId);

    int updateByPrimaryKeySelective(School record);

    int updateByPrimaryKey(School record);
}