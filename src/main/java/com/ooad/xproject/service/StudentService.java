package com.ooad.xproject.service;

import com.ooad.xproject.entity.Student;

import java.util.List;

public interface StudentService {

    Student getStudent(Integer sid);

    // list all the student groups
//    ArrayList<Integer> getStdGroupList();

    // select by group id, return all the students of this group
    List<Student> getStudentList(Integer group);

    // select by team id, return the teammates
    List<Student> getTeammatesByTeamId(Integer tid);

    List<Student> getTeammatesByStdId(Integer sid);

}
