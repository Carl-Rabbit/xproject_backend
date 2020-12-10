package com.ooad.xproject.service.impl;

import com.ooad.xproject.bo.StudentImportBO;
import com.ooad.xproject.entity.Admin;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ExcelServiceImplTest {

    @Test
    void generate() {
        String filePath = "C:\\BCSpace\\JetProjects\\JavaProject\\xproject_backend\\business\\input.xlsx";
        ExcelServiceImpl excelService = new ExcelServiceImpl();
        int clsFrom = 64, clsTo = 65; // 10 ~ 99
        int stdFrom = 10, stdTo = 60; // 10 ~ 99

        List<StudentImportBO> studentImportBOS = new ArrayList<>();
        for (int lab = clsFrom; lab <= clsTo; ++lab) {
            for (int i = stdFrom; i <= stdTo; ++i) {
                String stdId = "1181" + lab + i;
                StudentImportBO studentImportBO = new StudentImportBO();
                studentImportBO.setEmail(stdId + "@mail.sustech.edu.cn");
                studentImportBO.setStdClass("" + lab);
                studentImportBO.setStdNo(stdId);
                studentImportBO.setStdName("std" + lab + "_" + i);
                studentImportBO.setUsername("sustech" + stdId);
                studentImportBO.setPassword("sustech" + stdId);
                System.out.println(studentImportBO.getEmail());
                studentImportBOS.add(studentImportBO);
            }
        }

        System.out.println(excelService.generate(studentImportBOS, filePath));
    }

    @Test
    void getField() {
        Admin admin = new Admin();
        admin.setAdminId(1);
        admin.setRoleId(1);
        ExcelServiceImpl excelService = new ExcelServiceImpl();

        String[] strings = excelService.getField(admin.getClass());
        System.out.println(Arrays.toString(strings));
    }

    @Test
    void importExcel() {

        ExcelServiceImpl excelService = new ExcelServiceImpl();
        String filePath = "C:\\BCSpace\\JetProjects\\JavaProject\\xproject_backend\\business\\output.xlsx";
        List<StudentImportBO> studentImportBOList = excelService.readStudentImportBO(filePath);
        System.out.println(studentImportBOList.toString());
    }
}