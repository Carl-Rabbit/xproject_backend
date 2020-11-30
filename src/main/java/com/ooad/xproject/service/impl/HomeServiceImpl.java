package com.ooad.xproject.service.impl;

import com.ooad.xproject.bo.SvRoleInfo;
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

    //todo: specify the student or teacher?
    @Override
    public ArrayList<Project> getProjectList(SvRoleInfo roleInfo) {
        ArrayList<Project> projects;
        switch (roleInfo.getRoleType()) {
            case RoleServiceImpl.ADMIN:
                projects = projectMapper.selectAll();
                break;
            case RoleServiceImpl.TEACHER:
                projects = projectMapper.selectByTchId(roleInfo.getTypeId());
                break;
            case RoleServiceImpl.STUDENT:
                projects = projectMapper.selectByStdId(roleInfo.getRoleId());
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
