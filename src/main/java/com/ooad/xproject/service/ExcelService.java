package com.ooad.xproject.service;

import java.lang.reflect.Field;
import java.util.List;

public interface ExcelService {
    String generate(List<?> objectList, String filePath, String[] fields, String[] titles);

    String generate(List<?> objectList, String filePath, String[] fields);

    String generate(List<?> objectList, String filePath);


}
