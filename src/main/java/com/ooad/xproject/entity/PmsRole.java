package com.ooad.xproject.entity;

public class PmsRole {
    private Integer pmsRoleId;

    private String name;

    public Integer getPmsRoleId() {
        return pmsRoleId;
    }

    public void setPmsRoleId(Integer pmsRoleId) {
        this.pmsRoleId = pmsRoleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}