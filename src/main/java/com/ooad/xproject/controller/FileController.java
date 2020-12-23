package com.ooad.xproject.controller;

import com.ooad.xproject.bo.RecordUnitBO;
import com.ooad.xproject.bo.StudentClassBO;
import com.ooad.xproject.bo.StudentImportBO;
import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.config.FileConfig;
import com.ooad.xproject.constant.RespStatus;
import com.ooad.xproject.dto.StudentDTO;
import com.ooad.xproject.entity.*;
import com.ooad.xproject.mapper.ProjectMapper;
import com.ooad.xproject.mapper.RecordInstMapper;
import com.ooad.xproject.mapper.ResourceMapper;
import com.ooad.xproject.mapper.SubmissionMapper;
import com.ooad.xproject.service.*;
import com.ooad.xproject.utils.RoleUtils;
import com.ooad.xproject.vo.ResourceVO;
import com.ooad.xproject.vo.Result;
import com.ooad.xproject.vo.UploadFileProjIdVO;
import com.ooad.xproject.vo.UploadSbmVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.ooad.xproject.constant.ProjInstStatus.Confirm;


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
    private final ProjInstService projInstService;
    private final SubmissionInstService submissionInstService;

    private final PermissionService permissionService;

    private final SubmissionMapper submissionMapper;

    private final ResourceMapper resourceMapper;

    private final ProjectService projService;
    private final MailService mailService;

    public FileController(FileConfig fileConfig, FileService fileService, ExcelService excelService, StudentService studentService, RoleService roleService, TeacherService teacherService, RecordService recordService, RecordInstMapper recordInstMapper, ProjectMapper projectMapper, ProjInstService projInstService, SubmissionInstService submissionInstService, PermissionService permissionService, SubmissionMapper submissionMapper, ResourceMapper resourceMapper, ProjectService projService, MailService mailService) {
        this.fileConfig = fileConfig;
        this.fileService = fileService;
        this.excelService = excelService;
        this.studentService = studentService;
        this.roleService = roleService;
        this.teacherService = teacherService;
        this.recordService = recordService;
        this.recordInstMapper = recordInstMapper;
        this.projectMapper = projectMapper;
        this.projInstService = projInstService;
        this.submissionInstService = submissionInstService;
        this.permissionService = permissionService;
        this.submissionMapper = submissionMapper;
        this.resourceMapper = resourceMapper;
        this.projService = projService;
        this.mailService = mailService;
    }

    @PostMapping("api/upload")
    public String fileUpload(@RequestParam("files") MultipartFile[] files) {
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
            , @RequestHeader("user-agent") String userAgent
            , @RequestParam(required = false, defaultValue = "false") boolean inline) {

        SvResult<String> svResult = excelService.exportRecordUnitByProjId(projId);

        String realPath = svResult.getData();
        return fileService.download(request, realPath, userAgent, "output.xlsx", inline);
    }

    @GetMapping("api/teacher/team/excel")
    public ResponseEntity<byte[]> getTeamExcel(HttpServletRequest request, @RequestParam("projId") Integer projId
            , @RequestHeader("user-agent") String userAgent
            , @RequestParam(required = false, defaultValue = "false") boolean inline) {

        SvResult<String> svResult = excelService.exportTeamByProjId(projId);

        String realPath = svResult.getData();
        return fileService.download(request, realPath, userAgent, "output.xlsx", inline);
    }

    @PostMapping("api/teacher/students/excel")
    public Result<Integer> postStudentAcCreationFromExcel(@RequestParam("files") MultipartFile[] files) {
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
    public Result<Integer> postRecordUnitImportFromExcel(@RequestBody UploadFileProjIdVO uploadFileProjIdVO) {
        MultipartFile[] files = uploadFileProjIdVO.getFiles();
        int projId = uploadFileProjIdVO.getProjId();

        // check project accessible
        if (!projService.isAccessible(projId)) {
            return new Result<>(RespStatus.FAIL, "Project is not accessible");
        }

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
    public Result<Integer> postProjStdExcel(@RequestBody UploadFileProjIdVO uploadFileProjIdVO) {
        MultipartFile[] files = uploadFileProjIdVO.getFiles();
        int projId = uploadFileProjIdVO.getProjId();

        // check project accessible
        if (!projService.isAccessible(projId)) {
            return new Result<>(RespStatus.FAIL, "Project is not accessible");
        }

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

    @PostMapping("api/student/submission/upload")
    public Result<?> postUploadSubmission(@RequestBody UploadSbmVO uploadSbmVO) {
        MultipartFile[] files = uploadSbmVO.getFiles();
        Integer sbmId = uploadSbmVO.getSbmId();

        Role role = roleService.getByUsername(RoleUtils.getUsername());
        StringBuilder attachment = new StringBuilder();
        for (MultipartFile file : files) {
            attachment.append(file.getOriginalFilename()).append(";");
        }

        Submission submission = submissionMapper.selectByPrimaryKey(sbmId);

        // check project accessible
        if (!projService.isAccessible(submission.getProjId())) {
            return new Result<>(RespStatus.FAIL, "Project is not accessible");
        }

        ProjectInst projectInst = projInstService.getPIByProjIdAndStdRoleId(submission.getProjId(), role.getRoleId());

        if (projectInst == null) {
            return new Result<>(RespStatus.FAIL, "No team yet");
        } else if (!projectInst.getStatus().equals(Confirm.name())) {
            return new Result<>(RespStatus.FAIL, "Team is not confirm");
        }

        SubmissionInst submissionInst = new SubmissionInst();
        submissionInst.setSbmId(sbmId);
        submissionInst.setProjInstId(projectInst.getProjInstId());
        submissionInst.setSubmitterId(role.getRoleId());
        submissionInst.setAttachments(attachment.toString());
        if (submissionInstService.upsertSubmissionInst(submissionInst) == 0) {
            return new Result<>(RespStatus.FAIL, "No chance left");
        }
        File studentDir = fileService.getOrCreateStudentDir(submissionInst);

        // send email to all students
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());

        List<StudentDTO> stdList = projInstService.getStudentDTOByProjInstId(projectInst.getProjInstId());
        List<String> mailList = stdList.stream().map(StudentDTO::getEmail).collect(Collectors.toList());
        mailService.sendMailToStudent(mailList, "[XProject] Your team has submitted an assignment",
                "Your team has submitted an assignment\r\n" +
                        "Title: " + submission.getTitle() + "\r\n" +
                        "Submitter: " + role.getUsername() + "\r\n" +
                        "Submission time: " + formatter.format(date) + "\r\n" +
                        "This automatic notification message was sent by Xproject");

        fileService.deleteFilesExceptFolder(studentDir);
        return new Result<>(fileService.upload(files, studentDir.getPath()));
    }

    @GetMapping("api/teacher/submission/download")
    public ResponseEntity<byte[]> getAllSbmFiles(HttpServletRequest request, @RequestParam("sbmId") int sbmId
            , @RequestHeader("user-agent") String userAgent
            , @RequestParam(required = false, defaultValue = "false") boolean inline) {

        Submission submission = submissionMapper.selectByPrimaryKey(sbmId);

        // check project accessible
        if (!projService.isAccessible(submission.getProjId())) {
            // TODO LYZ
            return null;
        }

        File file = fileService.getSbmDir(sbmId);
        String outputPath = fileConfig.getOutputRoot() + "\\" + "output.zip";
        SvResult<String> svResult = fileService.compressDir(file, outputPath);
        return fileService.download(request, outputPath, userAgent, "output.zip", inline);
    }

    @PostMapping("api/teacher/resource/upload")
    public Result<?> postResources(@RequestBody UploadFileProjIdVO uploadFileProjIdVO) {
        MultipartFile[] files = uploadFileProjIdVO.getFiles();
        int projId = uploadFileProjIdVO.getProjId();

        // check project accessible
        if (!projService.isAccessible(projId)) {
            return new Result<>(RespStatus.FAIL, "Project is not accessible");
        }

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
    public ResponseEntity<byte[]> getResource(HttpServletRequest request, @RequestParam("srcId") int srcId
            , @RequestHeader("user-agent") String userAgent
            , @RequestParam(required = false, defaultValue = "false") boolean inline) {

        Resource resource = resourceMapper.selectByPrimaryKey(srcId);

        if (resource == null) {
            // TODO LYZ
            return null;
        }

        // check project accessible
        if (!projService.isAccessible(resource.getProjId())) {
            // TODO LYZ
            return null;
        }

        File file = fileService.getResDir(srcId);
        String targetPath = file.getPath() + "\\" + resource.getFileName();
        return fileService.download(request, targetPath, userAgent, resource.getFileName(), inline);
    }

    @ResponseBody
    @GetMapping("api/all/resource/list")
    public Result<?> getResourceList(@RequestParam("projId") int projId) {

        // check project accessible
        if (!projService.isAccessible(projId)) {
            return new Result<>(RespStatus.FAIL, "Project is not accessible");
        }

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
            return new Result<>(RespStatus.FAIL, "No such resource");
        }

        // check project accessible
        if (!projService.isAccessible(resource.getProjId())) {
            return new Result<>(RespStatus.FAIL, "Project is not accessible");
        }
        File file = fileService.getResDir(srcId);
        fileService.deleteFilesAndFolder(file);

        return new Result<>(resourceMapper.deleteByPrimaryKey(srcId));
    }
}
