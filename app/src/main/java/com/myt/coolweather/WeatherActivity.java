package com.myt.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.myt.coolweather.gson.Forcast;
import com.myt.coolweather.gson.Weather;
import com.myt.coolweather.util.HttpUtil;
import com.myt.coolweather.util.Utility;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private TextView titleText;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfotText;
    private TextView washCarText;
    private TextView sportText;
    private LinearLayout forcastLayout;
    private ImageView bingPicImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        View  decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        titleText = (TextView)findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);
        aqiText = (TextView)findViewById(R.id.aqi_text);
        pm25Text = (TextView)findViewById(R.id.pm25_text);
        comfotText = (TextView)findViewById(R.id.comfort_text);
        washCarText = (TextView)findViewById(R.id.washcar_text);
        sportText = (TextView)findViewById(R.id.sport_text);
        forcastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
        bingPicImg = (ImageView)findViewById(R.id.biying_imageView);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherStr = prefs.getString("weather",null);
        if(weatherStr != null){
            try {
                Weather weather = Utility.handlerWeatherResponse(weatherStr);
                showWeather(weather);

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            String  weatherId = getIntent().getStringExtra("weather_id");
            requestWeather(weatherId);
        }
        String bingPic = prefs.getString("bing_pic",null);
        if(bingPic != null){
            Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void showWeather(Weather weather){
        titleText.setText(weather.basic.cityName);
        titleUpdateTime.setText(weather.basic.update.updateTime);
        degreeText.setText(weather.now.temperature+"℃");
        weatherInfoText.setText(weather.now.more.info);
        aqiText.setText(weather.aqi.city.aqi);
        pm25Text.setText(weather.aqi.city.pm25);
        comfotText.setText("舒适度："+weather.suggestion.comfort.info);
        washCarText.setText("洗车指数："+weather.suggestion.carWash.info);
        sportText.setText("运动建议："+weather.suggestion.sport.info);
        for(Forcast forcast : weather.forcasts){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forcastLayout,false);
            TextView dataText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dataText.setText(forcast.date);
            infoText.setText(forcast.more.info);
            maxText.setText(forcast.temprature.maxTempture);
            minText.setText(forcast.temprature.minTempture);
            forcastLayout.addView(view);
        }

    }
    private void requestWeather(final String weatherId){
        String string = "http://guolin.tech/api/weather?cityid="+weatherId+"&key=02e4bf8192bf4c198049541c935f64a3";


        HttpUtil.requestHttp(string, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气失败",Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String s = response.body().string();
                try {
                    final Weather weather = Utility.handlerWeatherResponse(s);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(weather!=null && "ok".equals(weather.status)){
                                SharedPreferences sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("weather",s);
                                editor.apply();
                                showWeather(weather);
                            }else {
                                Toast.makeText(WeatherActivity.this,"获取天气失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        loadBingPic();
    }
    private void loadBingPic(){
        String picUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.requestHttp(picUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String picStr = response.body().string();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("bing_pic",picStr);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(picStr).into(bingPicImg);
                    }
                });
            }
        });
    }

}
