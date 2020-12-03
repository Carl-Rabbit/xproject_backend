package com.ooad.xproject.vo;

import com.ooad.xproject.entity.ProjectInst;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * Simplified team view obj shown in card
 */
@Data
public class SimpleTeamVO {
    private int projInstId;
    private String teamName;
    private String topic;
    private String status;
    private List<String> tags;
    private String descriptions;

    public static SimpleTeamVO createFrom(ProjectInst projectInst) {
        SimpleTeamVO ret = new SimpleTeamVO();
        ret.projInstId = projectInst.getProjInstId();
        ret.teamName = projectInst.getTeamName();
        ret.topic = projectInst.getTopicStr();
        ret.status = projectInst.getStatus();
        ret.tags = Arrays.asList(projectInst.getFlags().split(","));
        ret.descriptions = projectInst.getDescription();
        return ret;
    }
}
