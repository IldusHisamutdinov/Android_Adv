package com.example.menu.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class DataModel {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String city;
    public String temp;
    public String date;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }


    //    @NonNull
    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
