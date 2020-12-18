package com.ooad.xproject.vo;

import lombok.Data;

@Data
public class RoleIdListParamVO {
    private int projId;
    private String groupMark = "Default";
    private int[] stdRoleIdList;
}

