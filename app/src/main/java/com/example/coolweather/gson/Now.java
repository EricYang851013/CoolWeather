package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {

    public class More {
        @SerializedName("txt")
        public String info;
    }

    @SerializedName("tmp")
    public  String temperature;

    @SerializedName("cond")
    public More more;


}
