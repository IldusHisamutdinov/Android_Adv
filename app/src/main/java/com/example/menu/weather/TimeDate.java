package com.example.menu.weather;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class TimeDate {

    // установим дату
    public static String dateNow() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM, H:mm");
        return sdf.format(calendar.getTime());
    }


    static final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("H:mm", Locale.ENGLISH)
            .withZone(ZoneId.systemDefault());

    public static String formatTime(Instant time) {
        return formatter.format(time);
    }

}
