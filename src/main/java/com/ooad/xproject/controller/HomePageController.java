package com.ooad.xproject.controller;

import com.ooad.xproject.bo.SvRoleInfo;
import com.ooad.xproject.constant.RespStatus;
import com.ooad.xproject.entity.Project;
import com.ooad.xproject.entity.Role;
import com.ooad.xproject.service.HomeService;
import com.ooad.xproject.service.RoleService;
import com.ooad.xproject.service.StudentService;
import com.ooad.xproject.vo.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HomePageController {

    private final RoleService roleService;
    private final StudentService studentService;
    private final HomeService homeService;
    private final Logger logger = LogManager.getLogger(this.getClass().getName());

    public HomePageController(RoleService roleService, StudentService studentService, HomeService homeService) {
        this.roleService = roleService;
        this.studentService = studentService;
        this.homeService = homeService;
    }

    @ResponseBody
    @GetMapping("api/home-page-info")
    public Result<?> getRoleInfo() {
        String data = "I have a great plan, but there's not enough room here.";
        return new Result<>(RespStatus.SUCCESS, "only a test", data);
    }

    @ResponseBody
    @GetMapping("api/project-list")
    public Result<?> getProjectList() {
        logger.info("getProjectList");
        Subject subject = SecurityUtils.getSubject();
        String username = subject.getPrincipal().toString();

        Role role = roleService.getByUsername(username);
        SvRoleInfo roleInfo = roleService.getRoleInfo(role);
        List<Project> projList = homeService.getProjectList(roleInfo);
        return new Result<>(projList);
    }
}
