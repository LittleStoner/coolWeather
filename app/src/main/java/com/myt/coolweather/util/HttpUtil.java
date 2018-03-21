package com.myt.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Z640 on 2018/3/18.
 */

public class HttpUtil {
    public static void requestHttp(String addr ,okhttp3.Callback callback){
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(addr).build();
        httpClient.newCall(request).enqueue(callback);
    }
}
