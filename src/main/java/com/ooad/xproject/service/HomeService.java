package com.ooad.xproject.service;

import com.ooad.xproject.bo.SvRoleInfo;
import com.ooad.xproject.entity.Announcement;
import com.ooad.xproject.entity.Project;

import java.util.List;

public interface HomeService {

    List<Project> getProjectList(SvRoleInfo roleInfo);

    List<Announcement> getAnnounceList();
}
