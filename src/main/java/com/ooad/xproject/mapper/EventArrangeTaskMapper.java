package com.ooad.xproject.mapper;

import com.ooad.xproject.entity.EventArrangeTask;

import java.util.List;

public interface EventArrangeTaskMapper {
    int deleteByPrimaryKey(Integer eaTaskId);

    int insert(EventArrangeTask record);

    int insertSelective(EventArrangeTask record);

    EventArrangeTask selectByPrimaryKey(Integer eaTaskId);

    int updateByPrimaryKeySelective(EventArrangeTask record);

    int updateByPrimaryKey(EventArrangeTask record);

    List<EventArrangeTask> selectByProjId(Integer pid);
}