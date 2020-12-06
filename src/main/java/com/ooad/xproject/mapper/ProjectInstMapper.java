package com.ooad.xproject.mapper;

import com.ooad.xproject.dto.StudentDTO;
import com.ooad.xproject.entity.ProjectInst;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectInstMapper {
    int deleteByPrimaryKey(Integer projInstId);

    int insert(ProjectInst record);

    int insertSelective(ProjectInst record);

    ProjectInst selectByPrimaryKey(Integer projInstId);

    int updateByPrimaryKeySelective(ProjectInst record);

    int updateByPrimaryKey(ProjectInst record);

    List<ProjectInst> selectByProjId(Integer pid);


    // extra

    List<StudentDTO> selectStudentByProjInstId(Integer projInstId);

    ProjectInst checkStdRoleId(int projId, int stdRoleId);
}