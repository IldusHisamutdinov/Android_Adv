package com.example.menu;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class BatteryReceiver extends BroadcastReceiver {
    private final static String BATTERY_LEVEL = "level";
    private BatteryReceiver batteryResiver;
    private int messageId = 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra(BATTERY_LEVEL, 0);

        String message = intent.getStringExtra(BATTERY_LEVEL);
        if (level < 20){
            message = "Battery Low";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "4")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Battery Receiver")
                .setContentText(message);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(messageId++, builder.build());
    }
}
