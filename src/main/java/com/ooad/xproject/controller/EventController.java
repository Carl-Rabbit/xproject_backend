package com.ooad.xproject.controller;

import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.constant.RespStatus;
import com.ooad.xproject.dto.EATaskDTO;
import com.ooad.xproject.dto.EventInstDTO;
import com.ooad.xproject.entity.*;
import com.ooad.xproject.service.*;
import com.ooad.xproject.utils.RoleUtils;
import com.ooad.xproject.vo.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.ooad.xproject.vo.Result.createBoolResult;

@RestController
public class EventController {

    private final RoleService roleService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final HomeService homeService;
    private final ProjInstService projInstService;
    private final EATaskService eaTaskService;

    private final Logger logger = LogManager.getLogger(this.getClass().getName());

    public EventController(RoleService roleService, StudentService studentService, TeacherService teacherService, HomeService homeService, ProjInstService projInstService, EATaskService eaTaskService) {
        this.roleService = roleService;
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.homeService = homeService;
        this.projInstService = projInstService;
        this.eaTaskService = eaTaskService;
    }


    @ResponseBody
    @GetMapping("api/all/event")
    public Result<?> getEATaskList(@RequestParam("projId") int projId) {
        List<EventArrangeTask> eaTaskList = eaTaskService.getEATaskList(projId);
        List<EATaskDTO> eaTaskDTOList = new ArrayList<>();
        for (EventArrangeTask eaTask: eaTaskList) {
            Teacher tch = teacherService.getTeacherByRoleId(eaTask.getCreatorId());
            eaTaskDTOList.add(EATaskDTO.builder()
                    .eaTask(eaTask)
                    .creator(tch)
                    .build());
        }
        return new Result<>(eaTaskDTOList);
    }

    @ResponseBody
    @PostMapping("api/teacher/event/create")
    public Result<?> postEventCreation(@RequestBody EATaskCreationVO eaTaskCreationVO) {
        String username = RoleUtils.getUsername();
        Role role = roleService.getByUsername(username);

        EventArrangeTask eaTask = new EventArrangeTask();
        try {
            eaTaskCreationVO.copyToEATask(eaTask, role.getRoleId());
        } catch (ParseException e) {
            logger.info("postEventCreation fail: date format incorrect");
            return new Result<>(RespStatus.FAIL, "Date format must be yyyy-MM-dd HH:mm:ss");
        }

        boolean success = eaTaskService.createEATask(eaTask);

        return createBoolResult(success, "Update successfully", "Update failed");
    }

    @ResponseBody
    @PostMapping("api/teacher/event/del")
    public Result<?> postEventDeletion(@RequestParam("eaTaskId") int eaTaskId) {
        boolean success = eaTaskService.deleteEATask(eaTaskId);
        return createBoolResult(success, "Delete successfully", "Delete failed");
    }

    /* EVENT INST */

    @ResponseBody
    @GetMapping("api/all/event/inst")
    public Result<?> getEventTaskList(@RequestParam("eaTaskId") int eaTaskId) {
        List<EventInst> eventInstList = eaTaskService.getEventInstList(eaTaskId);
        List<EventInstDTO> eventInstDTOList = new ArrayList<>();
        for (EventInst eventInst: eventInstList) {
            ProjectInst projInst = projInstService.getProjectInst(eventInst.getProjInstId());
            eventInstDTOList.add(EventInstDTO.builder()
                    .eventInst(eventInst)
                    .projInst(projInst)
                    .build());
        }
        return new Result<>(eventInstDTOList);
    }

    @ResponseBody
    @PostMapping("api/teacher/event/inst/create")
    public Result<?> postEventInstCreation(@RequestBody EventInstCreationVO eventInstCreationVO) {
//        String username = RoleUtils.getUsername();
//        Role role = roleService.getByUsername(username);

        SvResult<Integer> svResult = eaTaskService.createEventInsts(eventInstCreationVO);
        return new Result<>(svResult.getMsg(), svResult.getData());
    }

    @ResponseBody
    @PostMapping("api/teacher/event/inst/delete")
    public Result<?> postEventInstDeletion(@RequestBody EventInstListParamVO eilParamVO) {
//        String username = RoleUtils.getUsername();
//        Role role = roleService.getByUsername(username);

        int[] eventInstIdList = eilParamVO.getEventInstIdList();
        int successCnt = eaTaskService.deleteEventInsts(eventInstIdList);
        String message = "Delete " + successCnt + " event item successfully. Total "
                + eilParamVO.getEventInstIdList().length;
        return new Result<>(message, successCnt);
    }

