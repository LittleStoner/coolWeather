package com.myt.coolweather.gson;

/**
 * Created by Z640 on 2018/3/20.
 */

public class AQI {
    public AQICity city;
    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
