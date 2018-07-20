package com.example.bhati.moviehub;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<Result>> results;

    public MainViewModel(@NonNull Application application) {
        super(application);
        results = AppDatabase.getInstance(this.getApplication()).resultDao().loadAllMovies();
    }

    public LiveData<List<Result>> getResults() {
        return results;
    }
}
