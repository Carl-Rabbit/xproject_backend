package com.ooad.xproject.service;

import com.ooad.xproject.entity.*;

import java.util.ArrayList;

public interface ProjectService {
    Project getProject(Integer pid);

    // select by project id, return the list of ProjectInsts
    ArrayList<ProjectInst> getProjectInstList(Integer pid);

    // select by project id, return the list of Announcements
    ArrayList<Announcement> getAnnounceList(Integer pid);

    // select by project id, return the list of Events
    ArrayList<EventArrangeTask> getEventList(Integer pid);

    // select by project id, return the list of Submissions
    ArrayList<Submission> getSubmissionList(Integer pid);

    // select by project id, return the list of Records
    ArrayList<Record> getRecordList(Integer pid);

    // select by project id, return the Team form task of project
    TeamFormTask getTeamFormTask(Integer pid);

    // select by project id, return the list of Teams
    ArrayList<Team> getTeamList(Integer pid);
}
