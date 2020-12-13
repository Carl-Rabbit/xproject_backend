package com.ooad.xproject.service.impl;

import com.ooad.xproject.dto.StudentProjDTO;
import com.ooad.xproject.entity.*;
import com.ooad.xproject.mapper.*;
import com.ooad.xproject.service.ProjectService;
import com.ooad.xproject.vo.ProjectUpdateVO;
import org.springframework.stereotype.Service;

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
    public boolean updateProject(ProjectUpdateVO projectUpdateVO) {
        Project proj = new Project();
        proj.setProjId(projectUpdateVO.getProjId());
        proj.setDescription(projectUpdateVO.getDescription());
        proj.setProjSettings(projectUpdateVO.getProjSettings());
        proj.setTopics(projectUpdateVO.getTopics());

        int affectedRowCnt = projectMapper.updateByPrimaryKeySelective(proj);

        return affectedRowCnt == 1;
    }

    @Override
    public List<StudentProjDTO> getStdProjList(int projId) {
        return studentMapper.selectProjStudents(projId);
    }
}
