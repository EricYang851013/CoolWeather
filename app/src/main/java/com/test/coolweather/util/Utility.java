package com.test.coolweather.util;

import android.text.TextUtils;

import com.test.coolweather.db.City;
import com.test.coolweather.db.County;
import com.test.coolweather.db.Province;
import com.test.coolweather.gson.Weather;
import com.google.gson.reflect.TypeToken;


import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;

import java.util.List;

public class Utility {
    public static boolean handleProvinceResponse(String response){
        if (TextUtils.isEmpty(response)){
            return  false;
        }
        Type type = new TypeToken<List<Province>>(){}.getType();
        List<Province> provinces =  GsonBuildUtil.buildGson().fromJson(response, type);
      
        for (Province pro:
                provinces) {
            pro.save();
        }
        return true;
    }

    public static boolean handleCityResponse(String response,int provinceId){
        if (TextUtils.isEmpty(response)){
            return  false;
        }
        Type type = new TypeToken<List<City>>(){}.getType();

        List<City> cities =  GsonBuildUtil.buildGson().fromJson(response,  type);
        for (City pro:
                cities) {
            pro.setProvinceId(provinceId);
            pro.save();
        }
        return true;
    }

    public static boolean handleCountyResponse(String response,int cityId){
        if (TextUtils.isEmpty(response)){
            return  false;
        }
        Type type = new TypeToken<List<County>>(){}.getType();

        List<County> counties =  GsonBuildUtil.buildGson().fromJson(response,  type);

        for (County pro:
                counties) {
            pro.setCityId(cityId);
            pro.save();
        }
        return true;
    }

    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject  = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return GsonBuildUtil.buildGson().fromJson(weatherContent, Weather.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
            return null;
    }
}
