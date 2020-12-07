package com.ooad.xproject.controller;

import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.constant.RespStatus;
import com.ooad.xproject.constant.RoleType;
import com.ooad.xproject.dto.StudentDTO;
import com.ooad.xproject.entity.ProjectInst;
import com.ooad.xproject.entity.Role;
import com.ooad.xproject.service.ProjInstService;
import com.ooad.xproject.service.RoleService;
import com.ooad.xproject.utils.RoleUtils;
import com.ooad.xproject.vo.ProjInstCreationVO;
import com.ooad.xproject.vo.Result;
import com.ooad.xproject.vo.SimpleTeamVO;
import com.ooad.xproject.vo.TeamVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ProjInstController {

    private final RoleService roleService;
    private final ProjInstService projInstService;
    private final Logger logger = LogManager.getLogger(this.getClass().getName());

    public ProjInstController(RoleService roleService, ProjInstService projInstService) {
        this.roleService = roleService;
        this.projInstService = projInstService;
    }

    @ResponseBody
    @GetMapping("api/team-info-list")
    public Result<?> getTeamInfoList(@RequestParam(value="projId") int projId) {
        List<ProjectInst> projInstList = projInstService.getProInstList(projId);
        List<SimpleTeamVO> simpleTeamVOList = projInstList.stream()
                .map(SimpleTeamVO::createFrom).collect(Collectors.toList());
        logger.info("getTeamInfoList -> " + Arrays.toString(simpleTeamVOList.toArray()));
        return new Result<>(simpleTeamVOList);
    }

    @ResponseBody
    @GetMapping("api/team-detail")
    public Result<?> getTeamDetail(@RequestParam(value="teamId") int projInstId) {
        ProjectInst projectInst = projInstService.getProjectInst(projInstId);
        List<StudentDTO> studentList = projInstService.getStudentDTOByProjInstId(projInstId);
        TeamVO teamVO = TeamVO.createFrom(projectInst, studentList);
        logger.info(String.format("getTeamDetail -> %s", teamVO));
        return new Result<>(teamVO);

//        Subject subject = SecurityUtils.getSubject();
//        String username = subject.getPrincipal().toString();
//        Role role = roleService.getByUsername(username);
//        SvRoleInfo roleInfo = roleService.getRoleInfo(role);
//        List<Project> projList = homeService.getProjectList(roleInfo);
//        logger.info(String.format("getProjectList -> username: %s, projList: %s",
//                username, );
    }

    @ResponseBody
    @PostMapping("api/team-apply")
    public Result<?> postTeamApply(@RequestParam(value="teamId") int projInstId) {
        return new Result<>(RespStatus.SUCCESS, "Just a fake interface");
    }

    @ResponseBody
    @PostMapping("api/team-creation")
    public Result<Integer> postTeamCreation(@RequestBody ProjInstCreationVO projInstCreationVO) {
        String username = RoleUtils.getUsername();
        Role role = roleService.getByUsername(username);

        if (RoleType.Teacher.match(role.getRoleType())) {

            int successCnt = 0;
            boolean useIncrNum = projInstCreationVO.isUseIncrNum();
            int generateNum = projInstCreationVO.getGenerateNum();
            String baseTeamName = projInstCreationVO.getTeamName();

            for (int i = 0; i < generateNum; i++) {
                if (useIncrNum) {
                    projInstCreationVO.setTeamName(baseTeamName + ' ' + i);
                }
                SvResult<Boolean> svResult = projInstService.createProjInst(projInstCreationVO);
                if (svResult.getData()) {
                    successCnt ++;
                } else {
                    // fail to create
                    logger.info("postTeamCreation: create failed when i=" + i + ". " + svResult.getMsg());
                }
            }
            logger.info("postTeamCreation: creation process done for teacher. successCnt=" + successCnt);
            RespStatus status = (successCnt == 0) ? RespStatus.FAIL : RespStatus.SUCCESS;
            String msg = (successCnt == 0) ? "Create teams fail" : "Create teams done";
            return new Result<>(status, msg, successCnt);

        } else if (RoleType.Student.match(role.getRoleType())) {

            SvResult<Boolean> svResult = projInstService.createProjInstAndLink(role.getRoleId(), projInstCreationVO);
            if (svResult.getData()) {
                logger.info("postTeamCreation: create success for student. " + svResult.getMsg());
                return new Result<>(RespStatus.SUCCESS, svResult.getMsg(), 1);
            } else {
                logger.info("postTeamCreation: create fail for student. " + svResult.getMsg());
                return new Result<>(RespStatus.FAIL, svResult.getMsg(), 0);
            }

        } else {
            return new Result<>(RespStatus.NOT_IMPLEMENTED, "Type Error");
        }
    }

    @ResponseBody
    @PostMapping("api/team-deletion")
    public Result<Integer> postTeamDeletion(@RequestParam(value="teamIdList") int[] projInstIdList) {
        List<Integer> successList = new ArrayList<>();
        for (int projInstId : projInstIdList) {
            SvResult<Boolean> svResult = projInstService.deleteProjInst(projInstId);
            if (svResult.getData()) {
                // true
                successList.add(projInstId);
            } else {
                // false
                // do nothing
            }
        }
        int successCnt = successList.size();
        RespStatus status = (successCnt == 0) ? RespStatus.FAIL : RespStatus.SUCCESS;
        return new Result<>(status, "Delete teams done", successCnt);
    }

    @ResponseBody
    @PostMapping("api/team-confirm")
    public Result<Integer> postTeamConfirm(@RequestParam(value="teamIdList") int[] projInstIdList) {
        List<Integer> successList = new ArrayList<>();
        for (int projInstId : projInstIdList) {
            SvResult<Boolean> svResult = projInstService.confirmProjInst(projInstId);
            if (svResult.getData()) {
                // true
                successList.add(projInstId);
            } else {
                // false
                // do nothing
            }
        }
        int successCnt = successList.size();
        return new Result<>(successCnt);
    }

    @ResponseBody
    @GetMapping("api/student/team/detail")
    public Result<TeamVO> getTeamDetailByProjId(@RequestParam(value="projId") int projId) {
        String username = RoleUtils.getUsername();
        Role role = roleService.getByUsername(username);

        ProjectInst projectInst = projInstService.getPIByProjIdAndStdRoleId(projId, role.getRoleId());
        List<StudentDTO> studentList = projInstService.getStudentDTOByProjInstId(projectInst.getProjInstId());
        TeamVO teamVO = TeamVO.createFrom(projectInst, studentList);

        logger.info(String.format("getTeamDetailByProjId -> %s", teamVO));
        return new Result<>(teamVO);
    }
}
