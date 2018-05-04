package com.example.hfnunavigation.util;

import com.example.hfnunavigation.activity.PreferredLocationActivity;
import com.example.hfnunavigation.db.HistoricalTrack;
import java.util.Comparator;


public class MyComparator<T> implements Comparator<T> {
    @Override
    public int compare(T o1, T o2) {
        long duration = 0;
        if (o1 instanceof HistoricalTrack && o2 instanceof HistoricalTrack) {
            duration = ((HistoricalTrack) o1).getHistoryTime().getTime()
                    - ((HistoricalTrack) o2).getHistoryTime().getTime();
        } else if (o1 instanceof PreferredLocationActivity.PreferredLocation
                && o2 instanceof PreferredLocationActivity.PreferredLocation) {
            duration = ((PreferredLocationActivity.PreferredLocation) o1).getHistortyTimes()
                    - ((PreferredLocationActivity.PreferredLocation) o2).getHistortyTimes();
        }
        //升序
        return -((duration == 0) ? 0 : (duration > 0 ? 1 : -1));
    }
}
