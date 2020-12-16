package com.ooad.xproject.controller;

import com.ooad.xproject.entity.EventArrangeTask;
import com.ooad.xproject.entity.EventInst;
import com.ooad.xproject.service.*;
import com.ooad.xproject.vo.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EventController {

    private final RoleService roleService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final HomeService homeService;
    private final EATaskService eaTaskService;

    private final Logger logger = LogManager.getLogger(this.getClass().getName());

    public EventController(RoleService roleService, StudentService studentService, TeacherService teacherService, HomeService homeService, EATaskService eaTaskService) {
        this.roleService = roleService;
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.homeService = homeService;
        this.eaTaskService = eaTaskService;
    }


    @ResponseBody
    @GetMapping("api/all/event")
    public Result<?> getEATaskList(@RequestParam("projId") int projId) {
        List<EventArrangeTask> eaTaskList = eaTaskService.getEATaskList(projId);
        return new Result<>(eaTaskList);
    }

    @ResponseBody
    @GetMapping("api/all/event/inst")
    public Result<?> getEventTaskList(@RequestParam("eaTaskId") int eaTaskId) {
        List<EventInst> eventInstList = eaTaskService.getEventInstList(eaTaskId);
        return new Result<>(eventInstList);
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
