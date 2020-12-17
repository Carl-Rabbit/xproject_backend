package com.ooad.xproject.service.impl;

import com.ooad.xproject.entity.Submission;
import com.ooad.xproject.entity.SubmissionInst;
import com.ooad.xproject.mapper.SubmissionInstMapper;
import com.ooad.xproject.mapper.SubmissionMapper;
import com.ooad.xproject.service.SubmissionInstService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubmissionInstServiceImpl implements SubmissionInstService {
    private final SubmissionInstMapper submissionInstMapper;

    private final SubmissionMapper submissionMapper;

    public SubmissionInstServiceImpl(SubmissionInstMapper submissionInstMapper, SubmissionMapper submissionMapper) {
        this.submissionInstMapper = submissionInstMapper;
        this.submissionMapper = submissionMapper;
    }

    @Override
    public int upsertSubmissionInst(int sbmId, int projInstId, int submitterId) {
        SubmissionInst sbmInst = submissionInstMapper.selectBySbmIdAndProjInstId(sbmId, projInstId);
        if (sbmInst == null) {
            Submission sbm = submissionMapper.selectByPrimaryKey(sbmId);
            sbmInst = new SubmissionInst();
            sbmInst.setSbmId(sbmId);
            sbmInst.setProjInstId(projInstId);
            sbmInst.setSubmitterId(submitterId);
            sbmInst.setSbmLeft(sbm.getMaxSbm());
            return submissionInstMapper.insert(sbmInst);
        } else {
            if (sbmInst.getSbmLeft() <= 0) {
                return 0;
            } else {
                sbmInst.setSbmLeft(sbmInst.getSbmLeft() - 1);
                return submissionInstMapper.updateSubmissionInst(sbmInst);
            }
        }
    }

    @Override
    public List<SubmissionInst> getSubmissionInstList(int sbmId, int projId) {
        return submissionInstMapper.selectBySbmIdAndProjId(sbmId, projId);
    }

}
