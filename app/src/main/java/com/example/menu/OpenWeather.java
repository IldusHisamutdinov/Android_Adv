package com.example.menu;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeather {
    @GET("data/2.5/weather")
    Call<ResponseWeather> loadWeather(@Query("q") String cityCountry, @Query("units") String metric, @Query("appid") String keyApi);
}

