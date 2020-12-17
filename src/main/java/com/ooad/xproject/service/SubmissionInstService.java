package com.ooad.xproject.service;

import com.ooad.xproject.entity.SubmissionInst;

import java.util.List;

public interface SubmissionInstService {
    int upsertSubmissionInst(int sbmId, int projId, int submitterId);

    List<SubmissionInst> getSubmissionInstList(int sbmId, int projId);

}
