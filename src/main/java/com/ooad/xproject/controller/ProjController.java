package com.ooad.xproject.controller;

import com.alibaba.fastjson.JSON;
import com.ooad.xproject.bo.TopicBO;
import com.ooad.xproject.entity.Project;
import com.ooad.xproject.service.HomeService;
import com.ooad.xproject.service.ProjectService;
import com.ooad.xproject.service.RoleService;
import com.ooad.xproject.vo.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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

//        Subject subject = SecurityUtils.getSubject();
//        String username = subject.getPrincipal().toString();
//
//        Role role = roleService.getByUsername(username);
//        List<Project> projList = homeService.getProjectList(role);

        Project proj = projectService.getProject(projId);
        return new Result<>(proj);
    }
}
