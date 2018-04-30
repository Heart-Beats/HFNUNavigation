package com.example.hfnunavigation.util;

import com.example.hfnunavigation.db.HistoricalTrack;

import java.util.Comparator;

import javax.xml.datatype.Duration;

public class MyComparator<T> implements Comparator<T> {
    @Override
    public int compare(T o1, T o2) {
        long duration = 0;
        if (o1 instanceof HistoricalTrack && o2 instanceof HistoricalTrack) {
             duration =   ((HistoricalTrack) o1).getHistoryTime().getTime()
                    - ((HistoricalTrack) o2).getHistoryTime().getTime();

        }
        //升序
       return -((duration == 0) ? 0 :(duration > 0  ? 1 : -1));
    }
}
