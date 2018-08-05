package com.ahmetroid.popularmovies.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.ahmetroid.popularmovies.data.AppDatabase;
import com.ahmetroid.popularmovies.utils.Status;

public class FavoritesViewModel extends BaseMoviesViewModel {

    private final AppDatabase database;

    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getDatabase(getApplication());
    }

    @Override
    public void loadMovies(int page) {
        movies = database.movieDao().getAll();
        status.setValue(Status.SUCCESS);
    }
}
