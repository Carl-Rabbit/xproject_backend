package com.ooad.xproject.service.impl;

import com.ooad.xproject.bo.RecordUnitBO;
import com.ooad.xproject.entity.Record;
import com.ooad.xproject.entity.RecordInst;
import com.ooad.xproject.mapper.RecordMapper;
import com.ooad.xproject.mapper.StudentMapper;
import com.ooad.xproject.service.RecordService;
import com.ooad.xproject.vo.RecordCreationVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordServiceImpl implements RecordService {

    private final RecordMapper recordMapper;

    private final StudentMapper studentMapper;

    public RecordServiceImpl(RecordMapper recordMapper, StudentMapper studentMapper) {
        this.recordMapper = recordMapper;
        this.studentMapper = studentMapper;
    }

    @Override
    public RecordInst getRecordInstByUnit(RecordUnitBO recordUnitBO, Integer projId) {
        RecordInst recordInst = new RecordInst();
        try {
            recordInst.setRcdId(recordMapper.selectByProjIdAndRcdName(projId, recordUnitBO.getRcdName()).getRcdId());
            recordInst.setRoleId(studentMapper.selectByStdNo(recordUnitBO.getStdNo()).getRoleId());
        } catch (Exception e) {
            return null;
        }

        return recordInst;
    }

    @Override
    public boolean createNewRecord(Integer roleId, RecordCreationVO recordCreationVO) {
        Record record = new Record();
        recordCreationVO.copyToRecord(record);
        int affectedRowCnt = recordMapper.insertSelective(record);
        return affectedRowCnt == 1;
    }

    @Override
    public List<Record> getRecordList(int projId) {
        return recordMapper.selectByProjId(projId);
    }
}
