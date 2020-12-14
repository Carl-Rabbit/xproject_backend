package com.ooad.xproject.controller;

import com.ooad.xproject.constant.RespStatus;
import com.ooad.xproject.constant.RoleType;
import com.ooad.xproject.entity.Role;
import com.ooad.xproject.entity.School;
import com.ooad.xproject.entity.Student;
import com.ooad.xproject.entity.Teacher;
import com.ooad.xproject.service.HomeService;
import com.ooad.xproject.service.RoleService;
import com.ooad.xproject.service.StudentService;
import com.ooad.xproject.service.TeacherService;
import com.ooad.xproject.utils.RoleUtils;
import com.ooad.xproject.vo.AcInfoStdUpdateVO;
import com.ooad.xproject.vo.AccountInfoStdVO;
import com.ooad.xproject.vo.AccountInfoTchVO;
import com.ooad.xproject.vo.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {

    private final RoleService roleService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final HomeService homeService;
    private final Logger logger = LogManager.getLogger(this.getClass().getName());

    public AccountController(RoleService testService, StudentService studentService, TeacherService teacherService, HomeService homeService) {
        this.roleService = testService;
        this.studentService = studentService;
        this.teacherService = teacherService;
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
            Teacher teacher = teacherService.getTeacherByRoleId(role.getRoleId());
            School school = homeService.getSchool(teacher.getSchId());
            AccountInfoTchVO accountInfoTchVO = AccountInfoTchVO.createFrom(role, teacher, school);
            return new Result<>(accountInfoTchVO);
        }
    }

    @ResponseBody
    @GetMapping("api/team/stu-info")
    public Result<?> getStuInfo(@RequestParam("roleId") int roleId) {
        String username = RoleUtils.getUsername();
        Role role = roleService.getByUsername(username);

        int schId;
        if (RoleType.Student.match(role.getRoleType())) {
            Student student = studentService.getStudentByRoleId(role.getRoleId());
            schId = student.getSchId();
        } else {
            Teacher teacher = teacherService.getTeacherByRoleId(role.getRoleId());
            schId = teacher.getSchId();
        }

        Role targetRole = roleService.getByRoleId(roleId);

        if (!RoleType.Student.match(targetRole.getRoleType())) {
            return new Result<>(RespStatus.FAIL, "Getting non-student info is forbidden");
        }
        Student target = studentService.getStudentByRoleId(roleId);

        if (target == null) {
            // not the same school
            return new Result<>(RespStatus.FAIL, "No such student");
        }

        if (schId != target.getSchId()) {
            // not the same school
            return new Result<>(RespStatus.FAIL, "School not matched");
        }

        return new Result<>(target);
    }

    @ResponseBody
    @PostMapping("api/self-intro")
    public Result<?> updateAccountInfo(@RequestBody AcInfoStdUpdateVO acInfoStdUpdateVO) {
        String username = RoleUtils.getUsername();
        Role role = roleService.getByUsername(username);

        if (RoleType.Student.match(role.getRoleType())) {
            boolean success = studentService.updateAcInfo(role, acInfoStdUpdateVO);
            if (success) {
                return new Result<>(RespStatus.SUCCESS);
            } else {
                return new Result<>(RespStatus.FAIL);
            }
        } else {
            return new Result<>(RespStatus.NOT_IMPLEMENTED, "Not supported yet");
        }
    }
}
