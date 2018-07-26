package com.example.bhati.moviehub.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.bhati.moviehub.movieList.Result;

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
