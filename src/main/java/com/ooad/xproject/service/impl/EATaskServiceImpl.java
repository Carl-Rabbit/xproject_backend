package com.ooad.xproject.service.impl;

import com.ooad.xproject.bo.EventIntervalAlgo;
import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.entity.EventArrangeTask;
import com.ooad.xproject.entity.EventInst;
import com.ooad.xproject.mapper.EventArrangeTaskMapper;
import com.ooad.xproject.mapper.EventInstMapper;
import com.ooad.xproject.service.EATaskService;
import com.ooad.xproject.utils.TimeUtils;
import com.ooad.xproject.vo.EventInstCreationVO;
import com.ooad.xproject.vo.EventInstManageParamVO;
import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
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

    @Override
    public int clearEventInstTch(int[] eventInstIdList) {
        int successCnt = 0;
        for (int eventInstId: eventInstIdList) {
            EventInst eventInst = eventInstMapper.selectByPrimaryKey(eventInstId);
            if (eventInst.getProjInstId() == null) {
                continue;
            }
            int affectedRowCnt = eventInstMapper.insertSelective(eventInst);
            successCnt += affectedRowCnt;
            if (affectedRowCnt != 0) {
                // TODO notice all members
            }
        }
        return successCnt;
    }

    @Override
    public SvResult<Integer> createEventInsts(EventInstCreationVO eicVO) {
        Date date;
        String week;
        try {
            date = eicVO.getDate();
            week = TimeUtils.getWeek(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new SvResult<>("Fail to parse date", 0);
        }

        List<Pair<Date, Date>> timeList;
        try {
            timeList = EventIntervalAlgo.generateTimeIntervals(eicVO);
        } catch (ParseException e) {
            e.printStackTrace();
            return new SvResult<>("Fail to generate time intervals", 0);
        }

        int successCnt = 0;
        for (Pair<Date, Date> pair: timeList) {
            EventInst ei = new EventInst();
            ei.setEaTaskId(eicVO.getEaTaskId());
            ei.setDate(date);
            ei.setStartTime(pair.getFirst());
            ei.setEndTime(pair.getSecond());
            ei.setWeek(week);
            successCnt += eventInstMapper.insertSelective(ei);
        }

        return new SvResult<>("", successCnt);
    }

    @Override
    public int deleteEventInsts(int[] eventInstIdList) {
        int successCnt = 0;
        for (int eventInstId : eventInstIdList) {
            successCnt += eventInstMapper.deleteByPrimaryKey(eventInstId);
        }
        return successCnt;
    }

    @Override
    public int manageEventInsts(EventInstManageParamVO eimParamVO) {
        int[] eventInstIdList = eimParamVO.getEventInstIdList();
        int[] projInstIdList = eimParamVO.getProjInstIdList();

        int successCnt = 0;
        for (int i = 0; i < eventInstIdList.length; i++) {
            int eventInstId = eventInstIdList[i];
            int projInstId = projInstIdList[i];
            SvResult<Boolean> svResult = applyEventInst(eventInstId, projInstId);
            if (svResult.getData()) {
                successCnt += 1;
            } else {
                System.out.println(svResult.getMsg());
            }
        }
        return successCnt;
    }

}
