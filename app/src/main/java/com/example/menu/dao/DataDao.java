package com.example.menu.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.menu.model.DataModel;

import java.util.List;

@Dao
public interface DataDao {

    @Insert
    void insert(DataModel dataModel);

    @Delete
    void delete(DataModel dataModel);

    @Query("SELECT * FROM DataModel")
    List<DataModel> getAllData();

//    @Query("SELECT * FROM DataModel WHERE date = :date")
//    DataModel getByDate(@TypeConverters({DateConverter.class}) Date date);

    //пример запроса с выборкой
    @Query("SELECT * FROM DataModel WHERE city LIKE :title")
    List<DataModel> getByTitle(String title);
}