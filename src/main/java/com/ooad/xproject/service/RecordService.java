package com.ooad.xproject.service;

import com.ooad.xproject.bo.RecordUnitBO;
import com.ooad.xproject.entity.RecordInst;
import com.ooad.xproject.vo.RecordCreationVO;

public interface RecordService {

    RecordInst getRecordInstByUnit(RecordUnitBO recordUnitBO, Integer projId);

    boolean createNewRecord(Integer roleId, RecordCreationVO recordCreationVO);
}
