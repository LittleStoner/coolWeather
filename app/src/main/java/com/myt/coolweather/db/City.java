package com.myt.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Z640 on 2018/3/18.
 */

public class City extends DataSupport {
    private int ID;
    private int cityID;
    private int provinceID;
    private String cityName;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(int provinceID) {
        this.provinceID = provinceID;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }


}
