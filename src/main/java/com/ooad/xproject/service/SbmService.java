package com.ooad.xproject.service;

import com.ooad.xproject.entity.Submission;

import java.util.List;

public interface SbmService {

    List<Submission> getSbmListByProjId(Integer projId);

}
