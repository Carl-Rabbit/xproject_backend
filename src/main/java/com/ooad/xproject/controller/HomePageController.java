package com.ooad.xproject.controller;

import com.ooad.xproject.constant.RespStatus;
import com.ooad.xproject.constant.RoleType;
import com.ooad.xproject.dto.StudentDTO;
import com.ooad.xproject.entity.Project;
import com.ooad.xproject.entity.Role;
import com.ooad.xproject.entity.Teacher;
import com.ooad.xproject.service.HomeService;
import com.ooad.xproject.service.RoleService;
import com.ooad.xproject.service.StudentService;
import com.ooad.xproject.service.TeacherService;
import com.ooad.xproject.utils.RoleUtils;
import com.ooad.xproject.vo.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class HomePageController {

    private final RoleService roleService;
    private final TeacherService teacherService;
    private final StudentService studentService;
    private final HomeService homeService;
    private final Logger logger = LogManager.getLogger(this.getClass().getName());

    public HomePageController(RoleService roleService, TeacherService teacherService, StudentService studentService, HomeService homeService) {
        this.roleService = roleService;
        this.teacherService = teacherService;
        this.studentService = studentService;
        this.homeService = homeService;
    }

    @ResponseBody
    @GetMapping("api/all/home-page/info")
    public Result<?> getHomePageInfo() {
        logger.info("getHomePageInfo");
        return new Result<>(RespStatus.NOT_IMPLEMENTED, "Use account-info instead");
    }

    @ResponseBody
    @GetMapping("api/all/project/list")
    public Result<?> getProjectList() {
        logger.info("getProjectList");
        Subject subject = SecurityUtils.getSubject();
        String username = subject.getPrincipal().toString();

        Role role = roleService.getByUsername(username);
        List<Project> projList = homeService.getProjectList(role.getRoleId());
        return new Result<>(projList);
    }

    @ResponseBody
    @PostMapping("api/all/project/join")
    public Result<?> postJoinProject(@RequestParam("projId") int projId,
                                    @RequestParam(value = "groupMark", required = false, defaultValue = "Default") String groupMark) {
        logger.info("joinProject");
        Subject subject = SecurityUtils.getSubject();
        String username = subject.getPrincipal().toString();

        Role role = roleService.getByUsername(username);

        boolean success = homeService.joinProject(role.getRoleId(), projId, groupMark);

        if (success) {
            return new Result<>(true);
        } else {
            return new Result<>(RespStatus.FAIL,"Join project failed", false);
        }
    }

    @ResponseBody
    @PostMapping("api/all/proj/quit")
    public Result<?> postProjQuit(@RequestParam("projId") int projId) {
        logger.info("postProjQuit");
        Role role = roleService.getByUsername(RoleUtils.getUsername());

        boolean success = homeService.quitProject(role.getRoleId(), projId);

        if (success) {
            return new Result<>("Quit project successfully", true);
        } else {
            return new Result<>(RespStatus.FAIL, "Quit project failed", false);
        }
    }

    @ResponseBody
    @GetMapping("api/all/project/by-sch")
    public Result<?> getProjectListBySch() {
        logger.info("getProjectList");
        Subject subject = SecurityUtils.getSubject();
        String username = subject.getPrincipal().toString();

        Role role = roleService.getByUsername(username);
        List<Project> projList = homeService.getProjectListBySch(role);
        return new Result<>(projList);
    }

    @ResponseBody
    @GetMapping("api/teacher/students")
    public Result<?> postSelectStudents() {
        logger.info("postSelectStudents");
        Subject subject = SecurityUtils.getSubject();
        String username = subject.getPrincipal().toString();
        Role role = roleService.getByUsername(username);

        if (RoleType.Student.match(role.getRoleType())) {
            return new Result<>(RespStatus.FAIL, "Student can't visit this api");
        }

        Teacher teacher = teacherService.getTeacherByRoleId(role.getRoleId());

        List<StudentDTO> stdDTOList = studentService.getAllStudentList(teacher);

        return new Result<>(stdDTOList);
    }
}
