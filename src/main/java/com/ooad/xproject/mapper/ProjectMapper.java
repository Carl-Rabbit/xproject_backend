package com.ooad.xproject.mapper;

import com.ooad.xproject.bo.RecordUnitBO;
import com.ooad.xproject.entity.Project;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectMapper {
    int deleteByPrimaryKey(Integer projId);

    int insert(Project record);

    int insertSelective(Project record);

    Project selectByPrimaryKey(Integer projId);

    int updateByPrimaryKeySelective(Project record);

    int updateByPrimaryKey(Project record);

    List<Project> selectByTchId(Integer tchId);

    List<Project> selectByStdId(Integer roleId);

    List<Project> selectAll();

    List<Project> selectBySchId(Integer schId);

    void joinProject(int roleId, int projId);

    List<RecordUnitBO> selectRecordUnitByProjId(Integer pid);
}