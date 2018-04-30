package com.example.hfnunavigation.db;

import org.litepal.crud.DataSupport;

public class SchoolLocation extends DataSupport{

    private int id;
    private String LocationName;
    private double Latitude;
    private double Longitude;
    private String moreInfo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        this.LocationName = locationName;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

}
