package com.ooad.xproject.service.impl;

import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.dto.RecordInstDTO;
import com.ooad.xproject.dto.StudentDTO;
import com.ooad.xproject.entity.ProjectInst;
import com.ooad.xproject.entity.RecordInst;
import com.ooad.xproject.entity.SubmissionInst;
import com.ooad.xproject.mapper.ProjectInstMapper;
import com.ooad.xproject.mapper.RecordInstMapper;
import com.ooad.xproject.mapper.SubmissionInstMapper;
import com.ooad.xproject.service.ProjInstService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<RecordInstDTO> getRecordInstList(Integer projId, Integer stdRoleId) {
        return recordInstMapper.selectByProjAndStdRoleId(projId, stdRoleId);
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

    @Transactional
    @Override
    public SvResult<Boolean> deleteProjInst(int projInstId) {
        int affectedRowCnt = projectInstMapper.deleteByPrimaryKey(projInstId);
        if (affectedRowCnt == 1) {
            return new SvResult<>(0, true);
        } else {
            return new SvResult<>(0, false);
        }
    }
}
