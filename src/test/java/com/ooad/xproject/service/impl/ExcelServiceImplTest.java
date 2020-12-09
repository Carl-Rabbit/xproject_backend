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
        String filePath = "C:\\BCSpace\\JetProjects\\JavaProject\\xproject_backend\\business\\output.xlsx";
//        String[] strings = new String[2];
//        strings[0] = "col1";
//        strings[1] = "col2";

        ExcelServiceImpl excelService = new ExcelServiceImpl();

//        String[] fields = excelService.getField(Admin.class);

        List<StudentImportBO> studentImportBOS = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            StudentImportBO studentImportBO = new StudentImportBO();
            studentImportBO.setEmail("email of " + i);
            studentImportBO.setStdClass("class of " + i);
            studentImportBO.setStdNo("stdNo of " + i);
            studentImportBO.setStdName("name of " + i);
            studentImportBO.setUsername("username of " + i);
            studentImportBO.setPassword("password of " + i);
            studentImportBOS.add(studentImportBO);
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