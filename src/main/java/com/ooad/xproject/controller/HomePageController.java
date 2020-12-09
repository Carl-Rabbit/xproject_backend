package com.ooad.xproject.controller;

import com.github.pagehelper.PageInfo;
import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.constant.RespStatus;
import com.ooad.xproject.entity.Project;
import com.ooad.xproject.entity.Role;
import com.ooad.xproject.entity.Student;
import com.ooad.xproject.service.HomeService;
import com.ooad.xproject.service.RoleService;
import com.ooad.xproject.service.StudentService;
import com.ooad.xproject.vo.Result;
import com.ooad.xproject.vo.SelectorStdVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

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
    public Result<?> getHomePageInfo() {
        logger.info("getHomePageInfo");
        return new Result<>(RespStatus.NOT_IMPLEMENTED, "Use account-info instead");
    }

    @ResponseBody
    @GetMapping("api/project-list")
    public Result<?> getProjectList() {
        logger.info("getProjectList");
        Subject subject = SecurityUtils.getSubject();
        String username = subject.getPrincipal().toString();

        Role role = roleService.getByUsername(username);
        List<Project> projList = homeService.getProjectList(role);
        return new Result<>(projList);
    }

    @ResponseBody
    @PostMapping("api/teacher/students")
    public Result<?> postSelectStudents(@RequestBody SelectorStdVO selectorStdVO) {
        logger.info("getProjectList");
        Subject subject = SecurityUtils.getSubject();
        String username = subject.getPrincipal().toString();

        SvResult<PageInfo<Student>> svResult = studentService.getStudentListBySelector(selectorStdVO);

        return new Result<>(svResult.getData());
    }
}
