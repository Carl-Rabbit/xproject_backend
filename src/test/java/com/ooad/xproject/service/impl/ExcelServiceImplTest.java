package com.ooad.xproject.service.impl;

import com.ooad.xproject.entity.Admin;
import com.ooad.xproject.service.ExcelService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ExcelServiceImplTest {

    @Test
    void generate() {
        String filePath = "C:\\BCSpace\\JetProjects\\JavaProject\\xproject_backend\\business\\output.xlsx";
        String[] strings = new String[2];
        strings[0] = "col1";
        strings[1] = "col2";

        ExcelServiceImpl excelService = new ExcelServiceImpl();

        String[] fields = excelService.getField(Admin.class);

        List<Admin> admin = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            Admin a = new Admin();
            a.setRoleId(i);
            a.setAdminId(i);
            admin.add(a);
        }

        System.out.println(excelService.generate(admin, filePath, fields, strings));
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
}