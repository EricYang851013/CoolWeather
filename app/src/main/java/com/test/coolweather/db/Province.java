package com.test.coolweather.db;

import org.litepal.crud.DataSupport;
import com.google.gson.annotations.SerializedName;

public class Province extends DataSupport {
    //
    private transient int id;
    @SerializedName("name")
    private String localName;
    @SerializedName("id")  private int code;

    public int getId() {
            return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String name) {
        this.localName = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
