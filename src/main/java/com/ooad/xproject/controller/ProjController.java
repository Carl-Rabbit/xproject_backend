package com.ooad.xproject.controller;

import com.alibaba.fastjson.JSON;
import com.ooad.xproject.bo.TopicBO;
import com.ooad.xproject.entity.Project;
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
    private final ProjectService projectService;
    private final Logger logger = LogManager.getLogger(this.getClass().getName());

    public ProjController(RoleService roleService, ProjectService projectService) {
        this.roleService = roleService;
        this.projectService = projectService;
    }

    @ResponseBody
    @GetMapping("api/proj-topic")
    public Result<?> getProjTopics(@RequestParam(value="projId") int projId) {
        Project project = projectService.getProject(projId);
        List<TopicBO> topicList = JSON.parseArray(project.getTopics(), TopicBO.class);
        return new Result<>(topicList);
    }
}
