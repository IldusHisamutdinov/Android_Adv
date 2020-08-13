package com.example.menu;

import java.text.SimpleDateFormat;
import java.util.Calendar;

 class TimeDate {

    public static String g() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return sdf.format(calendar.getTime());

    }
}