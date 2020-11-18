package com.ooad.xproject.bo;

// Indicate the relationship of the role and its type information
public class SvRoleInfo {
     private Integer roleId;
     private String roleType;
     private Integer typeId;

    public SvRoleInfo(Integer roleId, String roleType) {
        this.roleId = roleId;
        this.roleType = roleType;
    }

    public SvRoleInfo(Integer roleId, String roleType, Integer typeId) {
        this.roleId = roleId;
        this.roleType = roleType;
        this.typeId = typeId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
}
