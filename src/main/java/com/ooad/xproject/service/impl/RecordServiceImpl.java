package com.ooad.xproject.service.impl;

import com.ooad.xproject.bo.RecordUnitBO;
import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.entity.Record;
import com.ooad.xproject.entity.RecordInst;
import com.ooad.xproject.mapper.RecordInstMapper;
import com.ooad.xproject.mapper.RecordMapper;
import com.ooad.xproject.mapper.StudentMapper;
import com.ooad.xproject.service.RecordService;
import com.ooad.xproject.vo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@Service
public class RecordServiceImpl implements RecordService {

    private final RecordMapper recordMapper;
    private final RecordInstMapper recordInstMapper;
    private final StudentMapper studentMapper;

    public RecordServiceImpl(RecordMapper recordMapper, RecordInstMapper recordInstMapper, StudentMapper studentMapper) {
        this.recordMapper = recordMapper;
        this.recordInstMapper = recordInstMapper;
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
        record.setCreatorId(roleId);
        int affectedRowCnt = recordMapper.insertSelective(record);
        return affectedRowCnt == 1;
    }

    @Override
    public List<Record> getRecordList(int projId) {
        return recordMapper.selectByProjId(projId);
    }

    @Override
    public SvResult<Integer> deleteRecords(RecordDeletionVO recordDeletionVO) {
        int success = 0;
        for (int rcdId: recordDeletionVO.getRcdIdList()) {
            success += recordMapper.deleteByPrimaryKey(rcdId);
        }
        String message = String.format("Delete %d records. Give: %d.",
                success,
                recordDeletionVO.getRcdIdList().length);
        return new SvResult<>(message, success);
    }

    @Override
    public SvResult<RecordInstUpdateRetVO> updateRecordInsts(int roleId, RecordInstUpdateParamVO recordInstUpdateParamVO) {
        GradeUpdate[] gradeUpdateList = recordInstUpdateParamVO.getGradeList();
        int rcdId = recordInstUpdateParamVO.getRcdId();

        // check rcd
        Record rcd = recordMapper.selectByPrimaryKey(recordInstUpdateParamVO.getRcdId());
        if (rcd == null) {
            return new SvResult<>("No such record", null);
        }

        RecordInstUpdateRetVO rcdInstUpdateRetVO = new RecordInstUpdateRetVO();
        rcdInstUpdateRetVO.setTotal(gradeUpdateList.length);
        for (GradeUpdate grade: gradeUpdateList) {
            RecordInst recordInst = recordInstMapper.selectByRcdIdAndRoleId(rcdId, grade.getRoleId());
            if (recordInst == null) {
                // create it
                recordInst = new RecordInst();
                recordInst.setRcdId(rcdId);
                recordInst.setRoleId(grade.getRoleId());
                recordInst.setContent(grade.getContent());
                recordInst.setComments(grade.getComments());
                int affectedRowCnt = recordInstMapper.insertSelective(recordInst);
                rcdInstUpdateRetVO.updateSuccessOrFail(affectedRowCnt == 1);
                rcdInstUpdateRetVO.incrCreate();
            } else {
                // update it
                grade.copyToRecordInst(recordInst);
                int affectedRowCnt = recordInstMapper.updateByPrimaryKey(recordInst);
                rcdInstUpdateRetVO.updateSuccessOrFail(affectedRowCnt == 1);
                rcdInstUpdateRetVO.incrModify();
            }
        }
        return new SvResult<>(rcdInstUpdateRetVO.generateMessage(), rcdInstUpdateRetVO);
    }

    @Transactional
    @Override
    public SvResult<Integer> updateRecordInstsBatch(Integer roleId, CombineRcdInstParamVO combineRcdInstParamVO) {
        int[] rcdIdList = combineRcdInstParamVO.getRcdIdList();
        double[] coeList = combineRcdInstParamVO.getCoeList();

        if (rcdIdList.length == 0) {
            return new SvResult<>("Record list is empty", 0);
        }

        try {
            Record record = recordMapper.selectByPrimaryKey(rcdIdList[0]);

            Record newRecord = new Record();
            newRecord.setProjId(record.getProjId());
            newRecord.setCreatorId(roleId);
            newRecord.setRcdName(combineRcdInstParamVO.getRecordName());
            newRecord.setType("Point");
            recordMapper.insertSelective(newRecord);

            int successCnt = recordInstMapper.generateRecordInst(record.getProjId(),
                    newRecord.getRcdId(), roleId, rcdIdList, coeList);

            return new SvResult<>("Execute finished", successCnt);
        } catch (Exception e) {
            // roll back
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return new SvResult<>(e.toString(), 0);
        }
    }
}
