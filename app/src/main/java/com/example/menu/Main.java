package com.example.menu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Main {

    @SerializedName("temp")
    @Expose
    public Double temp;

 //   public Double getTemp() {
//        return temp;
 //   }

    public long getTemp() {
        double doubleValue = temp;
        long temp1 = (long) doubleValue;
        return temp1;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }
}
