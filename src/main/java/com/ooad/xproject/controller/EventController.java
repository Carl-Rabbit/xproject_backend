package com.ooad.xproject.controller;

import com.ooad.xproject.dto.EATaskDTO;
import com.ooad.xproject.dto.EventInstDTO;
import com.ooad.xproject.entity.*;
import com.ooad.xproject.service.*;
import com.ooad.xproject.utils.RoleUtils;
import com.ooad.xproject.vo.EATaskCreationVO;
import com.ooad.xproject.vo.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

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
        eaTaskCreationVO.copyToEATask(eaTask, role.getRoleId());

        boolean success = eaTaskService.createEATask(eaTask);

        if (eaTaskCreationVO.isAutoDistribution()) {
            // TODO
        }

        return createBoolResult(success, "Update successfully", "Update failed");
    }

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
