package com.ooad.xproject.service;

import com.ooad.xproject.bo.SvRoleInfo;
import com.ooad.xproject.entity.Announcement;
import com.ooad.xproject.entity.Project;

import java.util.ArrayList;

public interface HomeService {

    ArrayList<Project> getProjectList(SvRoleInfo roleInfo);

    ArrayList<Announcement> getAnnounceList();
}
