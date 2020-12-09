package com.ooad.xproject.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ooad.xproject.bo.StudentImportBO;
import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.constant.RoleType;
import com.ooad.xproject.dto.StudentDTO;
import com.ooad.xproject.entity.Role;
import com.ooad.xproject.entity.Student;
import com.ooad.xproject.entity.Teacher;
import com.ooad.xproject.mapper.RoleMapper;
import com.ooad.xproject.mapper.StudentMapper;
import com.ooad.xproject.service.StudentService;
import com.ooad.xproject.vo.AcInfoStdUpdateVO;
import com.ooad.xproject.vo.SelectorStdVO;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    private final RoleMapper roleMapper;
    private final StudentMapper studentMapper;

    public StudentServiceImpl(RoleMapper roleMapper, StudentMapper studentMapper) {
        this.roleMapper = roleMapper;
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
    public SvResult<PageInfo<StudentDTO>> getStudentListBySelector(Role role, Teacher teacher, SelectorStdVO selectorStdVO) {
        PageHelper.startPage(selectorStdVO.getPage(), selectorStdVO.getPageSize(), selectorStdVO.getOrderStr());

        List<StudentDTO> stdDTOList;
        try {
            stdDTOList = studentMapper.selectDTOBySelector(teacher.getSchId(), selectorStdVO.getStdClass());
        } catch (Exception e) {
            e.printStackTrace();
            return new SvResult<>(-1, e.getMessage());
        }
        PageInfo<StudentDTO> pageInfo = new PageInfo<>(stdDTOList);

        return new SvResult<>("Success", pageInfo);
    }

    @Override
    public SvResult<Boolean> creatRoleAndStudent(StudentImportBO studentImportBO) {
        if (roleMapper.selectByUsername(studentImportBO.getUsername()) != null) {
            return new SvResult<>("User already exist", false);
        }

        try{
            String username = studentImportBO.getUsername();
            String password = studentImportBO.getPassword();
            String type = "Student";
            String salt = new SecureRandomNumberGenerator().nextBytes().toString();
            int times = 2;      // hash times
            String encodedPassword = new SimpleHash("md5", password, salt, times).toString();

//            create and insert role
            Role newRole = new Role();
            newRole.setUsername(username);
            newRole.setPassword(encodedPassword);
            newRole.setRoleType(type);
            newRole.setSalt(salt);
            int affectedRowCnt = roleMapper.insertSelective(newRole);
            if (affectedRowCnt == 0) {
                throw new Exception("Error when insert Role");
            }
            int roleId = newRole.getRoleId();
            System.out.println(roleId);

//            create and insert student
            Student std = new Student();
            std.setStdName(studentImportBO.getStdName());
            std.setStdNo(studentImportBO.getStdNo());
            std.setStdClass(studentImportBO.getStdClass());
            std.setEmail(studentImportBO.getEmail());
            std.setRoleId(roleId);
            affectedRowCnt = studentMapper.insertSelective(std);
            if (affectedRowCnt == 0) {
                throw new Exception("Error when insert Student");
            }
            return new SvResult<>("Create a new Student account", true);
        }catch (Exception e){
            // roll back
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return new SvResult<>(e.toString(), false);
        }
    }
}
