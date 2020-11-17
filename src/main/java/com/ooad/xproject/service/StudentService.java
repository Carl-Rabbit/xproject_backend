package com.ooad.xproject.service;

import com.ooad.xproject.entity.Student;

import java.util.ArrayList;

public interface StudentService {
    Student getStudent();

    Student getStudent(Integer sid);

    // list all the groups
    ArrayList<Integer> getGroups();

    // select by group id, return all the students of this group
    ArrayList<Student> getStudentList(Integer group);

    // select by team id, return the teammates
    ArrayList<Student> getTeammates(Integer tid);

}
