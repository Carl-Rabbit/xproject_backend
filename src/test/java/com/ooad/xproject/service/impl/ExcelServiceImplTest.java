package com.ooad.xproject.service.impl;

import com.ooad.xproject.bo.RecordUnitBO;
import com.ooad.xproject.bo.StudentImportBO;
import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.config.FileConfig;
import com.ooad.xproject.constant.RespStatus;
import com.ooad.xproject.entity.Admin;
import com.ooad.xproject.entity.RecordInst;
import com.ooad.xproject.mapper.RecordInstMapper;
import com.ooad.xproject.service.*;
import com.ooad.xproject.vo.Result;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
class ExcelServiceImplTest {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private RecordService recordService;

    @Autowired
    private RecordInstMapper recordInstMapper;

    @Test
    void outputRecordUnitList() {
        FileConfig fileConfig = new FileConfig();
        String filePath = "C:\\BCSpace\\JetProjects\\JavaProject\\xproject_backend\\business\\output.xlsx";
        List<RecordUnitBO> recordUnitList = projectService.getRecordUnitList(1);

        excelService.generate(recordUnitList, filePath);

    }

    @Test
    void generateStudentImportBO() {

        FileConfig fileConfig = new FileConfig();
        String filePath = "C:\\BCSpace\\JetProjects\\JavaProject\\xproject_backend\\business\\output.xlsx";
        int clsFrom = 70, clsTo = 71; // 10 ~ 99
        int stdFrom = 10, stdTo = 11; // 10 ~ 99

        List<StudentImportBO> studentImportBOS = new ArrayList<>();
        for (int lab = clsFrom; lab <= clsTo; ++lab) {
            for (int i = stdFrom; i <= stdTo; ++i) {
                String stdId = "1181" + lab + i;
                StudentImportBO studentImportBO = new StudentImportBO();
                studentImportBO.setEmail(stdId + "@mail.sustech.edu.cn");
                studentImportBO.setStdClass("18" + lab);
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


    @Test
    void postStudentAcCreationFromExcel() {

        String filePath = "C:\\BCSpace\\JetProjects\\JavaProject\\xproject_backend\\business\\output.xlsx";
        System.out.println(filePath);
        List<StudentImportBO> studentImportBOList = excelService.readStudentImportBO(filePath);

        int successCnt = 0;
        for (StudentImportBO studentImportBO :
                studentImportBOList) {
            SvResult<Boolean> svResult = studentService.creatRoleAndStudent(1, studentImportBO);
            if (svResult.getData()) {
                successCnt++;
            }
        }
        RespStatus status = (successCnt == 0) ? RespStatus.FAIL : RespStatus.SUCCESS;
        String msg = (successCnt == 0) ? "Create Student Account fail" : "Create Student Account done";
        System.out.println(new Result<>(status, msg, successCnt).toString());
    }

    @Test
    void postRecordInstImportFromExcel() {
        Integer projId = 1;
        String filePath = "C:\\BCSpace\\JetProjects\\JavaProject\\xproject_backend\\business\\output.xlsx";
        List<RecordUnitBO> recordUnitBOList = excelService.readRecordUnitBO(filePath);

        int failCnt = 0;
        for (RecordUnitBO recordUnitBO : recordUnitBOList) {
            RecordInst recordInst = recordService.getRecordInstByUnit(recordUnitBO, projId);
            if (recordInst == null) {
                ++failCnt;
            } else {
                RecordInst recordInst1 = recordInstMapper.selectByRcdIdAndRoleId(recordInst.getRcdId(), recordInst.getRoleId());
                if (recordInst1 == null) {
                    recordInst.setComments(recordUnitBO.getComments());
                    recordInst.setContent(recordUnitBO.getGrade());
                    recordInstMapper.insertRecordInst(recordInst);
                } else {
                    recordInst1.setComments(recordUnitBO.getComments());
                    recordInst1.setContent(recordUnitBO.getGrade());
                    recordInstMapper.updateRecordInst(recordInst1);
                }
            }
        }
        RespStatus status = (failCnt != 0) ? RespStatus.FAIL : RespStatus.SUCCESS;
        String msg = (failCnt != 0) ? "upsert fail" : "upsert done";
        System.out.println(new Result<>(status, msg, failCnt).toString());
    }
}