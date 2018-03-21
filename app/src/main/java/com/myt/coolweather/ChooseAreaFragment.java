package com.myt.coolweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.myt.coolweather.db.City;
import com.myt.coolweather.db.Country;
import com.myt.coolweather.db.Province;
import com.myt.coolweather.util.HttpUtil;
import com.myt.coolweather.util.Utility;

import org.json.JSONException;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Z640 on 2018/3/16.
 */

public class ChooseAreaFragment  extends Fragment{


    private  static int NONE_LEVEL = 0;
    private  static int PROVINCTY_LEVEL = 1;
    private  static int CITY_LEVEL = 2;
    private  static int COUNTRY_LEVEL = 3;
    private  int currentLevel = NONE_LEVEL;

    private ProgressDialog progressDialog;
    private Button backButton;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList = new ArrayList<>();
    private List<City> cityList = new ArrayList<>();
    private List<Country> countryList = new ArrayList<>();
    private Province selectProvince = new Province();
    private City selectCity = new City();
    private Country selectCountry = new Country();



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area_fragment,container,false);

        backButton = view.findViewById(R.id.back_bt);
        titleText = view.findViewById(R.id.title_tv);
        listView = view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               // Toast.makeText(getActivity(),dataList.get(i),Toast.LENGTH_SHORT).show();
                if(currentLevel == PROVINCTY_LEVEL){
                    selectProvince = provinceList.get(i);
                    queryCity();
                }else if(currentLevel == CITY_LEVEL){
                    selectCity = cityList.get(i);
                    queryCountry();
                }else if(currentLevel == COUNTRY_LEVEL){
                    String weatherId = countryList.get(i).getWeather_id();
                    Intent intent = new Intent(getActivity(),WeatherActivity.class);
                    intent.putExtra("weather_id",weatherId);
                    startActivity(intent);
                    getActivity().finish();
                }



            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel == CITY_LEVEL){
                    queryProvince();
                }else if(currentLevel == COUNTRY_LEVEL){
                    queryCity();
                }
            }
        });
        queryProvince();
    }

    private  void queryProvince(){
        backButton.setVisibility(View.GONE);
        titleText.setText("中国");
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size() > 0){
            dataList.clear();
            for (Province province : provinceList){
                dataList.add(province.getProvinceName());
                currentLevel = PROVINCTY_LEVEL;
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
        }else {
            queryServer("http://guolin.tech/api/china/","province");
        }
    }


    private void queryCity(){
        backButton.setVisibility(View.VISIBLE);
        titleText.setText(selectProvince.getProvinceName());
        cityList = DataSupport.where("provinceID = ?",String.valueOf(selectProvince.getProvinceID())).find(City.class);
        if(cityList.size() > 0){
            dataList.clear();
            for(City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(2);
            currentLevel = CITY_LEVEL;
        }else{
            queryServer("http://guolin.tech/api/china/"+selectProvince.getProvinceID(),"city");
        }
    }
    private void queryCountry(){
        backButton.setVisibility(View.VISIBLE);
        titleText.setText(selectCity.getCityName());
        countryList = DataSupport.where("cityID = ?",String.valueOf(selectCity.getCityID())).find(Country.class);
        if(countryList.size() > 0){
            dataList.clear();
            for(Country country : countryList){
                dataList.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = COUNTRY_LEVEL;
        }else {
            queryServer("http://guolin.tech/api/china/"+selectProvince.getProvinceID()+"/"+selectCity.getCityID(),"country");
        }
    }
    private void queryServer(String addr, final String areaType){
        showProgressDialog();
        HttpUtil.requestHttp(addr, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                  getActivity().runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          closeProgressDialog();
                          Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                      }
                  });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                boolean result = false;
                   if("province".equals(areaType)){
                       try {
                           result = Utility.handleProvince(response.body().string());
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                   }else if ("city".equals(areaType)){
                       try {
                           result = Utility.handleCity(response.body().string(),selectProvince.getProvinceID());
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                   }else if ("country".equals(areaType)){
                       try {
                           result = Utility.handleCountry(response.body().string(),selectCity.getCityID());
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                   }
                if(result == true){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if(currentLevel == NONE_LEVEL){
                                queryProvince();
                            }else if(currentLevel == PROVINCTY_LEVEL){
                                queryCity();
                            }else if(currentLevel == CITY_LEVEL){
                                queryCountry();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载....");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
