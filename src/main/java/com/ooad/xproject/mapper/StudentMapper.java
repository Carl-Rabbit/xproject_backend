package com.ooad.xproject.mapper;

import com.ooad.xproject.entity.Student;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentMapper {
    int deleteByPrimaryKey(Integer stdId);

    int insert(Student record);

    int insertSelective(Student record);

    Student selectByPrimaryKey(Integer stdId);

    int updateByPrimaryKeySelective(Student record);

    int updateByPrimaryKey(Student record);

    Student selectByRoleId(Integer rid);

    List<Student> selectBySgpId(Integer sgpId);

    List<Student> selectByTeamId(Integer tid);

    int selectTeamIdByStdId(Integer sid);
}