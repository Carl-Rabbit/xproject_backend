package com.ooad.xproject.vo;

import com.ooad.xproject.dto.StudentProjDTO;
import com.ooad.xproject.entity.ProjectInst;
import lombok.Data;

import java.util.Arrays;

@Data
public class AutoFormingVO {
    private FormingTeamVO[] teamList;
    private StudentProjDTO[] stuList;
    private String strategy;
    private boolean autoSubmit;

    public ProjectInst[] getProjInstList() {
        return (ProjectInst[]) Arrays.stream(teamList).map(FormingTeamVO::toProjectInst).toArray();
    }
}
