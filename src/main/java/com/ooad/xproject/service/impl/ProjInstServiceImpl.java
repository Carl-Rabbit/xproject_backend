package com.ooad.xproject.service.impl;

import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.constant.ProjInstStatus;
import com.ooad.xproject.dto.RecordInstDTO;
import com.ooad.xproject.dto.StudentDTO;
import com.ooad.xproject.entity.ProjInstStudentRT;
import com.ooad.xproject.entity.ProjectInst;
import com.ooad.xproject.entity.RecordInst;
import com.ooad.xproject.entity.SubmissionInst;
import com.ooad.xproject.mapper.*;
import com.ooad.xproject.service.ProjInstService;
import com.ooad.xproject.vo.ProjInstCreationVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@Service
public class ProjInstServiceImpl implements ProjInstService {

    private final ProjectMapper projectMapper;
    private final ProjectInstMapper projectInstMapper;
    private final RecordInstMapper recordInstMapper;
    private final SubmissionInstMapper submissionInstMapper;

    private final ProjInstStudentRTMapper pisRTMapper;

    public ProjInstServiceImpl(ProjectMapper projectMapper, ProjectInstMapper projectInstMapper, RecordInstMapper recordInstMapper, SubmissionInstMapper submissionInstMapper, ProjInstStudentRTMapper pisRTMapper) {
        this.projectMapper = projectMapper;
        this.projectInstMapper = projectInstMapper;
        this.recordInstMapper = recordInstMapper;
        this.submissionInstMapper = submissionInstMapper;
        this.pisRTMapper = pisRTMapper;
    }

    @Override
    public ProjectInst getProjectInst(Integer piid) {
        return projectInstMapper.selectByPrimaryKey(piid);
    }

    @Override
    public List<ProjectInst> getProInstList(Integer projId) {
        return projectInstMapper.selectByProjId(projId);
    }

    @Override
    public List<StudentDTO> getStudentDTOByProjInstId(Integer projInstId) {
        return projectInstMapper.selectStudentByProjInstId(projInstId);
    }


    @Override
    public List<RecordInst> getRecordInstList(Integer rid) {
        return recordInstMapper.selectByRcdId(rid);
    }

    // todo: select Record by project and student
    @Override
    public List<RecordInstDTO> getRecordInstList(Integer projId, Integer stdRoleId) {
        return recordInstMapper.selectByProjAndStdRoleId(projId, stdRoleId);
    }


    @Override
    public List<SubmissionInst> getSubmissionInstList(Integer sbmId) {
        return submissionInstMapper.selectBySbmId(sbmId);
    }

    // todo: select Submission by project and student
    @Override
    public List<SubmissionInst> getSubmissionInstList(Integer pid, Integer sid) {
        return null;
    }

    @Transactional
    @Override
    public SvResult<Boolean> deleteProjInst(int projInstId) {
        int affectedRowCnt = projectInstMapper.deleteByPrimaryKey(projInstId);
        if (affectedRowCnt == 1) {
            return new SvResult<>(0, true);
        } else {
            return new SvResult<>(0, false);
        }
    }

    @Transactional
    @Override
    public SvResult<Boolean> confirmProjInst(int projInstId) {
        ProjectInst record = new ProjectInst();
        record.setProjInstId(projInstId);
        record.setStatus(ProjInstStatus.Confirm.toString());

        int affectedRowCnt = projectInstMapper.updateByPrimaryKeySelective(record);

        if (affectedRowCnt == 1) {
            return new SvResult<>(0, true);
        } else {
            return new SvResult<>(0, false);
        }
    }

    @Override
    public ProjectInst getPIByProjIdAndStdRoleId(int projId, int stdRoleId) {
        return projectInstMapper.selectPIByProjIdAndStdRoleId(projId, stdRoleId);
    }

    @Transactional
    @Override
    public SvResult<Boolean> createProjInst(ProjInstCreationVO projInstCreationVO) {
        try {
            ProjectInst projInst = new ProjectInst();
            projInst.setProjId(projInstCreationVO.getProjId());
            projInst.setTeamName(projInstCreationVO.getTeamName());
            projInst.setTopicStr(projInstCreationVO.getTopic());
            projInst.setTargetMemNum(projInstCreationVO.getIdealSize());
            projInst.setDescription(projInstCreationVO.getDescription());

            int affectedRowCnt = projectInstMapper.insertSelective(projInst);

            if (affectedRowCnt == 0) {
                throw new Exception("Error when insert projInst");
            }

            return new SvResult<>("Create project inst", true);
        } catch (Exception e) {
            // roll back
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return new SvResult<>(e.toString(), false);
        }
    }

    /**
     * Reuse VO to create project inst.
     */
    @Transactional
    @Override
    public SvResult<Boolean> createProjInstAndLink(int stdRoleId, ProjInstCreationVO projInstCreationVO) {
        // check first
        if (this.getPIByProjIdAndStdRoleId(projInstCreationVO.getProjId(), stdRoleId) != null) {
            return new SvResult<>("Already in a team", false);
        }

        try {
            ProjectInst projInst = new ProjectInst();
            projInst.setProjId(projInstCreationVO.getProjId());
            projInst.setTeamName(projInstCreationVO.getTeamName());
            projInst.setTopicStr(projInstCreationVO.getTopic());
            projInst.setTargetMemNum(projInstCreationVO.getIdealSize());
            projInst.setDescription(projInstCreationVO.getDescription());

            int affectedRowCnt = projectInstMapper.insertSelective(projInst);

            if (affectedRowCnt == 0) {
                throw new Exception("Error when insert projInst");
            }

            int projInstId = projInst.getProjInstId();

            // update relate (first member)

            ProjInstStudentRT pisRT = new ProjInstStudentRT();
            pisRT.setStdRoleId(stdRoleId);
            pisRT.setProjInstId(projInstId);
            affectedRowCnt = pisRTMapper.insertSelective(pisRT);

            if (affectedRowCnt == 0) {
                throw new Exception("Error when update ProjInstStudentRT");
            }

            return new SvResult<>("Create project inst, and take student as first member", true);
        } catch (Exception e) {
            // roll back
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return new SvResult<>(e.toString(), false);
        }
    }
}
