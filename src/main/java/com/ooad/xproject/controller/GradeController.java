package com.ooad.xproject.controller;

import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.dto.GradeDTO;
import com.ooad.xproject.dto.RecordInstDTO;
import com.ooad.xproject.entity.Record;
import com.ooad.xproject.entity.Role;
import com.ooad.xproject.entity.Teacher;
import com.ooad.xproject.service.*;
import com.ooad.xproject.utils.RoleUtils;
import com.ooad.xproject.vo.RecordCreationVO;
import com.ooad.xproject.vo.RecordDeletionVO;
import com.ooad.xproject.vo.RecordVO;
import com.ooad.xproject.vo.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GradeController {

    private final RoleService roleService;
    private final ProjectService projectService;
    private final ProjInstService projInstService;
    private final RecordService recordService;
    private final TeacherService teacherService;
    private final Logger logger = LogManager.getLogger(this.getClass().getName());

    public GradeController(RoleService roleService, ProjectService projectService, ProjInstService projInstService, RecordService recordService, TeacherService teacherService) {
        this.roleService = roleService;
        this.projectService = projectService;
        this.projInstService = projInstService;
        this.recordService = recordService;
        this.teacherService = teacherService;
    }


    @ResponseBody
    @GetMapping("api/student/grade/list")
    public Result<?> getGradeList(@RequestParam(value="projId") int projId) {
        Subject subject = SecurityUtils.getSubject();
        String username = subject.getPrincipal().toString();
        Role role = roleService.getByUsername(username);

        List<RecordInstDTO> recordInstDTOList = projInstService.getRecordInstList(projId, role.getRoleId());

//        logger.info("getTeamInfoList -> " + Arrays.toString(recordInstDTOList.toArray()));
        return new Result<>(recordInstDTOList);
    }

    @ResponseBody
    @PostMapping("api/teacher/grade/new/record")
    public Result<?> postNewRecord(@RequestBody RecordCreationVO recordCreationVO) {
        Role role = roleService.getByUsername(RoleUtils.getUsername());
        boolean success = recordService.createNewRecord(role.getRoleId(), recordCreationVO);
        return Result.createBoolResult(success, "Create record successfully", "Create record failed");
    }

    @ResponseBody
    @GetMapping("api/teacher/record/inst")
    public Result<?> getRecordInst(@RequestParam("projInstId") int projInstId,
                                   @RequestParam("rcdId") int rcdId) {
        Subject subject = SecurityUtils.getSubject();
        String username = subject.getPrincipal().toString();
        Role role = roleService.getByUsername(username);

        // TODO check access

        List<GradeDTO> recordInstDTOList = projInstService.getTeamRecordInstList(projInstId, rcdId);

//        logger.info("getRecordInst -> " + Arrays.toString(recordInstDTOList.toArray()));
        return new Result<>(recordInstDTOList);
    }

    @ResponseBody
    @GetMapping("api/all/record")
    public Result<?> getAllRecord(@RequestParam(value="projId") int projId) {
        Subject subject = SecurityUtils.getSubject();
        String username = subject.getPrincipal().toString();
        Role role = roleService.getByUsername(username);

        List<Record> recordList = recordService.getRecordList(projId);
        List<RecordVO> recordVOList = new ArrayList<>(recordList.size());
        for (Record rcd : recordList) {
            Teacher teacher = teacherService.getTeacherByRoleId(rcd.getCreatorId());
            recordVOList.add(RecordVO.builder()
                    .record(rcd)
                    .creator(teacher)
                    .build());
        }

//        logger.info("getTeamInfoList -> " + Arrays.toString(recordInstDTOList.toArray()));
        return new Result<>(recordVOList);
    }

    @ResponseBody
    @PostMapping("api/teacher/grade/delete/record")
    public Result<?> postDeleteRecord(@RequestBody RecordDeletionVO recordDeletionVO) {
        Subject subject = SecurityUtils.getSubject();
        String username = subject.getPrincipal().toString();
        Role role = roleService.getByUsername(username);

        // TODO check access

        SvResult<Integer> svResult = recordService.deleteRecords(recordDeletionVO);
        return new Result<>(svResult.getMsg(), svResult.getData());
    }
}
