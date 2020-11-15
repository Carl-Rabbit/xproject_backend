package com.ooad.xproject.service;

import com.ooad.xproject.entity.Student;

import java.util.List;

public interface StudentService {
    Student getStudent();

    Student getStudent(Integer sid);

    List<Integer> getGroups();

    List<Student> getStudentList(Integer group);

}
