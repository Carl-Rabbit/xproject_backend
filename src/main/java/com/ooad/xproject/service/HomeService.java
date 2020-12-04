package com.ooad.xproject.service;

import com.ooad.xproject.entity.Announcement;
import com.ooad.xproject.entity.Project;
import com.ooad.xproject.entity.Role;

import java.util.List;

public interface HomeService {

    List<Project> getProjectList(Role role);

    List<Announcement> getAnnounceList();
}
