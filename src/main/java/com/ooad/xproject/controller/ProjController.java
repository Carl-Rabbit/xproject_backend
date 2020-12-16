package com.ooad.xproject.controller;

import com.alibaba.fastjson.JSON;
import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.bo.TopicBO;
import com.ooad.xproject.bo.forming.FormingBO;
import com.ooad.xproject.bo.forming.FormingResultBO;
import com.ooad.xproject.constant.RespStatus;
import com.ooad.xproject.constant.RoleType;
import com.ooad.xproject.dto.StudentProjDTO;
import com.ooad.xproject.entity.Project;
import com.ooad.xproject.entity.Role;
import com.ooad.xproject.service.HomeService;
import com.ooad.xproject.service.ProjectService;
import com.ooad.xproject.service.RoleService;
import com.ooad.xproject.vo.AutoFormingVO;
import com.ooad.xproject.vo.ProjectUpdateVO;
import com.ooad.xproject.vo.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProjController {

    private final RoleService roleService;
    private final HomeService homeService;
    private final ProjectService projectService;
    private final Logger logger = LogManager.getLogger(this.getClass().getName());

    public ProjController(RoleService roleService, HomeService homeService, ProjectService projectService) {
        this.roleService = roleService;
        this.homeService = homeService;
        this.projectService = projectService;
    }

    @ResponseBody
    @GetMapping("api/proj-topic")
    public Result<?> getProjTopics(@RequestParam(value="projId") int projId) {
        Project project = projectService.getProject(projId);
        List<TopicBO> topicList = JSON.parseArray(project.getTopics(), TopicBO.class);
        return new Result<>(topicList);
    }

    @ResponseBody
    @GetMapping("api/project/overview")
    public Result<Project> getProject(@RequestParam("projId") int projId) {
        logger.info("getProject");

        Subject subject = SecurityUtils.getSubject();
        String username = subject.getPrincipal().toString();

        Role role = roleService.getByUsername(username);

        if (!RoleType.Teacher.match(role.getRoleType())) {
            return new Result<>(RespStatus.UNAUTHORIZED);
        }

        List<Project> projList = homeService.getProjectList(role);

        boolean hasThisProj = false;
        for (Project proj: projList) {
            if (proj.getProjId() == projId) {
                hasThisProj = true;
                break;
            }
        }
        if (!hasThisProj) {
            return new Result<>(RespStatus.UNAUTHORIZED);
        }

        Project proj = projectService.getProject(projId);

        return new Result<>(proj);
    }

    @ResponseBody
    @PostMapping("api/teacher/update-overview")
    public Result<Boolean> updateProject(@RequestBody ProjectUpdateVO projectUpdateVO) {
        logger.info("updateProject");

        Subject subject = SecurityUtils.getSubject();
        String username = subject.getPrincipal().toString();

        Role role = roleService.getByUsername(username);

        if (!RoleType.Teacher.match(role.getRoleType())) {
            return new Result<>(RespStatus.UNAUTHORIZED);
        }

        List<Project> projList = homeService.getProjectList(role);

        boolean hasThisProj = false;
        for (Project proj: projList) {
            if (proj.getProjId() == projectUpdateVO.getProjId()) {
                hasThisProj = true;
                break;
            }
        }
        if (!hasThisProj) {
            return new Result<>(RespStatus.UNAUTHORIZED);
        }

        boolean success = projectService.updateProject(projectUpdateVO);

        if (success) {
            return new Result<>(true);
        } else {
            return new Result<>(RespStatus.FAIL, "Update failed", false);
        }
    }


    @ResponseBody
    @GetMapping("api/teacher/project/students")
    public Result<?> getProjStdList(@RequestParam(value="projId") int projId) {
        List<StudentProjDTO> stdProjDTOList = projectService.getStdProjList(projId);
        logger.info(String.format("getProjStdList -> %s", stdProjDTOList));
        return new Result<>(stdProjDTOList);
    }

    @ResponseBody
    @PostMapping("api/team/teacher/auto-forming")
    public Result<?> postAutoForming(@RequestBody AutoFormingVO autoFormingVO) {
        FormingBO formContext = new FormingBO();
        boolean success = formContext.setStrategy(autoFormingVO.getStrategy());
        if (!success) {
            return new Result<>(RespStatus.FAIL, "No such strategy");
        }
        formContext.setProjInstList(autoFormingVO.getProjInstList());
        formContext.setStdList(autoFormingVO.getStuList());

        SvResult<FormingResultBO> result = projectService.autoForming(formContext);

//        logger.info(String.format("getProjStdList -> %s", stdProjDTOList));
        FormingResultBO res = result.getData();
        res.setMatchList(null);
        return new Result<>(res);
    }

    @ResponseBody
    @GetMapping("api/student/team/ungrouped")
    public Result<?> getUngroupedStudents(@RequestParam(value="projId") int projId) {
        List<StudentProjDTO> stdProjDTOList = projectService.getStdProjList(projId);
        stdProjDTOList.removeIf(studentProjDTO -> studentProjDTO.getProjInstId() != null);
        logger.info(String.format("getProjStdList -> %s", stdProjDTOList));
        return new Result<>(stdProjDTOList);
    }
}
