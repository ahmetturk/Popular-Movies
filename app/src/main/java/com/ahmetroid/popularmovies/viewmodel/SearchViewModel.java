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

public class SearchViewModel extends BaseMoviesViewModel {

    private ApiClient apiClient;
    private String searchString;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        apiClient = ServiceGenerator.createService(ApiClient.class);
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    @Override
    public void loadMovies(int page) {
        if (status == null) {
            status = new MutableLiveData<>();
        }
        status.setValue(Codes.LOADING);

        Call<ApiResponse<Movie>> call = apiClient.getSearchMovies(
                getApplication().getString(R.string.language), String.valueOf(page), searchString);

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
