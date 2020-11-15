package com.ooad.xproject.service;

import com.ooad.xproject.entity.*;

import java.util.List;

public interface ProjectInstService {
    ProjectInst getProjectInst();

    List<ProjectInst> getProjectInstList();

    List<Student> getTeammembers();

    List<RecordInst> getRecordInstList();

    List<SubmissionInst> getSubmissionInstList();
}
