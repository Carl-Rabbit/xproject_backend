package com.ooad.xproject.service.impl;

import com.ooad.xproject.entity.ProjectInst;
import com.ooad.xproject.entity.RecordInst;
import com.ooad.xproject.entity.Student;
import com.ooad.xproject.entity.SubmissionInst;
import com.ooad.xproject.service.ProjectInstService;

import java.util.List;

public class ProjectInstServiceImpl implements ProjectInstService {
    @Override
    public ProjectInst getProjectInst() {
        return null;
    }

    @Override
    public ProjectInst getProjectInst(Integer piid) {
        return null;
    }

    @Override
    public List<ProjectInst> getProjectInstList(Integer pid) {
        return null;
    }

    @Override
    public List<Student> getTeammembers(Integer tid) {
        return null;
    }

    @Override
    public List<RecordInst> getRecordInstList(Integer rid) {
        return null;
    }

    @Override
    public List<RecordInst> getRecordInstList(Integer pid, Integer sid) {
        return null;
    }


    @Override
    public List<SubmissionInst> getSubmissionInstList(Integer suid) {
        return null;
    }

    @Override
    public List<SubmissionInst> getSubmissionInstList(Integer pid, Integer sid) {
        return null;
    }
}
