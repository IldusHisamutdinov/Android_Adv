package com.example.menu.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.menu.dao.AccessToData;
import com.example.menu.model.City;


@Database(entities = {City.class}, version = 1)
public abstract class CityDatabase extends RoomDatabase {
    public abstract AccessToData getAccessToData();
}


