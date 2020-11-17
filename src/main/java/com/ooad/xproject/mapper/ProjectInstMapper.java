package com.ooad.xproject.mapper;

import com.ooad.xproject.entity.ProjectInst;

import java.util.ArrayList;

public interface ProjectInstMapper {
    int deleteByPrimaryKey(Integer projInstId);

    int insert(ProjectInst record);

    int insertSelective(ProjectInst record);

    ProjectInst selectByPrimaryKey(Integer projInstId);

    int updateByPrimaryKeySelective(ProjectInst record);

    int updateByPrimaryKey(ProjectInst record);

    ArrayList<ProjectInst> selectByProjId(Integer pid);
}