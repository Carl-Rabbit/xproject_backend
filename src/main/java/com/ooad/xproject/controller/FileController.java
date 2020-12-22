package com.ooad.xproject.controller;

import com.ooad.xproject.bo.RecordUnitBO;
import com.ooad.xproject.bo.StudentClassBO;
import com.ooad.xproject.bo.StudentImportBO;
import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.config.FileConfig;
import com.ooad.xproject.constant.RespStatus;
import com.ooad.xproject.entity.*;
import com.ooad.xproject.mapper.ProjectMapper;
import com.ooad.xproject.mapper.RecordInstMapper;
import com.ooad.xproject.mapper.ResourceMapper;
import com.ooad.xproject.service.*;
import com.ooad.xproject.utils.RoleUtils;
import com.ooad.xproject.vo.ResourceVO;
import com.ooad.xproject.vo.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
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
    private final ProjectMapper projectMapper;
    private final SubmissionInstService submissionInstService;

    private final PermissionService permissionService;

    private final ResourceMapper resourceMapper;

    public FileController(FileConfig fileConfig, FileService fileService, ExcelService excelService, StudentService studentService, RoleService roleService, TeacherService teacherService, RecordService recordService, RecordInstMapper recordInstMapper, ProjectMapper projectMapper, SubmissionInstService submissionInstService, PermissionService permissionService, ResourceMapper resourceMapper) {
        this.fileConfig = fileConfig;
        this.fileService = fileService;
        this.excelService = excelService;
        this.studentService = studentService;
        this.roleService = roleService;
        this.teacherService = teacherService;
        this.recordService = recordService;
        this.recordInstMapper = recordInstMapper;
        this.projectMapper = projectMapper;
        this.submissionInstService = submissionInstService;
        this.permissionService = permissionService;
        this.resourceMapper = resourceMapper;
    }

    @PostMapping("api/upload")
    public String fileUpload(@RequestParam("file") MultipartFile[] files) {
        return fileService.upload(files, fileConfig.getUploadRoot());
    }


    @GetMapping("api/download")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request, @RequestParam("path") String path
            , @RequestHeader("user-agent") String userAgent, @RequestParam("filename") String filename
            , @RequestParam(required = false, defaultValue = "false") boolean inline) {

        String realPath = fileConfig.getDownloadRoot() + "\\" + path;
        return fileService.download(request, realPath, userAgent, filename, inline);
    }

    @GetMapping("api/teacher/records/export")
    public ResponseEntity<byte[]> getRecordUnitExportToExcel(HttpServletRequest request, @RequestParam("projId") Integer projId
            , @RequestHeader("user-agent") String userAgent, @RequestParam("filename") String filename
            , @RequestParam(required = false, defaultValue = "false") boolean inline) {

        SvResult<String> svResult = excelService.exportRecordUnitByProjId(projId);

        String realPath = svResult.getData();
        return fileService.download(request, realPath, userAgent, filename, inline);
    }

    @GetMapping("api/teacher/team/excel")
    public ResponseEntity<byte[]> getTeamExcel(HttpServletRequest request, @RequestParam("projId") Integer projId
            , @RequestHeader("user-agent") String userAgent, @RequestParam("filename") String filename
            , @RequestParam(required = false, defaultValue = "false") boolean inline) {

        SvResult<String> svResult = excelService.exportTeamByProjId(projId);

        String realPath = svResult.getData();
        return fileService.download(request, realPath, userAgent, filename, inline);
    }

    @PostMapping("api/teacher/students/excel")
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
            SvResult<Role> svResult = studentService.creatRoleAndStudent(teacher.getSchId(), studentImportBO);
            if (svResult.getData() != null) {
                boolean success = permissionService.appendPmsRoleToNewRole(svResult.getData());
                if (success) {
                    successCnt++;
                }
            }
        }
        RespStatus status = (successCnt == 0) ? RespStatus.FAIL : RespStatus.SUCCESS;
        String msg = (successCnt == 0) ? "Create Student Account fail" : "Create Student Account done";
        return new Result<>(status, msg, successCnt);
    }


    @PostMapping("api/teacher/records/excel")
    public Result<Integer> postRecordUnitImportFromExcel(@RequestParam("file") MultipartFile[] files, @RequestParam("projId") Integer projId) {
        String filePath = fileService.upload(files[0], fileConfig.getInputRoot(), "input.xlsx");
//        System.out.println(filePath);
        List<RecordUnitBO> recordUnitBOList = excelService.readRecordUnitBO(filePath);

        int successCnt = 0;
        for (RecordUnitBO recordUnitBO : recordUnitBOList) {
            RecordInst recordInst = recordService.getRecordInstByUnit(recordUnitBO, projId);
            if (recordInst != null) {
                ++successCnt;
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
        boolean check = (successCnt == 0);
        RespStatus status = (check) ? RespStatus.FAIL : RespStatus.SUCCESS;
        String msg = (check) ? "Upsert record fail" : "Upsert record done";
        return new Result<>(status, msg, successCnt);
    }

    @PostMapping("api/teacher/project/student/excel")
    public Result<Integer> postProjStdExcel(@RequestParam("file") MultipartFile[] files, @RequestParam("projId") Integer projId) {
        String filePath = fileService.upload(files[0], fileConfig.getInputRoot(), "input.xlsx");
//        System.out.println(filePath);
        List<StudentClassBO> studentClassBOList = excelService.readStudentClassBO(filePath);

        int successCnt = 0;
        for (StudentClassBO studentClassBO : studentClassBOList) {
            Student student = studentService.getStudentByStdNo(studentClassBO.getStdNo());
            if (student != null) {
                ++successCnt;
                List<Integer> prrIdList = projectMapper.selectByProjAndRole(projId, student.getRoleId());
                if (prrIdList.size() == 0) {
                    projectMapper.insertProjectRoleRT(projId, student.getRoleId(), studentClassBO.getClsMark());
                } else {
                    projectMapper.updateProjectRoleRT(prrIdList.get(0), studentClassBO.getClsMark());
                }
            }
        }
        boolean check = (successCnt == 0);
        RespStatus status = (check) ? RespStatus.FAIL : RespStatus.SUCCESS;
        String msg = (check) ? "Upsert student to project fail" : "Upsert student to project done";
        return new Result<>(status, msg, successCnt);
    }

    // todo: upsert database
    @PostMapping("api/student/submission/upload")
    public Result<?> postUploadSubmission(@RequestParam("file") MultipartFile[] files,
                                          @RequestParam("sbmId") int sbmId,
                                          @RequestParam("projInstId") int projInstId) {

        Role role = roleService.getByUsername(RoleUtils.getUsername());
        SubmissionInst submissionInst = new SubmissionInst();
        submissionInst.setSbmId(sbmId);
        submissionInst.setProjInstId(projInstId);
        submissionInst.setSubmitterId(role.getRoleId());
        if (submissionInstService.upsertSubmissionInst(submissionInst) == 0) {
            return new Result<>(RespStatus.FAIL);
        }
        File studentDir = fileService.getOrCreateStudentDir(submissionInst);

        fileService.deleteFilesExceptFolder(studentDir);
        return new Result<>(fileService.upload(files, studentDir.getPath()));
    }

    @GetMapping("api/teacher/submission/download")
    public ResponseEntity<byte[]> getAllSbmFiles(HttpServletRequest request, @RequestParam("sbmId") int sbmId
            , @RequestHeader("user-agent") String userAgent
            , @RequestParam(required = false, defaultValue = "false") boolean inline) {

        File file = fileService.getSbmDir(sbmId);
        String outputPath = fileConfig.getOutputRoot() + "\\" + "output.zip";
        SvResult<String> svResult = fileService.compressDir(file, outputPath);
        return fileService.download(request, outputPath, userAgent, "output.zip", inline);
    }

    @PostMapping("api/teacher/resource/upload")
    public Result<?> postResources(@RequestParam("file") MultipartFile[] files,
                                   @RequestParam("projId") int projId) {

        Role role = roleService.getByUsername(RoleUtils.getUsername());
        int creatorId = role.getRoleId();
        int successCnt = 0;
        for (MultipartFile file : files) {
            if (fileService.uploadResource(file, projId, creatorId) > 0) {
                ++successCnt;
            }
        }

        boolean check = (successCnt == 0);
        RespStatus status = (check) ? RespStatus.FAIL : RespStatus.SUCCESS;
        String msg = (check) ? "Upload resource fail" : "Upload resource done";
        return new Result<>(status, msg, successCnt);
    }

    @GetMapping("api/all/resource/download")
    public ResponseEntity<byte[]> getRecource(HttpServletRequest request, @RequestParam("srcId") int srcId
            , @RequestHeader("user-agent") String userAgent
            , @RequestParam(required = false, defaultValue = "false") boolean inline) {

        File file = fileService.getResDir(srcId);
        String outputPath = fileConfig.getOutputRoot() + "\\" + "output.zip";
        SvResult<String> svResult = fileService.compressDir(file, outputPath);
        return fileService.download(request, outputPath, userAgent, "output.zip", inline);
    }

    @ResponseBody
    @GetMapping("api/all/resource/list")
    public Result<?> getResourceList(@RequestParam("projId") int projId) {

        List<Resource> resources = resourceMapper.selectByProjId(projId);

        List<ResourceVO> resourceVOS = new ArrayList<>();

        for (Resource resource : resources) {
            Teacher teacher = teacherService.getTeacherByRoleId(resource.getCreatorRoleId());
            ResourceVO resourceVO = ResourceVO.builder().resource(resource).creator(teacher).build();
            resourceVOS.add(resourceVO);
        }
        return new Result<>(resourceVOS);
    }

    @ResponseBody
    @GetMapping("api/teacher/project/resource/del")
    public Result<?> getDeleteResources(@RequestParam("srcId") int srcId) {
        Resource resource = resourceMapper.selectByPrimaryKey(srcId);
        if (resource == null) {
            return new Result<>(RespStatus.FAIL);
        }
        File file = fileService.getResDir(srcId);
        fileService.deleteFilesAndFolder(file);

        return new Result<>(resourceMapper.deleteByPrimaryKey(srcId));
    }
}
