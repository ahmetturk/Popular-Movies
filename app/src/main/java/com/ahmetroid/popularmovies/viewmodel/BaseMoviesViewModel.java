package com.ahmetroid.popularmovies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.ahmetroid.popularmovies.model.Movie;
import com.ahmetroid.popularmovies.utils.Status;

import java.util.List;

public abstract class BaseMoviesViewModel extends AndroidViewModel {

    LiveData<List<Movie>> movies;
    MutableLiveData<Integer> status;

    BaseMoviesViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Movie>> getMovies() {
        if (movies == null) {
            movies = new MutableLiveData<>();
            loadMovies(1);
        }
        return movies;
    }

    public LiveData<Integer> getStatus() {
        if (status == null) {
            status = new MutableLiveData<>();
            status.setValue(Status.LOADING);
        }
        return status;
    }

    public abstract void loadMovies(int page);

}

