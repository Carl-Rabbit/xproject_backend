package com.ooad.xproject.service;

import com.ooad.xproject.bo.RecordUnitBO;
import com.ooad.xproject.bo.StudentImportBO;
import com.ooad.xproject.entity.Admin;
import com.ooad.xproject.entity.Student;

import java.lang.reflect.Field;
import java.util.List;

public interface ExcelService {
    String generate(List<?> objectList, String filePath, String[] fields, String[] titles);

    String generate(List<?> objectList, String filePath, String[] fields);

    String generate(List<?> objectList, String filePath);

    List<StudentImportBO> readStudentImportBO(String filePath);

    List<RecordUnitBO> readRecordUnitBO(String filePath);
}
