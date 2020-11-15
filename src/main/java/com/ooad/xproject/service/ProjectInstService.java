package com.ooad.xproject.service;

import com.ooad.xproject.entity.*;

import java.util.List;

public interface ProjectInstService {
    ProjectInst getProjectInst();

    ProjectInst getProjectInst(Integer piid);

    List<ProjectInst> getProjectInstList(Integer pid);

    List<Student> getTeammembers(Integer tid);

    // select by record, return recordinst of the record
    List<RecordInst> getRecordInstList(Integer rid);

    // select by Project id and student id, return the student's RecordInsts under the project
    List<RecordInst> getRecordInstList(Integer pid, Integer sid);

    // select by submission, return submissionInst of the submission
    List<SubmissionInst> getSubmissionInstList(Integer suid);

    // select by Project id and student id, return the student's submissionInsts under the submission
    List<SubmissionInst> getSubmissionInstList(Integer pid, Integer sid);
}
