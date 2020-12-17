package com.ooad.xproject.bo;

import com.ooad.xproject.vo.EventInstCreationVO;
import org.apache.commons.math3.util.Pair;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventIntervalAlgo {
    public static List<Pair<Date, Date>> generateTimeIntervals(EventInstCreationVO eicVO) throws ParseException {
        Date startTime = eicVO.getStartTime();
        long timestamp = startTime.getTime();
        long duration = eicVO.getDuration() * 1000 * 60;        // parse minute to ms
        List<Pair<Date, Date>> timeList = new ArrayList<>(eicVO.getCounts());

        for (int i = 0; i < eicVO.getCounts(); i++) {
            Date start = new Date(timestamp);
            timestamp += duration;
            Date end = new Date(timestamp + (i + 1) * eicVO.getDuration());

            Pair<Date, Date> pair = new Pair<>(start, end);
            timeList.add(pair);
        }
        return timeList;
    }
}
