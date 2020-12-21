package com.ooad.xproject.service.impl;

import com.alibaba.fastjson.JSON;
import com.ooad.xproject.bo.MessageFactory;
import com.ooad.xproject.bo.ProjSettingsBO;
import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.constant.ProjInstStatus;
import com.ooad.xproject.dto.GradeDTO;
import com.ooad.xproject.dto.RecordInstDTO;
import com.ooad.xproject.dto.StudentDTO;
import com.ooad.xproject.dto.StudentProjDTO;
import com.ooad.xproject.entity.*;
import com.ooad.xproject.mapper.*;
import com.ooad.xproject.service.ProjInstService;
import com.ooad.xproject.vo.ApplyReplyParamVO;
import com.ooad.xproject.vo.ApplyTeamParamVO;
import com.ooad.xproject.vo.ProjInstCreationVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjInstServiceImpl implements ProjInstService {

    private final ProjectMapper projectMapper;
    private final ProjectInstMapper projectInstMapper;
    private final RecordInstMapper recordInstMapper;
    private final SubmissionInstMapper submissionInstMapper;
    private final MessageMapper msgMapper;

    private final ProjInstStudentRTMapper pisRTMapper;
    private final RecordMapper recordMapper;

    public ProjInstServiceImpl(ProjectMapper projectMapper, ProjectInstMapper projectInstMapper, RecordInstMapper recordInstMapper, SubmissionInstMapper submissionInstMapper, MessageMapper msgMapper, ProjInstStudentRTMapper pisRTMapper, RecordMapper recordMapper) {
        this.projectMapper = projectMapper;
        this.projectInstMapper = projectInstMapper;
        this.recordInstMapper = recordInstMapper;
        this.submissionInstMapper = submissionInstMapper;
        this.msgMapper = msgMapper;
        this.pisRTMapper = pisRTMapper;
        this.recordMapper = recordMapper;
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
    public SvResult<Boolean> confirmProjInst(int projInstId, boolean isForce) {
        if (!isForce) {
            // check proj inst validate
            SvResult<Boolean> svResult = checkProjInst(projInstId);
            if (!svResult.getData()) {
                return svResult;
            }
        }

        ProjectInst record = new ProjectInst();
        record.setProjInstId(projInstId);
        record.setStatus(ProjInstStatus.Confirm.toString());
        int affectedRowCnt = projectInstMapper.updateByPrimaryKeySelective(record);

        if (affectedRowCnt == 1) {
            return new SvResult<>("Confirm successfully", true);
        } else {
            return new SvResult<>("Confirm failed", false);
        }
    }

    private SvResult<Boolean> checkProjInst(int projInstId) {
        ProjectInst projInst = projectInstMapper.selectByPrimaryKey(projInstId);
        Project project = projectMapper.selectByPrimaryKey(projInst.getProjId());

        ProjSettingsBO settings = JSON.parseObject(project.getProjSettings(), ProjSettingsBO.class);

        // check recruit system
        if (!settings.isUseRecruitSystem()) {
            return new SvResult<>("Recruit system is not open now", false);
        }

        List<StudentProjDTO> stdProjDTOList = projectInstMapper.selectStdProjDTOByProjInstId(projInstId);

        // check size
        if (stdProjDTOList.size() > settings.getMaxSize()) {
            String msg = String.format("Your team is over-sized. Current: %d. Max size: %d",
                    stdProjDTOList.size(), settings.getMaxSize());
            return new SvResult<>(msg, false);
        } else if (stdProjDTOList.size() < settings.getMinSize()) {
            String msg = String.format("Your team has no enough members. Current: %d. Min size: %d",
                stdProjDTOList.size(), settings.getMinSize());
            return new SvResult<>(msg, false);
        }

        // check group mark
        if (!stdProjDTOList.isEmpty() && !settings.isAllowCrossMark()) {
            String groupMark = stdProjDTOList.get(0).getGroupMark();
            for (int i = 1; i < stdProjDTOList.size(); i++) {
                if (!groupMark.equals(stdProjDTOList.get(i).getGroupMark())) {
                    return new SvResult<>("Across group is not allowed", false);
                }
            }
        }

        return new SvResult<>("", true);
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

    @Override
    public boolean quitTeam(int stdRoleId, int projInstId) {
        int affectedRoleCnt = projectInstMapper.deleteProjInstStdRT(stdRoleId, projInstId);
        return affectedRoleCnt == 1;
    }

    @Override
    public boolean updateProjInst(ProjectInst projectInst) {
        int affectedRoleCnt = projectInstMapper.updateByPrimaryKeySelective(projectInst);
        return affectedRoleCnt == 1;
    }

    @Override
    public SvResult<Boolean> applyTeam(Integer roleId, ApplyTeamParamVO atpVO) {
        int projInstId = atpVO.getProjInstId();
        List<StudentDTO> teamMemList = projectInstMapper.selectStudentByProjInstId(projInstId);
        if (teamMemList.isEmpty()) {
            // add this role directly
            System.out.printf("%d, %d", projInstId, roleId);
            projectInstMapper.insertProjInstStdRT(projInstId, roleId, null);
            // TODO send email
            return new SvResult<>("Apply successfully. You are the first member!", true);
        } else {
            ProjectInst projInst = projectInstMapper.selectByPrimaryKey(projInstId);
            Message msg = MessageFactory.createApplyTeamMsg(projInst, roleId, atpVO.getMessage());
            msgMapper.insertSelective(msg);
            // TODO send email to all members
            return new SvResult<>("Apply successfully. Please wait for result!", true);
        }
    }

    @Transactional
    @Override
    public SvResult<Boolean> applyTeamReply(Integer roleId, ApplyReplyParamVO applyReplyParamVO) {
        try {
            Message msg = msgMapper.selectByPrimaryKey(applyReplyParamVO.getMsgId());
            if (msg.getDecided()) {
                return new SvResult<>("This message has been processed", false);
            }

            // change the basic message info
            msg.setDecided(true);
            msg.setContent(applyReplyParamVO.getMessage());
            msg.setResult(applyReplyParamVO.isAccepted() ? "Accept" : "Reject");
            msg.setHandlerRoleId(roleId);

            // check applicant
            // can't use projId in msg here
            ProjectInst projInst = projectInstMapper.selectByPrimaryKey(msg.getProjInstId());
            ProjectInst projInstApplicant = projectInstMapper.selectPIByProjIdAndStdRoleId(projInst.getProjId(), msg.getCreatorRoleId());
            if (projInstApplicant != null) {
                // already has a team
                msg.setResult("Fail to join");
            }

            // update message
            int affectedRowCnt = msgMapper.updateByPrimaryKey(msg);
            if (affectedRowCnt == 1) {
                if (!applyReplyParamVO.isAccepted()) {
                    // reject
                    return new SvResult<>("Application rejected", true);
                }
            } else {
                return new SvResult<>("Error occur when update message", false);
            }

            if (projInstApplicant != null) {
                // already has a team
                return new SvResult<>("Application accepted. Applicant has been in anther team.", true);
            }


            boolean success = projectInstMapper.insertProjInstStdRT(msg.getProjInstId(), msg.getCreatorRoleId(), "Join");

            if (success) {
                return new SvResult<>("Application accepted", true);
            } else {
                return new SvResult<>("Error occur when update team info", false);
            }

        } catch (Exception e) {
            // roll back
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return new SvResult<>("Error occur", false);
        }
    }

    @Override
    public List<GradeDTO> getTeamRecordInstList(int projInstId, int rcdId) {
        return recordInstMapper.selectByProjInstIdAndRcdId(projInstId, rcdId);
    }

    @Override
    public String confirmBatchTch(int[] projInstIdList, boolean isForce) {
        List<Integer> successList = new ArrayList<>();
        for (int projInstId : projInstIdList) {
            SvResult<Boolean> svResult = confirmProjInst(projInstId, isForce);
            if (svResult.getData()) {
                // true
                successList.add(projInstId);
            } else {
                // false
                System.out.printf("postTeamConfirm -> Fail to confirm projInst %d%n", projInstId);
            }
        }
        int successCnt = successList.size();
        String message = String.format("Confirm %d teams. Total %d.", successCnt,
                projInstIdList.length);
        return message;
    }

    @Override
    public List<GradeDTO> getTeamRecordInstListStd(int stdRoleId, int rcdId) {
        Record record = recordMapper.selectByPrimaryKey(rcdId);
        ProjectInst projInst = projectInstMapper.selectPIByProjIdAndStdRoleId(record.getProjId(), stdRoleId);
        if (projInst == null) {
            // no team yet
            return recordInstMapper.selectByStdRoleIdAndRcdId(stdRoleId, rcdId);
        } else {
            List<GradeDTO> gradeList = recordInstMapper.selectByProjInstIdAndRcdId(projInst.getProjInstId(), rcdId);
            for (int idx = 0; idx < gradeList.size(); ++idx) {
                if (gradeList.get(idx).getRoleId() == stdRoleId) {
                    GradeDTO temp = gradeList.remove(idx);
                    gradeList.add(0, temp);
                }
            }
            return gradeList;
        }
    }
}
