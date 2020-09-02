package com.example.menu;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeoFenceService extends IntentService {

    private int messageId = 0;

    public GeoFenceService() {
        super("GeoFenceService");
    }

    @Override
    public void onCreate() {
         super.onCreate();
    }

    public GeoFenceService(String name) {
        super(name);
          }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);   // получаем событие
        int transitionType = geofencingEvent.getGeofenceTransition();   // определяем тип события
        List<Geofence> triggeredGeofences = geofencingEvent.getTriggeringGeofences();    // если надо, получаем, какие геозоны нам подходят
        String idFence = triggeredGeofences.get(0).getRequestId();
        makeNote(idFence, transitionType); // отправляем уведомление
    }

    // вывод уведомления в строке состояния
    private void makeNote(String idFence, int transitionType){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "2")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Near " + idFence)
                .setContentText(getTransitionTypeString(transitionType));
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(messageId++, builder.build());
    }

    // возвращает строку с типом события
    private String getTransitionTypeString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "enter";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "exit";
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return "dwell";
            default:
                return "unknown";
        }
    }
}
