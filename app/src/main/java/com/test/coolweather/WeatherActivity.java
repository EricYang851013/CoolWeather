package com.test.coolweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.coolweather.gson.Forecast;
import com.test.coolweather.gson.ImageInfo;
import com.test.coolweather.gson.Weather;
import com.test.coolweather.service.AutoUpdateService;
import com.test.coolweather.util.HttpUtil;
import com.test.coolweather.util.Utility;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;




public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private  TextView titleUpdateTime;
    private  TextView degreeText;
    private  TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private  TextView aqiText;
    private  TextView pm25Text;
    private  TextView comfortText;
    private  TextView carWashText;
    private  TextView sportText;
    private Button backButton;

    private ImageView bingPicImg;
    final String weatherKey = "weather";

    public SwipeRefreshLayout swipRefresh;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |View.SYSTEM_UI_FLAG_LAYOUT_STABLE );
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }
        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        titleCity = (TextView)findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);

        swipRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipRefresh.setColorSchemeColors(R.color.design_default_color_primary);;

        backButton = (Button)findViewById(R.id.weather_back_button);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString(weatherKey, null);
        final String weatherId = getIntent().getStringExtra("weather_id");;
        if (weatherString != null){
            Weather weather = Utility.handleWeatherResponse(weatherString);
            assert weather != null;
            if (Objects.equals(weatherId, weather.basic.weatherId)){
                showWeatherInfo(weather);
            }
            else {
                weatherLayout.setVisibility(View.INVISIBLE);
                requestWeather(weatherId);

            }
        }else {
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        swipRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

        backButton.setOnClickListener(v -> finish());

        String bingPic = prefs.getString("bing_pic", null);
        if(bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }
        else {
            getImageInfo();
        }

    }

    public void  showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast :weather.forecastList){
            //动态加载布局
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText  = (TextView) view.findViewById(R.id.info_text);
            TextView maxText  = (TextView) view.findViewById(R.id.max_text);
            TextView minText  = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);

            forecastLayout.addView(view);
        }
        if (weather.aqi != null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数："+weather.suggestion.carWash.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;

        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);

        weatherLayout.setVisibility(View.VISIBLE);

    }


    public void requestWeather(final String weatherId){
        String weahterUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=86f7ad696c02457aa6a9dec556da6a79";
        HttpUtil.sendOKHttpReuqest(weahterUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString(weatherKey,responseText);
                            editor.apply();
                            showWeatherInfo(weather);

                            Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
                            startService(intent);


                        }
                        else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipRefresh.setRefreshing(false);
                    }
                });
            }
        });
        getImageInfo();
    }

    private void getImageInfo(){
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
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(() -> Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg));
    }
}