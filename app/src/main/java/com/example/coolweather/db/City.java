package com.example.coolweather.db;

import org.litepal.crud.DataSupport;
import com.google.gson.annotations.SerializedName;

public class City extends DataSupport {
    private transient int id;
    private String name;
    @SerializedName("id")  private int code;
    private int provinceId;

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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
