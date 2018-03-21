package com.myt.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Z640 on 2018/3/20.
 */

public class Forcast {
    public String date;

    @SerializedName("tmp")
    public Temprature temprature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt_d")
        public String info;
    }
    public class Temprature{
        @SerializedName("max")
        public String maxTempture;
        @SerializedName("min")
        public String minTempture;
    }
}
