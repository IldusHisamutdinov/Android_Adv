package com.example.menu;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class WeatherService extends IntentService {
    static final String EXTRA_RESULT = "com.example.menu.temp.RESULT";

    public WeatherService() {
        super("TempWeather");
    }

    public static void startWeatherService(Context context) {
        Intent intent = new Intent(context, WeatherService.class);
        context.startService(intent);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        long result = 2;
        sendBrodcast(result);
    }

    //отправить уведомление о завершении сервиса
    private void sendBrodcast(long result) {
        Intent broadcastIntent = new Intent(MainActivity.BROADCAST_ACTION_WEATHERFINISHED);
        broadcastIntent.putExtra(EXTRA_RESULT, result);
        sendBroadcast(broadcastIntent);
    }
}
