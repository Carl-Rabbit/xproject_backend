package com.ooad.xproject.service.impl;

import com.ooad.xproject.service.ExcelService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.jam.JField;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

@Service
public class ExcelServiceImpl implements ExcelService {

//    objectList: List of data
//    filePath: output path of excel
//    fields: Output list of fields of the class of data
//    titles: first row
    @Override
    public String generate(List<?> objectList, String filePath, String[] fields, String[] titles) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        int rowCount = 0;

        Row row = sheet.createRow(rowCount++);

        for (int i = 0; i < titles.length; ++i) {
            Cell cell = row.createCell(i);
            cell.setCellValue(titles[i]);
        }

        for (Object o : objectList) {
            row = sheet.createRow(rowCount++);
            for (int j = 0; j < fields.length; ++j) {
                Cell cell = row.createCell(j);
                String str;
                try {
                    str = Objects.requireNonNull(getFieldValueByName(fields[j], o)).toString();
                } catch (Exception e) {
                    str = "";
                }
                cell.setCellValue(str);
            }
        }

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath + ", rowCount: " + rowCount;
    }

//  titles = fields
    @Override
    public String generate(List<?> objectList, String filePath, String[] fields) {
        return generate(objectList, filePath, fields, fields);
    }

//    fields = all the fields in class of data
//    titles = fields
    @Override
    public String generate(List<?> objectList, String filePath) {
        String[] fields;
        fields = new String[0];

        if(!objectList.isEmpty())
            fields = getField(objectList.get(0).getClass());

        return generate(objectList, filePath, fields, fields);
    }

    public String[] getField(Class cls) {
        Field[] fields=cls.getDeclaredFields();
        String[] fieldNames=new String[fields.length];
        for(int i=0;i<fields.length;i++){
//            System.out.println(fields[i].getType());
            fieldNames[i]=fields[i].getName();
        }
        return fieldNames;
    }

    private static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            Object value = method.invoke(o, new Object[] {});
            return value;
        } catch (Exception e) {

            return null;
        }
    }
}
