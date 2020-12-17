package com.ooad.xproject.service.impl;

import com.ooad.xproject.bo.RecordUnitBO;
import com.ooad.xproject.bo.SvResult;
import com.ooad.xproject.bo.forming.FormingBO;
import com.ooad.xproject.bo.forming.FormingResultBO;
import com.ooad.xproject.dto.StudentProjDTO;
import com.ooad.xproject.entity.*;
import com.ooad.xproject.mapper.*;
import com.ooad.xproject.service.ProjectService;
import com.ooad.xproject.vo.ProjectVO;
import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final AnnouncementMapper announcementMapper;
    private final EventArrangeTaskMapper eventArrangeTaskMapper;
    private final SubmissionMapper submissionMapper;
    private final RecordMapper recordMapper;
    private final ProjectMapper projectMapper;
    private final ProjectInstMapper projectInstMapper;
    private final TeamFormTaskMapper teamFormTaskMapper;
    private final StudentMapper studentMapper;

    public ProjectServiceImpl(AnnouncementMapper announcementMapper, EventArrangeTaskMapper eventArrangeTaskMapper, SubmissionMapper submissionMapper, RecordMapper recordMapper, ProjectMapper projectMapper, ProjectInstMapper projectInstMapper, TeamFormTaskMapper teamFormTaskMapper, StudentMapper studentMapper) {
        this.announcementMapper = announcementMapper;
        this.eventArrangeTaskMapper = eventArrangeTaskMapper;
        this.submissionMapper = submissionMapper;
        this.recordMapper = recordMapper;
        this.projectMapper = projectMapper;
        this.projectInstMapper = projectInstMapper;
        this.teamFormTaskMapper = teamFormTaskMapper;
        this.studentMapper = studentMapper;
    }

    @Override
    public Project getProject(Integer pid) {
        return projectMapper.selectByPrimaryKey(pid);
    }

    @Override
    public List<ProjectInst> getProjectInstList(Integer pid) {
        return projectInstMapper.selectByProjId(pid);
    }

    @Override
    public List<Announcement> getAnnounceList(Integer pid) {
        return announcementMapper.selectByProjId(pid);
    }

    @Override
    public List<EventArrangeTask> getEventList(Integer pid) {
        return eventArrangeTaskMapper.selectByProjId(pid);
    }

    @Override
    public List<Submission> getSubmissionList(Integer pid) {
        return submissionMapper.selectByProjId(pid);
    }

    @Override
    public List<Record> getRecordList(Integer pid) {
        return recordMapper.selectByProjId(pid);
    }

    @Override
    public TeamFormTask getTeamFormTask(Integer pid) {
        return teamFormTaskMapper.selectByPrimaryKey(pid);
    }

    @Override
    public boolean updateProject(ProjectVO projectVO) {
        Project proj = new Project();
        proj.setProjId(projectVO.getProjId());
        proj.setDescription(projectVO.getDescription());
        proj.setProjSettings(projectVO.getProjSettings());
        proj.setTopics(projectVO.getTopics());

        int affectedRowCnt = projectMapper.updateByPrimaryKeySelective(proj);

        return affectedRowCnt == 1;
    }

    @Override
    public List<RecordUnitBO> getRecordUnitList(Integer pid) {
        return projectMapper.selectRecordUnitByProjId(pid);
    }

    @Override
    public List<StudentProjDTO> getStdProjList(int projId) {
        return studentMapper.selectProjStudents(projId);
    }

    @Override
    public SvResult<FormingResultBO> autoForming(FormingBO formingBO) {
        FormingResultBO res = formingBO.executeForming();
        for (Pair<Integer, Integer> pair : res.getMatchList()) {
            try {
                boolean success = projectInstMapper.insertProjInstStdRT(pair.getFirst(), pair.getSecond(), null);
                if (!success) {
                    res.reduceSuccess(1);
                    System.out.println("Fail in autoForming " + pair.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
                res.reduceSuccess(1);
            }
        }

        System.out.println(Arrays.toString(res.getMatchList().toArray()));
        System.out.println(res.getMessage());

        return new SvResult<>("", res);
    }
}
