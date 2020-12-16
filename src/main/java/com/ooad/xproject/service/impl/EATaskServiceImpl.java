package com.ooad.xproject.service.impl;

import com.ooad.xproject.entity.EventArrangeTask;
import com.ooad.xproject.mapper.EventArrangeTaskMapper;
import com.ooad.xproject.service.EATaskService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EATaskServiceImpl implements EATaskService {

    private final EventArrangeTaskMapper eaTaskMapper;

    public EATaskServiceImpl(EventArrangeTaskMapper eaTaskMapper) {
        this.eaTaskMapper = eaTaskMapper;
    }

    @Override
    public List<EventArrangeTask> getEaTaskList(int projId) {
        return eaTaskMapper.selectByProjId(projId);
    }
}
