package com.ooad.xproject.service.impl;

import com.ooad.xproject.dto.StudentDTO;
import com.ooad.xproject.entity.ProjectInst;
import com.ooad.xproject.entity.RecordInst;
import com.ooad.xproject.entity.SubmissionInst;
import com.ooad.xproject.mapper.ProjectInstMapper;
import com.ooad.xproject.mapper.RecordInstMapper;
import com.ooad.xproject.mapper.SubmissionInstMapper;
import com.ooad.xproject.service.ProjInstService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjInstServiceImpl implements ProjInstService {
    private final ProjectInstMapper projectInstMapper;
    private final RecordInstMapper recordInstMapper;
    private final SubmissionInstMapper submissionInstMapper;

    public ProjInstServiceImpl(ProjectInstMapper projectInstMapper, RecordInstMapper recordInstMapper, SubmissionInstMapper submissionInstMapper) {
        this.projectInstMapper = projectInstMapper;
        this.recordInstMapper = recordInstMapper;
        this.submissionInstMapper = submissionInstMapper;
    }

    @Override
    public ProjectInst getProjectInst(Integer piid) {
        return projectInstMapper.selectByPrimaryKey(piid);
    }

    @Override
    public List<ProjectInst> getProInstList(Integer projId) {
        return projectInstMapper.selectByProjId(projId);
    }

    @Override
    public List<StudentDTO> getStudentDTOByProjInstId(Integer projInstId) {
        return projectInstMapper.selectStudentByProjInstId(projInstId);
    }


    @Override
    public List<RecordInst> getRecordInstList(Integer rid) {
        return recordInstMapper.selectByRcdId(rid);
    }

    // todo: select Record by project and student
    @Override
    public List<RecordInst> getRecordInstList(Integer pid, Integer sid) {
        return null;
    }


    @Override
    public List<SubmissionInst> getSubmissionInstList(Integer sbmId) {
        return submissionInstMapper.selectBySbmId(sbmId);
    }

    // todo: select Submission by project and student
    @Override
    public List<SubmissionInst> getSubmissionInstList(Integer pid, Integer sid) {
        return null;
    }
}
