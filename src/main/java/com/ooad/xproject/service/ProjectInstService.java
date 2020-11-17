package com.ooad.xproject.service;

import com.ooad.xproject.entity.*;

import java.util.ArrayList;

public interface ProjectInstService {

    ProjectInst getProjectInst(Integer piid);

    // select by record, return recordinst of the record
    ArrayList<RecordInst> getRecordInstList(Integer rid);

    // select by Project id and student id, return the student's RecordInsts under the project
    ArrayList<RecordInst> getRecordInstList(Integer pid, Integer sid);

    // select by submission, return submissionInst of the submission
    ArrayList<SubmissionInst> getSubmissionInstList(Integer sbmid);

    // select by Project id and student id, return the student's submissionInsts under the submission
    ArrayList<SubmissionInst> getSubmissionInstList(Integer pid, Integer sid);
}
