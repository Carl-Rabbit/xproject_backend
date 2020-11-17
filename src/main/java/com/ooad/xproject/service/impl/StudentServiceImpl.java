package com.ooad.xproject.service.impl;

import com.ooad.xproject.entity.Student;
import com.ooad.xproject.mapper.StudentMapper;
import com.ooad.xproject.mapper.TeamMapper;
import com.ooad.xproject.service.StudentService;

import java.util.ArrayList;


public class StudentServiceImpl implements StudentService {
    private final StudentMapper studentMapper;

    public StudentServiceImpl(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    @Override
    public Student getStudent(Integer sid) {
        return studentMapper.selectByPrimaryKey(sid);
    }

    @Override
    public ArrayList<Student> getStudentList(Integer group) {
        return studentMapper.selectBySgpId(group);
    }

    @Override
    public ArrayList<Student> getTeammatesByTeamId(Integer tid) {
        return studentMapper.selectByTeamId(tid);
    }

    @Override
    public ArrayList<Student> getTeammatesByStdId(Integer sid) {
        int tid = studentMapper.selectTeamIdByStdId(sid);
        return studentMapper.selectByTeamId(tid);
    }
}
