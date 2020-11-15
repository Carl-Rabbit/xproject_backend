package com.ooad.xproject.service.impl;

import com.ooad.xproject.entity.*;
import com.ooad.xproject.mapper.*;
import com.ooad.xproject.service.ProjectService;

import java.util.List;

public class ProjectServiceImpl implements ProjectService {
    private final AnnouncementMapper announcementMapper;
    private final EventArrangeTaskMapper eventArrangeTaskMapper;
    private final SubmissionMapper submissionMapper;
    private final RecordMapper recordMapper;
    private final TeamMapper teamMapper;
    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(AnnouncementMapper announcementMapper, EventArrangeTaskMapper eventArrangeTaskMapper, SubmissionMapper submissionMapper, RecordMapper recordMapper, TeamMapper teamMapper, ProjectMapper projectMapper) {
        this.announcementMapper = announcementMapper;
        this.eventArrangeTaskMapper = eventArrangeTaskMapper;
        this.submissionMapper = submissionMapper;
        this.recordMapper = recordMapper;
        this.teamMapper = teamMapper;
        this.projectMapper = projectMapper;
    }

    @Override
    public Project getProject(Integer pid) {
        return projectMapper.selectByPrimaryKey(pid);
    }

    //todo: specify the student or teacher?
    @Override
    public List<Project> getProjectByRoleId(Integer rid) {
        projectMapper.selectByRoleId(rid);
        return null;
    }

    //todo: get from all projects?
    @Override
    public List<Announcement> getAnnounceList() {
        return null;
    }

    @Override
    public List<Announcement> getAnnounceList(Integer pid) {
        return announcementMapper.selectByProjId(pid);
    }

    @Override
    public List<EventArrangeTask> getEventList(Integer pid) {
        return eventArrangeTaskMapper.selectByProjId(pid);
    }

    @Override
    public List<Submission> getSubmissionList(Integer pid) {
        return submissionMapper.selectByProjId(pid);
    }

    @Override
    public List<Record> getRecordList(Integer pid) {
        return recordMapper.selectByProjId(pid);
    }

    //todo: Team forming task
    @Override
    public TeamFormTask getTeamFormTask(Integer pid) {
        return null;
    }

    @Override
    public List<Team> getTeamList(Integer pid) {
        return teamMapper.selectByProjId(pid);
    }
}
