package com.ooad.xproject.vo;

import lombok.Data;

@Data
public class ProjectUpdateVO {
    private int projId;
    private String description;
    private String topics;
    private String projSettings;
}

