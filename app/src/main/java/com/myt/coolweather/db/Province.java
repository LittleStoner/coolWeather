package com.myt.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Z640 on 2018/3/18.
 */

public class Province extends DataSupport {
    private int ID;
    private int provinceID;
    private String provinceName;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(int provinceID) {
        this.provinceID = provinceID;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}
