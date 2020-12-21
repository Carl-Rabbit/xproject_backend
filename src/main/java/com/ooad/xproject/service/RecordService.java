package com.ooad.xproject.service;

import com.ooad.xproject.bo.RecordUnitBO;
import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.entity.Record;
import com.ooad.xproject.entity.RecordInst;
import com.ooad.xproject.vo.RecordCreationVO;
import com.ooad.xproject.vo.RecordDeletionVO;

import java.util.List;

public interface RecordService {

    RecordInst getRecordInstByUnit(RecordUnitBO recordUnitBO, Integer projId);

    boolean createNewRecord(Integer roleId, RecordCreationVO recordCreationVO);

    List<Record> getRecordList(int projId);

    SvResult<Integer> deleteRecords(RecordDeletionVO recordDeletionVO);
}
