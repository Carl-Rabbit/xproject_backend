package com.ooad.xproject.vo;

import com.ooad.xproject.entity.EventArrangeTask;
import com.ooad.xproject.utils.TimeUtils;
import lombok.Data;

@Data
public class EATaskCreationVO {
    private int projId;
    private String title;
    private String description;
    private String dueTime;     // standard time format
    private boolean stdAdaptable;
    private boolean autoDistribution;

    public void copyToEATask(EventArrangeTask eaTask, int creatorRoleId) {
        eaTask.setProjId(projId);
        eaTask.setTitle(title);
        eaTask.setDescription(description);
        eaTask.setDueTime(TimeUtils.strToDateStandard(dueTime));
        eaTask.setStdAdaptable(stdAdaptable);
        eaTask.setCreatorId(creatorRoleId);
    }
}

