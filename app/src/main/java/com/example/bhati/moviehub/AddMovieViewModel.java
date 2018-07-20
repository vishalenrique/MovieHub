package com.example.bhati.moviehub;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

class AddMovieViewModel extends ViewModel {
    private LiveData<Result> mResult;
    public AddMovieViewModel(AppDatabase database, Result result) {
        mResult = database.resultDao().loadMovieById(result.getId());
    }

    public LiveData<Result> getResult() {
        return mResult;
    }
}
