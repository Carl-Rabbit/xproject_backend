package com.ooad.xproject.service.impl;

import com.ooad.xproject.entity.Submission;
import com.ooad.xproject.mapper.SubmissionInstMapper;
import com.ooad.xproject.mapper.SubmissionMapper;
import com.ooad.xproject.service.SbmService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SbmServiceImpl implements SbmService {

    private final SubmissionMapper submissionMapper;
    private final SubmissionInstMapper sbmInstMapper;

    public SbmServiceImpl(SubmissionMapper submissionMapper, SubmissionInstMapper sbmInstMapper) {
        this.submissionMapper = submissionMapper;
        this.sbmInstMapper = sbmInstMapper;
    }

    @Override
    public List<Submission> getSbmListByProjId(Integer projId) {
        return submissionMapper.selectByProjId(projId);
    }

    @Override
    public boolean createSubmission(Submission sbm) {
        sbm.setSbmId(null);
        int affectedRowCnt = submissionMapper.insertSelective(sbm);
        return affectedRowCnt == 1;
    }

    @Override
    public boolean deleteSubmission(int sbmId) {
        int affectedRowCnt = submissionMapper.deleteByPrimaryKey(sbmId);
        return affectedRowCnt == 1;
    }

    @Override
    public boolean modifySubmission(Submission sbm) {
        int affectedRowCnt = submissionMapper.updateByPrimaryKeySelective(sbm);
        return affectedRowCnt == 1;
    }
}