    @ResponseBody
    @PostMapping("api/student/event/inst/apply")
    public Result<?> postEventInstApply(@RequestParam("projId") int projId,
                                        @RequestParam("eventInstId") int eventInstId) {
        String username = RoleUtils.getUsername();
        Role role = roleService.getByUsername(username);

        ProjectInst projInst = projInstService.getPIByProjIdAndStdRoleId(projId, role.getRoleId());
        if (projInst == null) {
            return new Result<>(RespStatus.FAIL, "No Team yet");
        }

        SvResult<Boolean> svResult = eaTaskService.applyEventInst(eventInstId, projInst.getProjInstId());

        return createBoolResult(svResult.getData(), "Apply successfully", svResult.getMsg());
    }

    @ResponseBody
    @PostMapping("api/student/event/inst/clear")
    public Result<?> postEventInstClearStd(@RequestParam("projId") int projId,
                                           @RequestParam("eventInstId") int eventInstId) {
        String username = RoleUtils.getUsername();
        Role role = roleService.getByUsername(username);

        ProjectInst projInst = projInstService.getPIByProjIdAndStdRoleId(projId, role.getRoleId());
        if (projInst == null) {
            return new Result<>(RespStatus.FAIL, "No Team yet");
        }

        SvResult<Boolean> svResult = eaTaskService.clearEventInstStd(eventInstId, projInst.getProjInstId());

        return createBoolResult(svResult.getData(), "Clear successfully", svResult.getMsg());
    }

    @ResponseBody
    @PostMapping("api/teacher/event/inst/clear")
    public Result<?> postEventInstClearTch(@RequestBody EventInstListParamVO eventInstListParamVO) {
        String username = RoleUtils.getUsername();
        Role role = roleService.getByUsername(username);

        // check teacher access here

        // not implemented yet

        int successCnt = eaTaskService.clearEventInstTch(eventInstListParamVO.getEventInstIdList());

        String message = "Clear " + successCnt + " event item successfully. Total "
                + eventInstListParamVO.getEventInstIdList().length;
        return new Result<>(message, successCnt);
    }

    @ResponseBody
    @PostMapping("api/teacher/event/inst/manage")
    public Result<?> postEventInstAuto(@RequestBody EventInstManageParamVO eventInstManageParamVO) {
//        logger.info(String.format("getProjStdList -> %s", stdProjDTOList));
        int successCnt = eaTaskService.manageEventInsts(eventInstManageParamVO);
        String message = String.format("Manage %d event items successfully. Total event item %d. Remain %d teams.",
                successCnt,
                eventInstManageParamVO.getEventInstIdList().length,
                eventInstManageParamVO.getProjInstIdList().length - successCnt);
        return new Result<>(message, successCnt);
    }


//    @ResponseBody
//    @PostMapping("api/teacher/project/ann/modify")
//    public Result<?> postModifyAnnouncement(@RequestBody Announcement announcement) {
//        boolean success = annService.updateAnn(announcement);
//        if (success) {
//            return new Result<>(true);
//        } else {
//            return new Result<>(RespStatus.FAIL, "Update failed", false);
//        }
//    }
//
//    @ResponseBody
//    @PostMapping("api/teacher/project/ann/add")
//    public Result<?> postAddAnnouncement(@RequestBody Announcement announcement) {
//        String username = RoleUtils.getUsername();
//        Role role = roleService.getByUsername(username);
//
//        announcement.setCreatorId(role.getRoleId());
//
//        boolean success = annService.addAnn(announcement);
//        if (success) {
//            return new Result<>(true);
//        } else {
//            return new Result<>(RespStatus.FAIL, "Add failed", false);
//        }
//    }
//
//    @ResponseBody
//    @GetMapping("api/teacher/project/ann/delete")
//    public Result<?> getDeleteAnnouncement(@RequestParam("annId") int annId) {
//        boolean success = annService.deleteAnn(annId);
//        if (success) {
//            return new Result<>(true);
//        } else {
//            return new Result<>(RespStatus.FAIL, "Delete failed", false);
//        }
//    }
}
