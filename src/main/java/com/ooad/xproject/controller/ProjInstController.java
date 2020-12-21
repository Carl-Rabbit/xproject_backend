package com.ooad.xproject.controller;

import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.constant.RespStatus;
import com.ooad.xproject.constant.RoleType;
import com.ooad.xproject.dto.StudentDTO;
import com.ooad.xproject.entity.ProjectInst;
import com.ooad.xproject.entity.Role;
import com.ooad.xproject.service.MessageService;
import com.ooad.xproject.service.ProjInstService;
import com.ooad.xproject.service.RoleService;
import com.ooad.xproject.utils.RoleUtils;
import com.ooad.xproject.vo.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProjInstController {

    private final RoleService roleService;
    private final ProjInstService projInstService;
    private final MessageService messageService;
    private final Logger logger = LogManager.getLogger(this.getClass().getName());

    public ProjInstController(RoleService roleService, ProjInstService projInstService, MessageService messageService) {
        this.roleService = roleService;
        this.projInstService = projInstService;
        this.messageService = messageService;
    }

    @ResponseBody
    @GetMapping("api/all/team/list")
    public Result<?> getTeamInfoList(@RequestParam(value="projId") int projId) {
        List<ProjectInst> projInstList = projInstService.getProInstList(projId);
        List<SimpleTeamVO> simpleTeamVOList = new ArrayList<>();
        for (ProjectInst projectInst: projInstList) {
            List<StudentDTO> studentList = projInstService.getStudentDTOByProjInstId(projectInst.getProjInstId());
            SimpleTeamVO simpleTeamVO = SimpleTeamVO.createFrom(projectInst, studentList.size());
            simpleTeamVOList.add(simpleTeamVO);
        }
//        logger.info("getTeamInfoList -> " + Arrays.toString(simpleTeamVOList.toArray()));
        return new Result<>(simpleTeamVOList);
    }

    @ResponseBody
    @GetMapping("api/all/team/info/detail")
    public Result<?> getTeamDetail(@RequestParam(value="teamId") int projInstId) {
        ProjectInst projectInst = projInstService.getProjectInst(projInstId);
        List<StudentDTO> studentList = projInstService.getStudentDTOByProjInstId(projInstId);
        TeamVO teamVO = TeamVO.createFrom(projectInst, studentList);
        logger.info(String.format("getTeamDetail -> %s", teamVO));
        return new Result<>(teamVO);
    }

    @ResponseBody
    @PostMapping("api/student/team/change/info")
    public Result<?> postEditedTeamInfo(@RequestBody ProjInstUpdateVO piuVO) {
        String username = RoleUtils.getUsername();
        Role role = roleService.getByUsername(username);

        ProjectInst projectInst = projInstService.getPIByProjIdAndStdRoleId(piuVO.getProjId(), role.getRoleId());
        if (projectInst == null) {
            return new Result<>(RespStatus.FAIL, "No team");
        }
        piuVO.copyToProjInst(projectInst);
        boolean success = projInstService.updateProjInst(projectInst);
        return Result.createBoolResult(success, "Update successfully", "Update failed");
    }

    @ResponseBody
    @GetMapping("api/student/team/detail")
    public Result<?> getMyTeamDetail(@RequestParam(value="projId") int projId) {
        String username = RoleUtils.getUsername();
        Role role = roleService.getByUsername(username);

        if (!RoleType.Student.match(role.getRoleType())) {
            return new Result<>(RespStatus.FAIL, "Only student can have a own team");
        }

        ProjectInst projInst = projInstService.getPIByProjIdAndStdRoleId(projId, role.getRoleId());

        if (projInst == null) {
            return new Result<>(RespStatus.FAIL, "No team");
        }

        List<StudentDTO> studentList = projInstService.getStudentDTOByProjInstId(projInst.getProjInstId());
        TeamVO teamVO = TeamVO.createFrom(projInst, studentList);

        logger.info(String.format("getTeamDetail -> %s", projInst));
        return new Result<>(teamVO);
    }

    @ResponseBody
    @PostMapping("api/student/team/apply")
    public Result<?> postTeamApply(@RequestBody ApplyTeamParamVO applyTeamParamVO) {
        String username = RoleUtils.getUsername();
        Role role = roleService.getByUsername(username);

        SvResult<Boolean> svResult = projInstService.applyTeam(role.getRoleId(), applyTeamParamVO);
        return new Result<>(svResult.getMsg(), svResult.getData());
    }

    @ResponseBody
    @PostMapping("api/student/team/reply/application")
    public Result<?> postReplyApplication(@RequestBody ApplyReplyParamVO applyReplyParamVO) {
        String username = RoleUtils.getUsername();
        Role role = roleService.getByUsername(username);

        // TODO check access here

        SvResult<Boolean> svResult = projInstService.applyTeamReply(role.getRoleId(), applyReplyParamVO);
        return new Result<>(svResult.getMsg(), svResult.getData());
    }

    @ResponseBody
    @PostMapping("api/all/team/creation")
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
    @PostMapping("api/teacher/team/deletion")
    public Result<Integer> postTeamDeletion(@RequestBody ProjInstIdListVO projInstIdListVO) {
        List<Integer> successList = new ArrayList<>();
        for (int projInstId : projInstIdListVO.getProjInstIdList()) {
            SvResult<Boolean> svResult = projInstService.deleteProjInst(projInstId);
            if (svResult.getData()) {
                // true
                successList.add(projInstId);
            } else {
                // false
                logger.info(String.format("postTeamDeletion: delete fail. " +
                        "projInstId=%d, msg=%s", projInstId, svResult.getMsg()));
            }
        }
        int successCnt = successList.size();
        RespStatus status = (successCnt == 0) ? RespStatus.FAIL : RespStatus.SUCCESS;
        String msg = (successCnt == 0) ? "Delete all teams fail" : "Delete teams done";
        return new Result<>(status, msg, successCnt);
    }

    @ResponseBody
    @PostMapping("api/all/team/confirm")
    public Result<?> postTeamConfirm(@RequestBody TeamConfirmParamVO teamConfirmParamVO) {
        Role role = roleService.getByUsername(RoleUtils.getUsername());

        if (RoleType.Student.match(role.getRoleType())) {
            int projInstId = teamConfirmParamVO.getProjInstIdList()[0];
            SvResult<Boolean> svResult = projInstService.confirmProjInst(projInstId, false);
            return new Result<>(svResult.getMsg(), svResult.getData() ? 1 : 0);
        } else {
            String message = projInstService.confirmBatchTch(teamConfirmParamVO.getProjInstIdList(),
                    teamConfirmParamVO.isForce());
            return new Result<>(message);
        }
    }


    @ResponseBody
    @PostMapping("api/student/team/quit")
    public Result<?> postQuitTeam(@RequestParam(value="projId") int projId) {
        String username = RoleUtils.getUsername();
        Role role = roleService.getByUsername(username);
        ProjectInst projectInst = projInstService.getPIByProjIdAndStdRoleId(projId, role.getRoleId());

        if (projectInst == null) {
            return new Result<>(RespStatus.FAIL, "No team");
        }

        boolean success = projInstService.quitTeam(role.getRoleId(), projectInst.getProjInstId());
        if (success) {
            return new Result<>("Quit successfully");
        } else {
            return new Result<>(RespStatus.FAIL, "Quit failed");
        }
    }

    @ResponseBody
    @GetMapping("api/all/teammates/by/id")
    public Result<?> getTeammatesByRoleId(@RequestParam(value="projId") int projId,
                                          @RequestParam("roleId") int roleId) {

        ProjectInst projectInst = projInstService.getPIByProjIdAndStdRoleId(projId, roleId);
        if (projectInst == null) {
            return new Result<>(RespStatus.FAIL, "No team");
        }

        List<StudentDTO> stdDTOList = projInstService.getStudentDTOByProjInstId(projectInst.getProjInstId());
        return new Result<>(stdDTOList);
    }
}
