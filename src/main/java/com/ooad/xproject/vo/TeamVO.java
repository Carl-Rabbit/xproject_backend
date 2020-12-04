package com.ooad.xproject.vo;

import com.ooad.xproject.dto.StudentDTO;
import com.ooad.xproject.entity.ProjectInst;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * Full team view obj shown in drawer
 */
@Data
public class TeamVO {
    private int projInstId;
    private String teamName;
    private String topic;
    private String status;
    private List<String> tags;
    private String descriptions;
    private int targetMemNum;
    private List<StudentDTO> teamMemberList;

    public static TeamVO createFrom(ProjectInst projectInst, List<StudentDTO> teamMemberList) {
        TeamVO ret = new TeamVO();
        ret.projInstId = projectInst.getProjInstId();
        ret.teamName = projectInst.getTeamName();
        ret.topic = projectInst.getTopicStr();
        ret.status = projectInst.getStatus();
        ret.tags = Arrays.asList(projectInst.getFlags().split(","));
        ret.descriptions = projectInst.getDescription();
        ret.targetMemNum = projectInst.getTargetMemNum();

        ret.teamMemberList = teamMemberList;

        return ret;
    }
}
