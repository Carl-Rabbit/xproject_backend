package com.ooad.xproject.controller;

import com.ooad.xproject.constant.RespStatus;
import com.ooad.xproject.constant.RoleType;
import com.ooad.xproject.entity.Role;
import com.ooad.xproject.entity.School;
import com.ooad.xproject.entity.Student;
import com.ooad.xproject.service.HomeService;
import com.ooad.xproject.service.RoleService;
import com.ooad.xproject.service.StudentService;
import com.ooad.xproject.utils.RoleUtils;
import com.ooad.xproject.vo.AccountInfoStdVO;
import com.ooad.xproject.vo.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    private final RoleService roleService;
    private final StudentService studentService;
    private final HomeService homeService;
    private final Logger logger = LogManager.getLogger(this.getClass().getName());

    public AccountController(RoleService testService, StudentService studentService, HomeService homeService) {
        this.roleService = testService;
        this.studentService = studentService;
        this.homeService = homeService;
    }

    @ResponseBody
    @GetMapping("api/account-info")
    public Result<?> getAccountInfo() {
        String username = RoleUtils.getUsername();
        Role role = roleService.getByUsername(username);

        if (RoleType.Student.match(role.getRoleType())) {
            Student student = studentService.getStudentByRoleId(role.getRoleId());
            School school = homeService.getSchool(student.getSchId());
            AccountInfoStdVO accountInfoStdVO = AccountInfoStdVO.createFrom(role, student, school);
            return new Result<>(accountInfoStdVO);
        } else {
            return new Result<>(RespStatus.NOT_IMPLEMENTED, "Not supported yet");
        }
    }
}
