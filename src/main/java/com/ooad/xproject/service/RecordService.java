package com.ooad.xproject.service;

import com.ooad.xproject.bo.RecordUnitBO;
import com.ooad.xproject.entity.RecordInst;

import java.util.List;

public interface RecordService {

    RecordInst getRecordInstByUnit(RecordUnitBO recordUnitBO, Integer projId);
}
