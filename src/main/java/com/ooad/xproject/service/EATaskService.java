package com.ooad.xproject.service;

import com.ooad.xproject.entity.EventArrangeTask;

import java.util.List;

public interface EATaskService {
    List<EventArrangeTask> getEaTaskList(int projId);
}
