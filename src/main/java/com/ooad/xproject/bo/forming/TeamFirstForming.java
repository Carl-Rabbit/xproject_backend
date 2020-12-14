package com.ooad.xproject.bo.forming;

import com.ooad.xproject.dto.StudentProjDTO;
import com.ooad.xproject.entity.ProjectInst;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class TeamFirstForming implements FormingStrategy {
    @Override
    public FormingResultBO forming(ProjectInst[] projInstList, StudentProjDTO[] stdList) {
        List<Pair<Integer, Integer>> matchList = new ArrayList<>();
        int successCnt = 0;

        outer:
        for(ProjectInst projInst: projInstList) {
            int targetMemNum = projInst.getTargetMemNum();
            for (int i = 0; i < targetMemNum; i++) {
                if (successCnt >= stdList.length) {
                    break outer;
                }
                StudentProjDTO std = stdList[successCnt];
                matchList.add(new Pair<>(projInst.getProjInstId(), std.getRoleId()));
                successCnt += 1;
            }
        }

        String message = String.format("Forming finished. Use Team First. " +
                "success:%d, fail:%d", successCnt, stdList.length - successCnt);

        return FormingResultBO.builder()
                .matchList(matchList)
                .message(message)
                .successCnt(successCnt)
                .failCnt(stdList.length - successCnt)
                .build();
    }
}
