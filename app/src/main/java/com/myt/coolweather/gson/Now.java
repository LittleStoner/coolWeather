package com.myt.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Z640 on 2018/3/20.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt")
        public String info;
    }
}
