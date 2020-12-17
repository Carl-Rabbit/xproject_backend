package com.ooad.xproject.service.impl;

import com.ooad.xproject.bo.SvResult;
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

    @Override
    public SvResult<Boolean> applyEventInst(int eventInstId, Integer projInstId) {
        int takeCnt = eventInstMapper.countApplyNum(projInstId);

        if (takeCnt >= 1) {
            return new SvResult<>("Can only apply one event inst", false);
        }

        EventInst eventInst = eventInstMapper.selectByPrimaryKey(eventInstId);
        if (eventInst.getProjInstId() != null) {
            return new SvResult<>("This time slot has been taken", false);
        }
        eventInst.setProjInstId(projInstId);
        boolean success = eventInstMapper.updateByPrimaryKeySelective(eventInst) == 1;

        return new SvResult<>("Apply finished", success);
    }

    @Override
    public SvResult<Boolean> clearEventInstStd(int eventInstId, Integer projInstId) {
        int takeCnt = eventInstMapper.countApplyNum(projInstId);

        if (takeCnt == 0) {
            return new SvResult<>("Can only clear selected event item", false);
        }

        EventInst eventInst = eventInstMapper.selectByPrimaryKey(eventInstId);
        if (eventInst.getProjInstId() == null) {
            return new SvResult<>("This time slot has not been taken yet", false);
        }
        if (!eventInst.getProjInstId().equals(projInstId)) {
            return new SvResult<>("Can only clear selected event item of your own team", false);
        }
        eventInst.setProjInstId(null);
        boolean success = eventInstMapper.updateByPrimaryKeySelective(eventInst) == 1;

        return new SvResult<>("Clear finished", success);
    }

}
