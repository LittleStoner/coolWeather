package com.myt.coolweather.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.myt.coolweather.db.City;
import com.myt.coolweather.db.Country;
import com.myt.coolweather.db.Province;
import com.myt.coolweather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Z640 on 2018/3/18.
 */

public class Utility {
    public static boolean handleProvince(String response) throws JSONException {
        if(!TextUtils.isEmpty(response)) {
            JSONArray jsonArray = new JSONArray(response);
            for(int i=0; i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Province province = new Province();
                province.setProvinceID(jsonObject.getInt("id"));
                province.setProvinceName(jsonObject.getString("name"));
                province.save();
            }
            return true;
        }
       return false;
    }
    public static boolean handleCity(String reponse,int provinceID) throws JSONException {
        if(!TextUtils.isEmpty(reponse)){
            JSONArray jsonArray = new JSONArray(reponse);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                City city = new City();
                city.setCityID(jsonObject.getInt("id"));
                city.setCityName(jsonObject.getString("name"));
                city.setProvinceID(provinceID);
                city.save();
            }
            return true;
        } else{
            return false;
        }
    }

    public static  boolean handleCountry(String response,int cityID) throws JSONException {
        if(!TextUtils.isEmpty(response)){
            JSONArray jsonArray = new JSONArray(response);
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Country country = new Country();
                country.setCityID(cityID);;
                country.setCountryID(jsonObject.getInt("id"));
                country.setCountryName(jsonObject.getString("name"));
                country.setWeather_id(jsonObject.getString("weather_id"));
                country.save();
            }
            return true;
        }
        return false;
    }

    public static Weather handlerWeatherResponse(String reponse) throws JSONException {
        JSONObject jsonObject = new JSONObject(reponse);
        JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
        String s = jsonArray.get(0).toString();
        Gson gson = new Gson();
        return gson.fromJson(s,Weather.class);
    }
}
