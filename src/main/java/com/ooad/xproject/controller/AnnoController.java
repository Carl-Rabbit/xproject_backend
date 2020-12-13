package com.ooad.xproject.controller;

import com.ooad.xproject.entity.Role;
import com.ooad.xproject.service.*;
import com.ooad.xproject.utils.RoleUtils;
import com.ooad.xproject.vo.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnnoController {

    private final RoleService roleService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final HomeService homeService;
    private final AnnoService annoService;
    private final Logger logger = LogManager.getLogger(this.getClass().getName());

    public AnnoController(RoleService testService, StudentService studentService, TeacherService teacherService, HomeService homeService, AnnoService annoService) {
        this.roleService = testService;
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.homeService = homeService;
        this.annoService = annoService;
    }

    // TODO
    @ResponseBody
    @GetMapping("api/all/announcements")
    public Result<?> getAnnoList(@RequestParam("projId") int projId) {
        String username = RoleUtils.getUsername();
        Role role = roleService.getByUsername(username);
//        List<Announcement> annoList =
        return new Result<>(null);
    }

    // TODO Update anno

    // TODO Create anno

    // TODO Delete anno
}
