package com.example.bhati.moviehub;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ResultDao {
    @Query("SELECT * FROM movies")
    LiveData<List<Result>> loadAllMovies();

    @Insert
    void insertMovie(Result result);

    @Delete
    void deleteMovie(Result result);

    @Query("SELECT * FROM movies WHERE id = :id")
    LiveData<Result> loadMovieById(int id);
}
