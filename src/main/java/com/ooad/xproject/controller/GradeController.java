package com.ooad.xproject.controller;

import com.ooad.xproject.dto.RecordInstDTO;
import com.ooad.xproject.entity.Role;
import com.ooad.xproject.service.ProjInstService;
import com.ooad.xproject.service.ProjectService;
import com.ooad.xproject.service.RoleService;
import com.ooad.xproject.vo.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class GradeController {

    private final RoleService roleService;
    private final ProjectService projectService;
    private final ProjInstService projInstService;
    private final Logger logger = LogManager.getLogger(this.getClass().getName());

    public GradeController(RoleService roleService, ProjectService projectService, ProjInstService projInstService) {
        this.roleService = roleService;
        this.projectService = projectService;
        this.projInstService = projInstService;
    }


    @ResponseBody
    @GetMapping("api/grade-list")
    public Result<?> getGradeList(@RequestParam(value="projId") int projId) {
        Subject subject = SecurityUtils.getSubject();
        String username = subject.getPrincipal().toString();
        Role role = roleService.getByUsername(username);

        List<RecordInstDTO> recordInstDTOList = projInstService.getRecordInstList(projId, role.getRoleId());

        logger.info("getTeamInfoList -> " + Arrays.toString(recordInstDTOList.toArray()));
        return new Result<>(recordInstDTOList);
    }
}
