package com.example.menu;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Main {

    @SerializedName("temp")
    @Expose
    public Double temp;


    public int getTemp() {
        double doubleValue = temp;
        int temp1 = (int) doubleValue;

        return temp1;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

}



