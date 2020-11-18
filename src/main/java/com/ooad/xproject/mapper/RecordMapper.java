package com.ooad.xproject.mapper;

import com.ooad.xproject.entity.Record;

import java.util.ArrayList;

public interface RecordMapper {
    int deleteByPrimaryKey(Integer rcdId);

    int insert(Record record);

    int insertSelective(Record record);

    Record selectByPrimaryKey(Integer rcdId);

    int updateByPrimaryKeySelective(Record record);

    int updateByPrimaryKey(Record record);

    ArrayList<Record> selectByProjId(Integer pid);
}