package com.ooad.xproject.controller;

import com.ooad.xproject.constant.RespStatus;
import com.ooad.xproject.dto.SbmDTO;
import com.ooad.xproject.entity.Role;
import com.ooad.xproject.entity.Submission;
import com.ooad.xproject.entity.Teacher;
import com.ooad.xproject.service.*;
import com.ooad.xproject.utils.RoleUtils;
import com.ooad.xproject.vo.Result;
import com.ooad.xproject.vo.SbmCreationVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ooad.xproject.vo.Result.createBoolResult;

@RestController
public class SubmissionController {

    private final RoleService roleService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final HomeService homeService;
    private final SbmService sbmService;
    private final Logger logger = LogManager.getLogger(this.getClass().getName());

    public SubmissionController(RoleService testService, StudentService studentService, TeacherService teacherService, HomeService homeService, SbmService sbmService) {
        this.roleService = testService;
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.homeService = homeService;
        this.sbmService = sbmService;
    }

    @ResponseBody
    @GetMapping("api/all/project/submission")
    public Result<?> getSbmListByProjId(@RequestParam(value="projId") int projId) {
        List<Submission> sbmList = sbmService.getSbmListByProjId(projId);
        List<SbmDTO> sbmDTOList = new ArrayList<>();
        for (Submission sbm: sbmList) {
            Teacher tch = teacherService.getTeacherByRoleId(sbm.getCreatorId());
            sbmDTOList.add(SbmDTO.builder()
                    .submission(sbm)
                    .teacher(tch)
                    .build());
        }
        logger.info("getSbmListByProjId -> " + Arrays.toString(sbmDTOList.toArray()));
        return new Result<>(sbmDTOList);
    }

    @ResponseBody
    @PostMapping("api/teacher/project/sub/add")
    public Result<?> postAddSubmission(@RequestBody SbmCreationVO sbmCreationVO) {
        String username = RoleUtils.getUsername();
        Role role = roleService.getByUsername(username);

        Submission sbm = new Submission();
        try {
            sbmCreationVO.copyToSubmission(sbm, role.getRoleId());
        } catch (ParseException e) {
            e.printStackTrace();
            return new Result<>(RespStatus.FAIL, "Format is not correct");
        }
        logger.info("postAddSubmission -> " + sbm);
        boolean success = sbmService.createSubmission(sbm);
        return createBoolResult(success, "Create submission successfully", "Fail to create");
    }

}
