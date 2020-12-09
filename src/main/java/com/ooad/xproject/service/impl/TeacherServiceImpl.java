package com.ooad.xproject.service.impl;

import com.ooad.xproject.entity.Role;
import com.ooad.xproject.entity.Teacher;
import com.ooad.xproject.mapper.TeacherMapper;
import com.ooad.xproject.service.TeacherService;
import com.ooad.xproject.vo.AcInfoTchUpdateVO;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

@Service
public class TeacherServiceImpl implements TeacherService {
    private final TeacherMapper teacherMapper;

    public TeacherServiceImpl(TeacherMapper teacherMapper) {
        this.teacherMapper = teacherMapper;
    }

    @Override
    public Teacher getTeacherByTid(Integer tid) {
        return teacherMapper.selectByPrimaryKey(tid);
    }

    @Override
    public Teacher getTeacherByRoleId(Integer roleId) {
        return teacherMapper.selectByRoleId(roleId);
    }

    @Override
    public boolean updateAcInfo(Role role, AcInfoTchUpdateVO acInfoTchUpdateVO) {
        Teacher teacher = teacherMapper.selectByRoleId(role.getRoleId());
        Teacher newTch = new Teacher();     // not use the old one to save recourse

        newTch.setTchId(teacher.getTchId());

        throw new NotImplementedException("Updating account info for teacher is not implemented");

//        if (acInfoTchUpdateVO.getBio() != null) {
//            String newBio = acInfoTchUpdateVO.getBio();
//            newTch.setBio(newBio);
//        }
//
//        int affectedRowCnt = studentMapper.updateByPrimaryKeySelective(newTch);
//
//        return affectedRowCnt == 1;
    }
}
