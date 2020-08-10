package com.example.menu;

import android.app.Application;

import androidx.room.Room;

import com.example.menu.dao.AccessToData;
import com.example.menu.database.CityDatabase;

// Паттерн Singleton, наследуем класс Application, создаём базу данных
// в методе onCreate
public class App extends Application {

    private static App instance;

    // База данных
    private CityDatabase db;

    // Получаем объект приложения
    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Сохраняем объект приложения (для Singleton’а)
        instance = this;

        // Строим базу
        db = Room.databaseBuilder(
                getApplicationContext(),
                CityDatabase.class,
                "city_database")
                .allowMainThreadQueries() //Только для примеров и тестирования
                .build();
    }

    // Получаем EducationDao для составления запросов
    public AccessToData getAccessToData() {
        return db.getAccessToData();
    }
}
