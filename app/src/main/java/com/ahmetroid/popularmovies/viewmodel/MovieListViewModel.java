package com.ahmetroid.popularmovies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.ahmetroid.popularmovies.R;
import com.ahmetroid.popularmovies.data.AppDatabase;
import com.ahmetroid.popularmovies.model.ApiResponse;
import com.ahmetroid.popularmovies.model.Movie;
import com.ahmetroid.popularmovies.rest.ApiClient;
import com.ahmetroid.popularmovies.rest.ServiceGenerator;
import com.ahmetroid.popularmovies.utils.Codes;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListViewModel extends AndroidViewModel {
    private MutableLiveData<List<Movie>> movieList;
    private MutableLiveData<Integer> status;
    private AppDatabase database;
    private ApiClient apiClient;
    private LiveData<List<Movie>> favoriteList;

    public MovieListViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getDatabase(getApplication());
        apiClient = ServiceGenerator.createService(ApiClient.class);
    }

    public LiveData<List<Movie>> getMovieList(int sortingCode) {
        if (movieList == null) {
            movieList = new MutableLiveData<>();
            loadMovies(sortingCode, 1);
        }
        return movieList;
    }

    public LiveData<List<Movie>> getFavoriteList() {
        if (favoriteList == null) {
            favoriteList = new MutableLiveData<>();
            favoriteList = database.movieDao().getAll();
        }
        return favoriteList;
    }

    public MutableLiveData<Integer> getStatus() {
        if (status == null) {
            status = new MutableLiveData<>();
            status.setValue(Codes.LOADING);
        }
        return status;
    }

    public void loadMovies(int sortingCode, int page) {
        if (status == null) {
            status = new MutableLiveData<>();
        }
        status.setValue(Codes.LOADING);
        Call<ApiResponse<Movie>> call;

        if (sortingCode < 4) {
            switch (sortingCode) {
                case Codes.POPULAR:
                    call = apiClient.getPopularMovies(getApplication().getString(R.string.language),
                            String.valueOf(page));
                    break;
                case Codes.TOP_RATED:
                    call = apiClient.getTopRatedMovies(getApplication().getString(R.string.language),
                            String.valueOf(page));
                    break;
                case Codes.NOW_PLAYING:
                    call = apiClient.getNowPlayingMovies(getApplication().getString(R.string.language),
                            String.valueOf(page));
                    break;
                case Codes.UPCOMING:
                default:
                    call = apiClient.getUpcomingMovies(getApplication().getString(R.string.language),
                            String.valueOf(page));
                    break;
            }
        } else {
            call = apiClient.getGenreMovies(getApplication().getString(R.string.language),
                    String.valueOf(page), Codes.getGenreCode(sortingCode));
        }

        call.enqueue(new Callback<ApiResponse<Movie>>() {
            @Override
            public void onResponse(Call<ApiResponse<Movie>> call,
                                   Response<ApiResponse<Movie>> response) {
                if (response.isSuccessful()) {
                    List<Movie> result = response.body().results;
                    List<Movie> value = movieList.getValue();
                    if (value == null || value.isEmpty()) {
                        movieList.setValue(result);
                    } else {
                        value.addAll(result);
                        movieList.setValue(value);
                    }
                    status.setValue(Codes.SUCCESS);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Movie>> call, Throwable t) {
                movieList = null;
                status.setValue(Codes.ERROR);
            }
        });
    }

}
