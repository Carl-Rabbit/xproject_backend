package com.ooad.xproject.service.impl;

import com.ooad.xproject.entity.*;
import com.ooad.xproject.mapper.*;
import com.ooad.xproject.service.ProjectService;

import java.util.ArrayList;

public class ProjectServiceImpl implements ProjectService {
    private final AnnouncementMapper announcementMapper;
    private final EventArrangeTaskMapper eventArrangeTaskMapper;
    private final SubmissionMapper submissionMapper;
    private final RecordMapper recordMapper;
    private final TeamMapper teamMapper;
    private final ProjectMapper projectMapper;
    private final ProjectInstMapper projectInstMapper;

    public ProjectServiceImpl(AnnouncementMapper announcementMapper, EventArrangeTaskMapper eventArrangeTaskMapper, SubmissionMapper submissionMapper, RecordMapper recordMapper, TeamMapper teamMapper, ProjectMapper projectMapper, ProjectInstMapper projectInstMapper) {
        this.announcementMapper = announcementMapper;
        this.eventArrangeTaskMapper = eventArrangeTaskMapper;
        this.submissionMapper = submissionMapper;
        this.recordMapper = recordMapper;
        this.teamMapper = teamMapper;
        this.projectMapper = projectMapper;
        this.projectInstMapper = projectInstMapper;
    }

    @Override
    public Project getProject(Integer pid) {
        return projectMapper.selectByPrimaryKey(pid);
    }

    @Override
    public ArrayList<ProjectInst> getProjectInstList(Integer pid) {
        return projectInstMapper.selectByProjId(pid);
    }

    @Override
    public ArrayList<Announcement> getAnnounceList(Integer pid) {
        return announcementMapper.selectByProjId(pid);
    }

    @Override
    public ArrayList<EventArrangeTask> getEventList(Integer pid) {
        return eventArrangeTaskMapper.selectByProjId(pid);
    }

    @Override
    public ArrayList<Submission> getSubmissionList(Integer pid) {
        return submissionMapper.selectByProjId(pid);
    }

    @Override
    public ArrayList<Record> getRecordList(Integer pid) {
        return recordMapper.selectByProjId(pid);
    }

    //todo: Team forming task
    @Override
    public TeamFormTask getTeamFormTask(Integer pid) {
        return null;
    }

    @Override
    public ArrayList<Team> getTeamList(Integer pid) {
        return teamMapper.selectByProjId(pid);
    }
}
