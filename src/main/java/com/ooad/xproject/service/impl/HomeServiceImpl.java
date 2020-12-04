package com.ooad.xproject.service.impl;

import com.ooad.xproject.constant.RoleType;
import com.ooad.xproject.entity.Announcement;
import com.ooad.xproject.entity.Project;
import com.ooad.xproject.entity.Role;
import com.ooad.xproject.mapper.ProjectMapper;
import com.ooad.xproject.service.HomeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HomeServiceImpl implements HomeService {
    private final ProjectMapper projectMapper;

    public HomeServiceImpl(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    @Override
    public List<Project> getProjectList(Role role) {
        List<Project> projects;
        RoleType roleType = RoleType.getRoleType(role.getRoleType());
        System.out.println(roleType);
        switch (roleType) {
            case Admin:
                projects = projectMapper.selectAll();
                break;
            case Teacher:
                projects = projectMapper.selectByTchId(role.getRoleId());
                break;
            case Student:
                projects = projectMapper.selectByStdId(role.getRoleId());
                System.out.println(role.getRoleId());
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
