package com.example.menu;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class WeatherService extends IntentService {
    static final String EXTRA_RESULT = "com.example.menu.temp.RESULT";
//    private TextView tempservice;
    private TextView tempservice2;
    private Handler handler = new Handler();
    Long temp; // температура

    public WeatherService() {
        super("TempWeather");
    }

    public static void startWeatherService(Context context) {
        Intent intent = new Intent(context, WeatherService.class);
        context.startService(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onHandleIntent(Intent intent) {
       long result = initHttp();

        sendBrodcast(result);
    }

    //отправить уведомление о завершении сервиса
    private void sendBrodcast(long result) {
        Intent broadcastIntent = new Intent(MainActivity.BROADCAST_ACTION_WEATHERFINISHED);
        broadcastIntent.putExtra(EXTRA_RESULT, result);
        sendBroadcast(broadcastIntent);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Long initHttp() {
        HttpsURLConnection urlConnection = null;

        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?id=479561&units=metric&appid=0ecf8658c4caf135dd4f087798c91ffb");
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);

            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String result = in.lines().collect(Collectors.joining());
            Gson gson = new Gson();
            ResponseWeather resultWeather = gson.fromJson(result, ResponseWeather.class);
            temp = resultWeather.getMain().getTemp();

            return temp;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }


}
