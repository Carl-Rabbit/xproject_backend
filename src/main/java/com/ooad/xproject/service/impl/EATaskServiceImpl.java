package com.ooad.xproject.service.impl;

import com.ooad.xproject.entity.EventArrangeTask;
import com.ooad.xproject.entity.EventInst;
import com.ooad.xproject.mapper.EventArrangeTaskMapper;
import com.ooad.xproject.mapper.EventInstMapper;
import com.ooad.xproject.service.EATaskService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EATaskServiceImpl implements EATaskService {

    private final EventArrangeTaskMapper eaTaskMapper;
    private final EventInstMapper eventInstMapper;

    public EATaskServiceImpl(EventArrangeTaskMapper eaTaskMapper, EventInstMapper eventInstMapper) {
        this.eaTaskMapper = eaTaskMapper;
        this.eventInstMapper = eventInstMapper;
    }

    @Override
    public List<EventArrangeTask> getEATaskList(int projId) {
        return eaTaskMapper.selectByProjId(projId);
    }

    @Override
    public boolean createEATask(EventArrangeTask eaTask) {
        return eaTaskMapper.insertSelective(eaTask) == 1;
    }

    @Override
    public boolean deleteEATask(int eaTaskId) {
        return eaTaskMapper.deleteByPrimaryKey(eaTaskId) == 1;
    }

    @Override
    public List<EventInst> getEventInstList(int eaTaskId) {
        return eventInstMapper.selectByEATaskId(eaTaskId);
    }

}
