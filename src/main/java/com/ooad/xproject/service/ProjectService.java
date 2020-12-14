package com.ooad.xproject.service;

import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.bo.forming.FormingBO;
import com.ooad.xproject.bo.forming.FormingResultBO;
import com.ooad.xproject.dto.StudentProjDTO;
import com.ooad.xproject.entity.*;
import com.ooad.xproject.vo.ProjectUpdateVO;

import java.util.List;

public interface ProjectService {
    Project getProject(Integer pid);

    // select by project id, return the list of ProjectInsts
    List<ProjectInst> getProjectInstList(Integer pid);

    // select by project id, return the list of Announcements
    List<Announcement> getAnnounceList(Integer pid);

    // select by project id, return the list of Events
    List<EventArrangeTask> getEventList(Integer pid);

    // select by project id, return the list of Submissions
    List<Submission> getSubmissionList(Integer pid);

    // select by project id, return the list of Records
    List<Record> getRecordList(Integer pid);

    // select by project id, return the Team form task of project
    TeamFormTask getTeamFormTask(Integer pid);

    boolean updateProject(ProjectUpdateVO projectUpdateVO);

    List<StudentProjDTO> getStdProjList(int projId);

    SvResult<FormingResultBO> autoForming(FormingBO formingBO);
}
