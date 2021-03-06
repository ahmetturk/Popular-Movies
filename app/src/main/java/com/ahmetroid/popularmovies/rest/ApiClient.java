package com.ahmetroid.popularmovies.rest;

import com.ahmetroid.popularmovies.model.ApiResponse;
import com.ahmetroid.popularmovies.model.Movie;
import com.ahmetroid.popularmovies.model.MovieDetail;
import com.ahmetroid.popularmovies.model.Review;
import com.ahmetroid.popularmovies.model.Video;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiClient {
    @GET("movie/top_rated")
    Call<ApiResponse<Movie>> getTopRatedMovies(@Query("language") String language,
                                               @Query("page") String page);

    @GET("movie/popular")
    Call<ApiResponse<Movie>> getPopularMovies(@Query("language") String language,
                                              @Query("page") String page);

    @GET("movie/now_playing")
    Call<ApiResponse<Movie>> getNowPlayingMovies(@Query("language") String language,
                                                 @Query("page") String page);

    @GET("movie/upcoming")
    Call<ApiResponse<Movie>> getUpcomingMovies(@Query("language") String language,
                                               @Query("page") String page);

    @GET("discover/movie")
    Call<ApiResponse<Movie>> getGenreMovies(@Query("language") String language,
                                            @Query("page") String page,
                                            @Query("with_genres") String with_genres);

    @GET("search/movie")
    Call<ApiResponse<Movie>> getSearchMovies(@Query("language") String language,
                                             @Query("page") String page,
                                             @Query("query") String query);

    @GET("movie/{id}/reviews")
    Call<ApiResponse<Review>> getReviews(@Path("id") String id);

    @GET("movie/{id}/videos")
    Call<ApiResponse<Video>> getVideos(@Path("id") String id);

    @GET("movie/{id}")
    Call<MovieDetail> getMovieById(@Path("id") String id);
}