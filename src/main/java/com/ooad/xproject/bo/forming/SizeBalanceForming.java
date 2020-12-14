package com.ooad.xproject.bo.forming;

import com.ooad.xproject.dto.StudentProjDTO;
import com.ooad.xproject.entity.ProjectInst;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SizeBalanceForming implements FormingStrategy {
    @Override
    public FormingResultBO forming(ProjectInst[] projInstList, StudentProjDTO[] stdList) {
        List<Pair<Integer, Integer>> matchList = new ArrayList<>();
        int successCnt = 0;

        int sum = Arrays.stream(projInstList).mapToInt(ProjectInst::getTargetMemNum).sum();
        int eachNum = sum / stdList.length;

        for (ProjectInst projInst: projInstList) {
            for (int i = 0; i < eachNum; i++) {
                StudentProjDTO std = stdList[successCnt];
                matchList.add(new Pair<>(projInst.getProjInstId(), std.getRoleId()));
                successCnt += 1;
            }
        }

        // one more turn to add
        for (ProjectInst projInst: projInstList) {
            StudentProjDTO std = stdList[successCnt];
            matchList.add(new Pair<>(projInst.getProjInstId(), std.getRoleId()));
            successCnt += 1;
        }

        String message = String.format("Forming finished. Use Size Balance. " +
                "success:%d, fail:%d", successCnt, stdList.length - successCnt);

        return FormingResultBO.builder()
                .matchList(matchList)
                .message(message)
                .successCnt(successCnt)
                .failCnt(stdList.length - successCnt)
                .build();
    }
}
