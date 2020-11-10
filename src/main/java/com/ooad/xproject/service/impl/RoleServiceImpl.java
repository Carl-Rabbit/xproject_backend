package com.ooad.xproject.service.impl;

import com.ooad.xproject.constant.RoleType;
import com.ooad.xproject.entity.Role;
import com.ooad.xproject.entity.Student;
import com.ooad.xproject.entity.Teacher;
import com.ooad.xproject.mapper.AdminMapper;
import com.ooad.xproject.mapper.RoleMapper;
import com.ooad.xproject.mapper.StudentMapper;
import com.ooad.xproject.mapper.TeacherMapper;
import com.ooad.xproject.service.RoleService;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    // Contain number and letter, at least 8 chars.
    // For username, not start with number
    public static final String USERNAME_REGEX = "^(?![0-9])(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,}$";
    public static final String PASSWORD_REGEX = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,}$";

    private final RoleMapper roleMapper;
    private final AdminMapper adminMapper;
    private final TeacherMapper teacherMapper;
    private final StudentMapper studentMapper;

    public RoleServiceImpl(RoleMapper roleMapper, AdminMapper adminMapper,
                           TeacherMapper teacherMapper, StudentMapper studentMapper) {
        this.roleMapper = roleMapper;
        this.adminMapper = adminMapper;
        this.teacherMapper = teacherMapper;
        this.studentMapper = studentMapper;
    }

    @Override
    public Role getByUsername(String username) {
        return roleMapper.selectByUsername(username);
    }

//    /**
//     * Check the role with the username and the pwd.
//     * Return Role obj if success.
//     */
//    @Override
//    public SvResult<Role> checkRolePwd(String username, String password) {
//        Role role = roleMapper.selectByUsername(username);
//        if (role == null) {
//            return new SvResult<>(-1, "No such username");
//        }
//        if (!password.equals(role.getPassword())) {
//            return new SvResult<>(-1, "Wrong password");
//        }
//        return new SvResult<>(0, "Authentication passed", role);
//    }

    @Override
    public void createUser(String type, String username, String password) {
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2;      // hash times
        String encodedPassword = new SimpleHash("md5", password, salt, times).toString();

        Role newRole = new Role();
        newRole.setUsername(username);
        newRole.setPassword(encodedPassword);
        newRole.setRoleType(type);
        newRole.setSalt(salt);
        int roleId = roleMapper.insertSelective(newRole);

        if (RoleType.Teacher.match(type)) {
            // create teacher
            Teacher tch = new Teacher();
            tch.setRoleId(roleId);
            teacherMapper.insertSelective(tch);
        } else {
            // create student
            Student std = new Student();
            std.setRoleId(roleId);
            studentMapper.insertSelective(std);
        }
    }

    @Override
    public boolean validUsername(String username) {
        return username.matches(USERNAME_REGEX);
    }

    @Override
    public boolean validPassword(String password) {
        return password.matches(PASSWORD_REGEX);
    }
}
