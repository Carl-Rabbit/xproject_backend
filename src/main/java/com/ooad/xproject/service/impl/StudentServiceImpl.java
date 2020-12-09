package com.ooad.xproject.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.entity.Role;
import com.ooad.xproject.entity.Student;
import com.ooad.xproject.entity.StudentExample;
import com.ooad.xproject.mapper.StudentMapper;
import com.ooad.xproject.service.StudentService;
import com.ooad.xproject.vo.AcInfoStdUpdateVO;
import com.ooad.xproject.vo.SelectorStdVO;
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
        Student newStd = new Student();     // not use the old one to save recourse

        newStd.setStdId(student.getStdId());

        if (acInfoStdUpdateVO.getFlags() != null) {
            String newFlagsJson = JSON.toJSONString(acInfoStdUpdateVO.getFlags());
            newStd.setFlags(newFlagsJson);
        }

        if (acInfoStdUpdateVO.getSkills() != null) {
            String newSkillsJson = JSON.toJSONString(acInfoStdUpdateVO.getSkills());
            newStd.setSkills(newSkillsJson);
        }

        if (acInfoStdUpdateVO.getBio() != null) {
            String newBio = acInfoStdUpdateVO.getBio();
            newStd.setBio(newBio);
        }

        int affectedRowCnt = studentMapper.updateByPrimaryKeySelective(newStd);

        return affectedRowCnt == 1;
    }

    @Override
    public SvResult<PageInfo<Student>> getStudentListBySelector(SelectorStdVO selectorStdVO) {
        PageHelper.startPage(selectorStdVO.getPage(), selectorStdVO.getPageSize(), selectorStdVO.getOrderStr());

        StudentExample stdExample = new StudentExample();

        if (selectorStdVO.getStdClass() != null) {
            stdExample.createCriteria().andStdClassEqualTo(selectorStdVO.getStdClass());
        }

        PageInfo<Student> pageInfo = new PageInfo<>(studentMapper.selectByExample(stdExample));

        return new SvResult<>("Success", pageInfo);
    }
}
