package com.example.hfnunavigation.db;

import org.litepal.crud.DataSupport;
import java.util.Date;

public class HistoricalTrack extends DataSupport {

    private boolean checkBoxVisibility;
    private String startPlaceName;
    private String endPlaceName;
    private Date historyTime;

    public boolean isCheckBoxVisibility() {
        return checkBoxVisibility;
    }

    public void setCheckBoxVisibility(boolean checkBoxVisibility) {
        this.checkBoxVisibility = checkBoxVisibility;
    }

    public String getStartPlaceName() {
        return startPlaceName;
    }

    public void setStartPlaceName(String startPlaceName) {
        this.startPlaceName = startPlaceName;
    }

    public String getEndPlaceName() {
        return endPlaceName;
    }

    public void setEndPlaceName(String endPlaceName) {
        this.endPlaceName = endPlaceName;
    }

    public Date getHistoryTime() {
        return historyTime;
    }

    public void setHistoryTime(Date historyTime) {
        this.historyTime = historyTime;
    }
}
