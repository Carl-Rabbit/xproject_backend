package com.ooad.xproject.service.impl;

import com.alibaba.fastjson.JSON;
import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.entity.Role;
import com.ooad.xproject.entity.Student;
import com.ooad.xproject.mapper.StudentMapper;
import com.ooad.xproject.service.StudentService;
import com.ooad.xproject.vo.AcInfoStdUpdateVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentMapper studentMapper;

    public StudentServiceImpl(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    @Override
    public Student getStudent(Integer sid) {
        return studentMapper.selectByPrimaryKey(sid);
    }

    @Override
    public List<Student> getStudentList(Integer group) {
        return studentMapper.selectBySgpId(group);
    }

    @Override
    public List<Student> getTeammatesByTeamId(Integer tid) {
        return studentMapper.selectByTeamId(tid);
    }

    @Override
    public List<Student> getTeammatesByStdId(Integer sid) {
        int tid = studentMapper.selectTeamIdByStdId(sid);
        return studentMapper.selectByTeamId(tid);
    }

    @Override
    public Student getStudentByRoleId(Integer roleId) {
        return studentMapper.selectByRoleId(roleId);
    }

    @Override
    public boolean updateAcInfo(Role role, AcInfoStdUpdateVO acInfoStdUpdateVO) {
        Student student = studentMapper.selectByRoleId(role.getRoleId());
        String newFlagsJson = JSON.toJSONString(acInfoStdUpdateVO.getFlags());
        String newSkillsJson = JSON.toJSONString(acInfoStdUpdateVO.getSkills());
        String newBio = acInfoStdUpdateVO.getBio();

        Student newStd = new Student();     // not use the old one to save recourse
        newStd.setStdId(student.getStdId());
        newStd.setFlags(newFlagsJson);
        newStd.setSkills(newSkillsJson);
        newStd.setBio(newBio);

        int affectedRowCnt = studentMapper.updateByPrimaryKeySelective(newStd);

        return affectedRowCnt == 1;
    }
}
