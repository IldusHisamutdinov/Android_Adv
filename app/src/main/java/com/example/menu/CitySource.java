package com.example.menu;

import com.example.menu.dao.AccessToData;
import com.example.menu.model.City;

import java.util.List;

public class CitySource {

    private final AccessToData accessToData;

    // Буфер с данными: сюда будем подкачивать данные из БД
    private List<City> cities;

    public CitySource(AccessToData accessToData){
        this.accessToData = accessToData;
    }

    // Получить всех студентов
    public List<City> getCities(){
        // Если объекты еще не загружены, загружаем их.
        // Это сделано для того, чтобы не делать запросы к БД каждый раз
        if (cities == null){
            LoadStudents();
        }
        return cities;
    }

    // Загружаем студентов в буфер
    public void LoadStudents(){
        cities = accessToData.getAllCities();
    }

    // Получаем количество записей
    public long getCountStudents(){
        return accessToData.getCountCities();
    }

    // Добавляем студента
    public void addStudent(City city){
        accessToData.insertCity(city);
        // После изменения БД надо повторно прочесть данные из буфера
        LoadStudents();
    }

    // Заменяем студента
    public void updateStudent(City city){
        accessToData.updateCity(city);
        LoadStudents();
    }

    // Удаляем студента из базы
    public void removeStudent(long id){
        accessToData.deteleCitytById(id);
        LoadStudents();
    }

}
