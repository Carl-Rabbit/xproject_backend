package com.ooad.xproject.bo.forming;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.math3.util.Pair;

import java.util.List;

@Data
@Builder
public class FormingResultBO {
    private List<Pair<Integer, Integer>> matchList;
    private String message;
    private int successCnt;
    private int failCnt;
}
