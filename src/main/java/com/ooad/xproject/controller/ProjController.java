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
import com.ooad.xproject.entity.Teacher;
import com.ooad.xproject.service.*;
import com.ooad.xproject.utils.RoleUtils;
import com.ooad.xproject.vo.AutoFormingVO;
import com.ooad.xproject.vo.ProjAddStdVO;
import com.ooad.xproject.vo.ProjectVO;
import com.ooad.xproject.vo.Result;
import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class ProjController {

    private final RoleService roleService;
    private final HomeService homeService;
    private final ProjectService projectService;
    private final SubmissionInstService submissionInstService;
    private final ProjInstService projInstService;
    private final Logger logger = LogManager.getLogger(this.getClass().getName());
    private final TeacherService teacherService;

    public ProjController(RoleService roleService, HomeService homeService, ProjectService projectService, SubmissionInstService submissionInstService, ProjInstService projInstService, TeacherService teacherService) {
        this.roleService = roleService;
        this.homeService = homeService;
        this.projectService = projectService;
        this.submissionInstService = submissionInstService;
        this.projInstService = projInstService;
        this.teacherService = teacherService;
    }

    @ResponseBody
    @GetMapping("api/all/project/topic")
    public Result<?> getProjTopics(@RequestParam(value="projId") int projId) {
        Project project = projectService.getProject(projId);
        List<TopicBO> topicList = JSON.parseArray(project.getTopics(), TopicBO.class);
        return new Result<>(topicList);
    }

    @ResponseBody
    @GetMapping("api/all/project/overview")
    public Result<Project> getProject(@RequestParam("projId") int projId) {
        logger.info("getProject");

        Subject subject = SecurityUtils.getSubject();
        String username = subject.getPrincipal().toString();

        Role role = roleService.getByUsername(username);

        List<Project> projList = homeService.getProjectList(role.getRoleId());

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
    public Result<Boolean> updateProject(@RequestBody ProjectVO projectVO) {
        logger.info("updateProject");

        Subject subject = SecurityUtils.getSubject();
        String username = subject.getPrincipal().toString();

        Role role = roleService.getByUsername(username);

        List<Project> projList = homeService.getProjectList(role.getRoleId());

        boolean hasThisProj = false;
        for (Project proj: projList) {
            if (proj.getProjId().equals(projectVO.getProjId())) {
                hasThisProj = true;
                break;
            }
        }
        if (!hasThisProj) {
            return new Result<>(RespStatus.UNAUTHORIZED);
        }
        Project project = new Project();
        projectVO.copyToProjUpdate(project);
        boolean success = projectService.updateProject(project);

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
    @PostMapping("api/teacher/team/auto-forming")
    public Result<?> postAutoForming(@RequestBody AutoFormingVO autoFormingVO) {
        FormingBO formContext = new FormingBO();
        boolean success = formContext.setStrategy(autoFormingVO.getStrategy());
        if (!success) {
            return new Result<>(RespStatus.FAIL, "No such strategy");
        }
        formContext.setProjInstList(autoFormingVO.getProjInstList());
        formContext.setStdRoleIdList(autoFormingVO.getStdRoleIdList());

        SvResult<FormingResultBO> formingRes = projectService.autoForming(formContext);
        FormingResultBO res = formingRes.getData();

        // handle auto confirm
        String autoConfirmMsg = null;
        if (autoFormingVO.isAutoConfirm()) {
            List<Pair<Integer, Integer>> matchList = res.getMatchList();
            Set<Integer> projInstIdSet = matchList.stream()
                    .mapToInt(Pair::getFirst)
                    .collect(HashSet::new, (set, item) -> ((Set<Integer>) set).add(item), null);
            int[] projInstIdList = projInstIdSet.stream().mapToInt(Integer::valueOf).toArray();
            autoConfirmMsg = projInstService.confirmBatchTch(projInstIdList, false);
        }

//        logger.info(String.format("getProjStdList -> %s", stdProjDTOList));
        res.setMatchList(null);
        String retMsg = formingRes.getMsg() + ((autoConfirmMsg == null) ? "" : "\r\n" + autoConfirmMsg);
        return new Result<>(retMsg, res);
    }

    @ResponseBody
    @GetMapping("api/teacher/project/submission-ins")
    public Result<?> getSbmInsList(@RequestParam(value="sbmId") int sbmId, @RequestParam(value="projId") int projId){
        return new Result<>(submissionInstService.getSubmissionInstList(sbmId, projId));
    };

    @ResponseBody
    @GetMapping("api/student/team/ungrouped")
    public Result<?> getUngroupedStudents(@RequestParam(value="projId") int projId) {
        List<StudentProjDTO> stdProjDTOList = projectService.getStdProjList(projId);
        stdProjDTOList.removeIf(studentProjDTO -> studentProjDTO.getProjInstId() != null);
        logger.info(String.format("getUngroupedStudents -> %s", stdProjDTOList));
        return new Result<>(stdProjDTOList);
    }

    @ResponseBody
    @GetMapping("api/all/team/stu-proj")
    public Result<?> getStuProj(@RequestParam(value="roleId") int roleId) {
        List<Project> projList = homeService.getProjectList(roleId);
        logger.info(String.format("getUngroupedStudents -> %s", projList));
        return new Result<>(projList);
    }

    @ResponseBody
    @PostMapping("api/teacher/project/add")
    public Result<?> postAddProject(@RequestBody ProjectVO projectVO) {
        String username = RoleUtils.getUsername();
        Role role = roleService.getByUsername(username);

        Project proj = new Project();
        projectVO.copyToProjCreate(proj);
        proj.setCreatorId(role.getRoleId());
        Teacher teacher = teacherService.getTeacherByRoleId(role.getRoleId());
        proj.setSchId(teacher.getSchId());
        boolean success = projectService.createProject(proj);
        return Result.createBoolResult(success, "Create project successfully", "Create project failed");
    }

    @ResponseBody
    @PostMapping("api/teacher/project/add/std")
    public Result<?> postAddStdIntoProj(@RequestBody ProjAddStdVO projAddStdVO) {
//        String username = RoleUtils.getUsername();
//        Role role = roleService.getByUsername(username);

        int successCnt = 0;
        for (int stdRoleId: projAddStdVO.getStdRoleIdList()) {
            boolean success = homeService.joinProject(stdRoleId, projAddStdVO.getProjId(), projAddStdVO.getGroupMark());
            successCnt += success ? 1 : 0;
        }

        return new Result<>(successCnt);
    }
}
