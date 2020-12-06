package com.ooad.xproject.service.impl;

import com.ooad.xproject.constant.RoleType;
import com.ooad.xproject.entity.Announcement;
import com.ooad.xproject.entity.Project;
import com.ooad.xproject.entity.Role;
import com.ooad.xproject.entity.School;
import com.ooad.xproject.mapper.ProjectMapper;
import com.ooad.xproject.mapper.SchoolMapper;
import com.ooad.xproject.service.HomeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HomeServiceImpl implements HomeService {
    private final ProjectMapper projectMapper;
    private final SchoolMapper schoolMapper;

    public HomeServiceImpl(ProjectMapper projectMapper, SchoolMapper schoolMapper) {
        this.projectMapper = projectMapper;
        this.schoolMapper = schoolMapper;
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
                // temp version: user relation table
                projects = projectMapper.selectByStdId(role.getRoleId());
                break;
            case Student:
                projects = projectMapper.selectByStdId(role.getRoleId());
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

    @Override
    public School getSchool(int schId) {
        return schoolMapper.selectByPrimaryKey(schId);
    }
}
