package com.example.menu.database;

import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.example.menu.dao.DataDao;
import com.example.menu.model.DataModel;


@Database(entities = {DataModel.class }, version = 1, exportSchema = false)
public abstract class DatabaseHelper extends RoomDatabase {
    public abstract DataDao getDataDao();
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }
}


