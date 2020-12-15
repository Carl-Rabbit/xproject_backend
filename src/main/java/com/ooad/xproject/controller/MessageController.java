package com.ooad.xproject.controller;

import com.ooad.xproject.dto.MessageDTO;
import com.ooad.xproject.entity.Role;
import com.ooad.xproject.service.MessageService;
import com.ooad.xproject.service.ProjInstService;
import com.ooad.xproject.service.RoleService;
import com.ooad.xproject.utils.RoleUtils;
import com.ooad.xproject.vo.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MessageController {

    private final RoleService roleService;
    private final ProjInstService projInstService;
    private final MessageService messageService;
    private final Logger logger = LogManager.getLogger(this.getClass().getName());

    public MessageController(RoleService roleService, ProjInstService projInstService, MessageService messageService) {
        this.roleService = roleService;
        this.projInstService = projInstService;
        this.messageService = messageService;
    }

    @ResponseBody
    @GetMapping("api/all/project/message")
    public Result<?> getProjMsgList(@RequestParam("projId") int projId) {
        String username = RoleUtils.getUsername();
        Role role = roleService.getByUsername(username);

        List<MessageDTO> msgList = messageService.getMessageDTOList(role.getRoleId(), projId, null);
        return new Result<>(msgList);
    }

    @ResponseBody
    @GetMapping("api/all/team/message")
    public Result<?> getTeamMsgList(@RequestParam("projInstId") int projInstId) {
        List<MessageDTO> msgList = messageService.getMessageDTOList(null, null, projInstId);
        return new Result<>(msgList);
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
