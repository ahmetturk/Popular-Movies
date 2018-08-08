package com.ahmetroid.popularmovies.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ahmetroid.popularmovies.R;
import com.ahmetroid.popularmovies.model.ApiResponse;
import com.ahmetroid.popularmovies.model.Movie;
import com.ahmetroid.popularmovies.rest.ApiClient;
import com.ahmetroid.popularmovies.rest.ServiceGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class SinemateRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private List<String> movies;
    private final ApiClient apiClient;

    public SinemateRemoteViewsFactory(Context context) {
        this.context = context;
        apiClient = ServiceGenerator.createService(ApiClient.class);
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        Call<ApiResponse<Movie>> call = apiClient
                .getPopularMovies(context.getString(R.string.language), "1");


        try {
            Response<ApiResponse<Movie>> response = call.execute();
            if (response.isSuccessful()) {
                movies = new ArrayList<>();

                List<Movie> result = response.body().results;
                for (Movie movie : result) {
                    movies.add(movie.movieTitle);
                }
            }

        } catch (IOException e) {
            Log.e(SinemateRemoteViewsFactory.class.getSimpleName(), e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        if (movies == null) {
            return 0;
        }
        return movies.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION || movies == null) {
            return null;
        }

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
        rv.setTextViewText(R.id.widget_item_textview, movies.get(position));

        Intent fillInIntent = new Intent();
        rv.setOnClickFillInIntent(R.id.widget_item_textview, fillInIntent);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
