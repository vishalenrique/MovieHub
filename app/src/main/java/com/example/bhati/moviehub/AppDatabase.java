package com.example.bhati.moviehub;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

@Database(entities = Result.class,version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String TAG = "AppDatabase";
    private static final Object LOCK=new Object();
    private static final String DATABASE_NAME="moviehub";
    private static AppDatabase sDatabase;

    public static AppDatabase getInstance(Context context){
        if(sDatabase == null){
            synchronized (LOCK){
                Log.d(TAG,"creating new database");
                sDatabase = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class,
                        AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(TAG,"returning new database");
        return sDatabase;
    }
    public abstract ResultDao resultDao();
}
