package com.ooad.xproject.controller;

import com.ooad.xproject.dto.SbmDTO;
import com.ooad.xproject.entity.Submission;
import com.ooad.xproject.entity.Teacher;
import com.ooad.xproject.service.*;
import com.ooad.xproject.vo.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

}
