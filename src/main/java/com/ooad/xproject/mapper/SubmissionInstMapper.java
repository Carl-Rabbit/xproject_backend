package com.ooad.xproject.mapper;

import com.ooad.xproject.entity.SubmissionInst;

import java.util.ArrayList;
import java.util.List;

public interface SubmissionInstMapper {
    int deleteByPrimaryKey(Integer sbmInstId);

    int insert(SubmissionInst record);

    int insertSelective(SubmissionInst record);

    SubmissionInst selectByPrimaryKey(Integer sbmInstId);

    int updateByPrimaryKeySelective(SubmissionInst record);

    int updateByPrimaryKey(SubmissionInst record);

    ArrayList<SubmissionInst> selectBySbmId(Integer sbmId);
}