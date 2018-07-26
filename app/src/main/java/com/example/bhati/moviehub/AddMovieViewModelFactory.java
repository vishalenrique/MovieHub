package com.example.bhati.moviehub;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.bhati.moviehub.database.AppDatabase;
import com.example.bhati.moviehub.movieList.Result;

public class AddMovieViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private AppDatabase mDatabase;
    private Result mResult;


    public AddMovieViewModelFactory(AppDatabase database, Result result) {
        mDatabase = database;
        mResult = result;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddMovieViewModel(mDatabase,mResult);
    }
}
