package com.ooad.xproject.mapper;

import com.ooad.xproject.entity.RecordInst;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordInstMapper {
    int deleteByPrimaryKey(Integer rcdInstId);

    int insert(RecordInst record);

    int insertSelective(RecordInst record);

    RecordInst selectByPrimaryKey(Integer rcdInstId);

    int updateByPrimaryKeySelective(RecordInst record);

    int updateByPrimaryKey(RecordInst record);

    List<RecordInst> selectByRcdId(Integer rid);
}