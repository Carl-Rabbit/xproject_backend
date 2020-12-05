package com.ooad.xproject.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class RecordInstDTO {
    private int rcdId;
    private int rcdInstId;
    private String rcdName;
    private String type;
    private Timestamp createdTime;
    private Timestamp modifiedTime;
    private String derived;
    private String tchName;
    private String email;
    private String content;
    private String baseContent;
    private String comments;

    private String derivedStr;

    public void setDerived(String derived) {
        this.derived = derived;
        this.derivedStr = "";
        if ("".equals(derived)) {
            return;
        }

        for (String s : derived.split(",")) {
            String[] ss = s.split("#");
            this.derivedStr += ss[1] + " ";
        }

        this.derivedStr = this.derivedStr.substring(0, derivedStr.length() - 1);
    }
}
