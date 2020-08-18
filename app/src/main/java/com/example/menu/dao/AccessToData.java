package com.example.menu.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.menu.model.City;

import java.util.List;
@Dao
public interface AccessToData {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCity(City city);

    // Метод для замены данных студента
    @Update
    void updateCity(City city);

    // Удаляем данные студента
    @Delete
    void deleteCity(City city);

    // Удаляем данные студента, зная ключ
    @Query("DELETE FROM city WHERE id = :id")
    void deteleCitytById(long id);

    // Забираем данные по всем студентам
    @Query("SELECT * FROM city")
    List<City> getAllCities();

    // Получаем данные одного студента по id
    @Query("SELECT * FROM city WHERE id = :id")
    City getCitytById(long id);

    //Получаем количество записей в таблице
    @Query("SELECT COUNT() FROM city")
    long getCountCities();

}
