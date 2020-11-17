package com.ooad.xproject.mapper;

import com.ooad.xproject.entity.Project;

import java.util.ArrayList;

public interface ProjectMapper {
    int deleteByPrimaryKey(Integer projId);

    int insert(Project record);

    int insertSelective(Project record);

    Project selectByPrimaryKey(Integer projId);

    int updateByPrimaryKeySelective(Project record);

    int updateByPrimaryKey(Project record);

    ArrayList<Project> selectByTchId(Integer tchId);

    ArrayList<Project> selectByStdId(Integer stdId);

    ArrayList<Project> selectAll();
}