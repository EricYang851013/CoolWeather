package com.test.coolweather.db;

import org.litepal.crud.DataSupport;
import com.google.gson.annotations.SerializedName;

public class County extends DataSupport {
    private transient int  id;
    private String name;
    @SerializedName("weather_id") private String weatherId;
    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
