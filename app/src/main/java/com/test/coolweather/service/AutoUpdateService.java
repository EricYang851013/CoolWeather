package com.test.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.coolweather.gson.ImageInfo;
import com.test.coolweather.gson.Weather;
import com.test.coolweather.util.HttpUtil;
import com.test.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updatePic();
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int hour = 8 * 60 *60 *1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + hour;
        Intent  i =new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        alarmManager.cancel(pi);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,  pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherStr = pref.getString("weather", null);
        if(weatherStr != null){
            Weather weather = Utility.handleWeatherResponse(weatherStr);
            String weatherId = weather.basic.weatherId;
            String weahterUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=86f7ad696c02457aa6a9dec556da6a79";
            HttpUtil.sendOKHttpReuqest(weahterUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    final Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status)){
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                    }
                }
            });
            
        }

    }

    private void updatePic(){
        String requestUrl = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
        HttpUtil.sendOKHttpReuqest(requestUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Gson gson = new Gson();
                ImageInfo imageInfo = gson.fromJson(responseText, new TypeToken<ImageInfo>(){}.getType());
                ImageInfo.ImageSubInfo subInfo = imageInfo.images.get(0);
                if(subInfo != null && subInfo.url.length() > 0){
                    String url = "http://cn.bing.com" + subInfo.url;
                    loadBingPic(url);
                }



            }
        });
    }
    private void loadBingPic(String picUrl){
        final String bingPic = picUrl;
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
        editor.putString("bing_pic", bingPic);
        editor.apply();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}