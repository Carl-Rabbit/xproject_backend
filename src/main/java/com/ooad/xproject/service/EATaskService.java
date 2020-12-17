package com.ooad.xproject.service;

import com.ooad.xproject.entity.EventArrangeTask;
import com.ooad.xproject.entity.EventInst;

import java.util.List;

public interface EATaskService {
    List<EventArrangeTask> getEATaskList(int projId);

    boolean createEATask(EventArrangeTask eaTask);

    boolean deleteEATask(int eaTaskId);

    List<EventInst> getEventInstList(int eaTaskId);
}
