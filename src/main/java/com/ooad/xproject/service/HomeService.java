package com.ooad.xproject.service;

import com.ooad.xproject.entity.Announcement;
import com.ooad.xproject.entity.Project;
import com.ooad.xproject.entity.Role;
import com.ooad.xproject.entity.School;

import java.util.List;

public interface HomeService {

    List<Project> getProjectList(Role role);

    List<Announcement> getAnnounceList();

    School getSchool(int roleId);

    List<Project> getProjectListBySch(Role role);

    boolean joinProject(int roleId, int projId);
}
