package com.ooad.xproject.service.impl;

import com.ooad.xproject.bo.SvRoleInfo;
import com.ooad.xproject.constant.RoleType;
import com.ooad.xproject.entity.Announcement;
import com.ooad.xproject.entity.Project;
import com.ooad.xproject.mapper.ProjectMapper;
import com.ooad.xproject.service.HomeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class HomeServiceImpl implements HomeService {
    private final ProjectMapper projectMapper;

    public HomeServiceImpl(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    @Override
    public ArrayList<Project> getProjectList(SvRoleInfo roleInfo) {
        ArrayList<Project> projects;
        RoleType roleType = RoleType.getRoleType(roleInfo.getRoleType());
        switch (roleType) {
            case Admin:
                projects = projectMapper.selectAll();
                break;
            case Teacher:
                projects = projectMapper.selectByTchId(roleInfo.getTypeId());
                break;
            case Student:
                projects = projectMapper.selectByStdId(roleInfo.getTypeId());
                break;
            default:
                projects = new ArrayList<>();
                break;
        }
        return projects;
    }


    //todo: get from all projects?
    @Override
    public ArrayList<Announcement> getAnnounceList() {
        return null;
    }

}
