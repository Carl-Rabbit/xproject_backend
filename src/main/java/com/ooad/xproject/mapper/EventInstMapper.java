package com.ooad.xproject.mapper;

import com.ooad.xproject.entity.EventInst;

public interface EventInstMapper {
    int deleteByPrimaryKey(Integer eventId);

    int insert(EventInst record);

    int insertSelective(EventInst record);

    EventInst selectByPrimaryKey(Integer eventId);

    int updateByPrimaryKeySelective(EventInst record);

    int updateByPrimaryKey(EventInst record);
}