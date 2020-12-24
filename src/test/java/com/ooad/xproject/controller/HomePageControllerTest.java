package com.ooad.xproject.controller;

import com.ooad.xproject.entity.Role;
import com.ooad.xproject.entity.Student;
import com.ooad.xproject.service.StudentService;
import com.ooad.xproject.utils.RoleUtils;
import com.ooad.xproject.vo.Result;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class HomePageControllerTest {
    @Autowired
    private StudentService studentService;

    @Test
    void getComments() {

        Student student = studentService.getStudentByRoleId(5);
        String[] comments;
        System.out.println(student.getPayload());
        if (student.getPayload() == null) {
            comments = null;
        } else {
            comments = student.getPayload().split(";");
        }
        for (String str : comments) {
            System.out.println(str);
        }
    }
}