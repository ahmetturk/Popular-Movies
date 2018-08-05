package com.ahmetroid.popularmovies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.ahmetroid.popularmovies.R;
import com.ahmetroid.popularmovies.model.ApiResponse;
import com.ahmetroid.popularmovies.model.Movie;
import com.ahmetroid.popularmovies.rest.ApiClient;
import com.ahmetroid.popularmovies.rest.ServiceGenerator;
import com.ahmetroid.popularmovies.utils.Codes;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesViewModel extends BaseMoviesViewModel {

    private ApiClient apiClient;
    private int sortingCode;

    public MoviesViewModel(@NonNull Application application) {
        super(application);
        apiClient = ServiceGenerator.createService(ApiClient.class);
    }

    public void setSortingCode(int sortingCode) {
        this.sortingCode = sortingCode;
    }

    public void loadMovies(int page) {
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
                    List<Movie> value = movies.getValue();
                    if (value == null || value.isEmpty()) {
                        ((MutableLiveData) movies).setValue(result);
                    } else {
                        value.addAll(result);
                        ((MutableLiveData) movies).setValue(value);
                    }
                    status.setValue(Codes.SUCCESS);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Movie>> call, Throwable t) {
                movies = null;
                status.setValue(Codes.ERROR);
            }
        });
    }

}
