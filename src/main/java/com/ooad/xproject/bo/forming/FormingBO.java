package com.ooad.xproject.bo.forming;

import com.ooad.xproject.dto.StudentProjDTO;
import com.ooad.xproject.entity.ProjectInst;
import lombok.Setter;

public class FormingBO {
    public static final String RAND = "Rand";
    public static final String TEAM_FIRST = "Team First";
    public static final String SIZE_BALANCE = "Size Balance";

    private FormingStrategy strategy;
    private @Setter ProjectInst[] projInstList;
    private @Setter StudentProjDTO[] stdList;

    public static String[] getStrategyList() {
        return new String[]{RAND, TEAM_FIRST, SIZE_BALANCE};
    }

    public boolean setStrategy(String strategyName) {
        switch (strategyName) {
            case RAND: this.strategy = new RandForming(); break;
            case TEAM_FIRST: this.strategy = new TeamFirstForming(); break;
            case SIZE_BALANCE: this.strategy = new SizeBalanceForming(); break;
            default: return false;
        }
        return true;
    }

    public FormingResultBO executeForming() {
        if (projInstList == null || stdList == null) {
            throw new NullPointerException("Forming params have not set");
        }
        return this.strategy.forming(projInstList, stdList);
    }
}
