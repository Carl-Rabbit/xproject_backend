package com.ooad.xproject.service.impl;

import com.ooad.xproject.entity.ProjectInst;
import com.ooad.xproject.entity.RecordInst;
import com.ooad.xproject.entity.SubmissionInst;
import com.ooad.xproject.mapper.ProjectInstMapper;
import com.ooad.xproject.mapper.RecordInstMapper;
import com.ooad.xproject.mapper.SubmissionInstMapper;
import com.ooad.xproject.service.ProjectInstService;

import java.util.ArrayList;
import java.util.List;

public class ProjectInstServiceImpl implements ProjectInstService {
    private final ProjectInstMapper projectInstMapper;
    private final RecordInstMapper recordInstMapper;
    private final SubmissionInstMapper submissionInstMapper;

    public ProjectInstServiceImpl(ProjectInstMapper projectInstMapper, RecordInstMapper recordInstMapper, SubmissionInstMapper submissionInstMapper) {
        this.projectInstMapper = projectInstMapper;
        this.recordInstMapper = recordInstMapper;
        this.submissionInstMapper = submissionInstMapper;
    }

    @Override
    public ProjectInst getProjectInst(Integer piid) {
        return projectInstMapper.selectByPrimaryKey(piid);
    }


    @Override
    public ArrayList<RecordInst> getRecordInstList(Integer rid) {
        return recordInstMapper.selectByRcdId(rid);
    }

    // todo: select Record by project and student
    @Override
    public ArrayList<RecordInst> getRecordInstList(Integer pid, Integer sid) {
        return null;
    }


    @Override
    public ArrayList<SubmissionInst> getSubmissionInstList(Integer sbmId) {
        return submissionInstMapper.selectBySbmId(sbmId);
    }

    // todo: select Submission by project and student
    @Override
    public ArrayList<SubmissionInst> getSubmissionInstList(Integer pid, Integer sid) {
        return null;
    }
}
