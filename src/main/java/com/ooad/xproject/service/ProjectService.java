package com.ooad.xproject.service;

import com.ooad.xproject.entity.*;

import java.util.List;

public interface ProjectService {
    Project getProject();

    List<Project> getProjectList();

    List<Announcement> getAnnounceList();

    List<EventArrangeTask> getEventList();

    List<Submission> getSubmissionList();

    List<Record> getRecordList();

    TeamFormTask getTeamFormTask();

    List<Team> getTeamList();
}
