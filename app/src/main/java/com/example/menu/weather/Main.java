package com.example.menu.weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Main {

    @SerializedName("temp")
    @Expose
    public Double temp;
    @SerializedName("pressure")
    @Expose
    private Integer pressure;
    @SerializedName("humidity")
    @Expose
    private Integer humidity;

    public long getTemp() {
        double doubleValue = temp;
        long temp1 = (long) doubleValue;
        return temp1;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }


    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Integer getPressure() {
        int presMm = pressure;
        pressure = Math.abs((int) (presMm / 1.33));
        return pressure;
    }

    public void setPressure(Integer pressure) {
        this.pressure = pressure;
    }

}
