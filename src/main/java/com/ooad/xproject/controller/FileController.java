package com.ooad.xproject.controller;

import com.ooad.xproject.bo.RecordUnitBO;
import com.ooad.xproject.bo.StudentImportBO;
import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.config.FileConfig;
import com.ooad.xproject.constant.RespStatus;
import com.ooad.xproject.entity.RecordInst;
import com.ooad.xproject.entity.Role;
import com.ooad.xproject.entity.Teacher;
import com.ooad.xproject.mapper.RecordInstMapper;
import com.ooad.xproject.service.*;
import com.ooad.xproject.utils.RoleUtils;
import com.ooad.xproject.vo.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


// todo: role + database -> directory name -> upload/download
@RestController
public class FileController {

    private final FileConfig fileConfig;

    private final FileService fileService;

    private final ExcelService excelService;

    private final StudentService studentService;
    private final RoleService roleService;
    private final TeacherService teacherService;

    private final RecordService recordService;

    private final RecordInstMapper recordInstMapper;

    public FileController(FileConfig fileConfig, FileService fileService, ExcelService excelService, StudentService studentService, RoleService roleService, TeacherService teacherService, RecordService recordService, RecordInstMapper recordInstMapper) {
        this.fileConfig = fileConfig;
        this.fileService = fileService;
        this.excelService = excelService;
        this.studentService = studentService;
        this.roleService = roleService;
        this.teacherService = teacherService;
        this.recordService = recordService;
        this.recordInstMapper = recordInstMapper;
    }

    @PostMapping("api/upload")
    public String fileUpload(@RequestParam("file") MultipartFile[] files) {
        return fileService.upload(files, fileConfig.getUploadRoot());
    }


    @GetMapping("api/download")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, @RequestParam("path") String path
            , @RequestHeader("user-agent") String userAgent, @RequestParam("filename") String filename
            , @RequestParam(required = false, defaultValue = "false") boolean inline) {

        String realPath = fileConfig.getDownloadRoot() + path;
        return fileService.download(request, realPath, userAgent, filename, inline);
    }

    @GetMapping("api/teacher/records/export")
    public ResponseEntity<byte[]> getRecordUnitExportToExcel(HttpServletRequest request, @RequestParam("pid") Integer projId
            , @RequestHeader("user-agent") String userAgent, @RequestParam("filename") String filename
            , @RequestParam(required = false, defaultValue = "false") boolean inline) {

        SvResult<String> svResult = excelService.exportRecordUnitByProjId(projId);

        String realPath = svResult.getData();
        return fileService.download(request, realPath, userAgent, filename, inline);
    }

    @PostMapping("api/teacher/students/import")
    public Result<Integer> postStudentAcCreationFromExcel(@RequestParam("file") MultipartFile[] files) {
        String filePath = fileService.upload(files[0], fileConfig.getInputRoot(), "input.xlsx");
        System.out.println(filePath);
        List<StudentImportBO> studentImportBOList = excelService.readStudentImportBO(filePath);

        String username = RoleUtils.getUsername();
        Role role = roleService.getByUsername(username);

        Teacher teacher = teacherService.getTeacherByRoleId(role.getRoleId());

        int successCnt = 0;
        for (StudentImportBO studentImportBO :
                studentImportBOList) {
            SvResult<Boolean> svResult = studentService.creatRoleAndStudent(teacher.getSchId(), studentImportBO);
            if (svResult.getData()) {
                successCnt++;
            }
        }
        RespStatus status = (successCnt == 0) ? RespStatus.FAIL : RespStatus.SUCCESS;
        String msg = (successCnt == 0) ? "Create Student Account fail" : "Create Student Account done";
        return new Result<>(status, msg, successCnt);
    }


    @PostMapping("api/teacher/records/import")
    public Result<Integer> postRecordUnitImportFromExcel(@RequestParam("file") MultipartFile[] files, @RequestParam("projId") Integer projId) {
        String filePath = fileService.upload(files[0], fileConfig.getInputRoot(), "input.xlsx");
        System.out.println(filePath);
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
        String msg = (failCnt != 0) ? "Create Student Account fail" : "Create Student Account done";
        return new Result<>(status, msg, failCnt);
    }

}
