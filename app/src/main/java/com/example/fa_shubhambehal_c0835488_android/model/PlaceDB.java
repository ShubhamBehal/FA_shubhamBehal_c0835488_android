package com.example.fa_shubhambehal_c0835488_android.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {PlaceInfo.class},
        version = 1)
public abstract class PlaceDB extends RoomDatabase {
    private static volatile PlaceDB instance;

    public static synchronized PlaceDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    PlaceDB.class, "place_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract PlaceDao placeDao();
}