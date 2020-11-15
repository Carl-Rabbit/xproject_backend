package com.ooad.xproject.service;

import com.ooad.xproject.entity.*;

import java.util.List;

public interface ProjectService {
    Project getProject(Integer pid);

    List<Project> getProjectList();

    List<Announcement> getAnnounceList();

    List<Announcement> getAnnounceList(Integer pid);

    List<EventArrangeTask> getEventList(Integer pid);

    List<Submission> getSubmissionList(Integer pid);

    List<Record> getRecordList(Integer pid);

    TeamFormTask getTeamFormTask(Integer pid);

    List<Team> getTeamList(Integer pid);
}
