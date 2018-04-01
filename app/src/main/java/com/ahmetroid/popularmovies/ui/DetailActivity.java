package com.ahmetroid.popularmovies.ui;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ahmetroid.popularmovies.R;
import com.ahmetroid.popularmovies.adapter.ReviewAdapter;
import com.ahmetroid.popularmovies.adapter.VideoAdapter;
import com.ahmetroid.popularmovies.data.AppDatabase;
import com.ahmetroid.popularmovies.data.AppPreferences;
import com.ahmetroid.popularmovies.databinding.ActivityDetailBinding;
import com.ahmetroid.popularmovies.model.ApiResponse;
import com.ahmetroid.popularmovies.model.MiniMovie;
import com.ahmetroid.popularmovies.model.Movie;
import com.ahmetroid.popularmovies.model.MovieDetail;
import com.ahmetroid.popularmovies.model.Review;
import com.ahmetroid.popularmovies.model.Video;
import com.ahmetroid.popularmovies.rest.ApiClient;
import com.ahmetroid.popularmovies.rest.ServiceGenerator;
import com.ahmetroid.popularmovies.utils.Codes;
import com.ahmetroid.popularmovies.utils.HorizontalItemDecoration;
import com.ahmetroid.popularmovies.utils.MyExecutor;
import com.google.android.gms.ads.AdRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ahmetroid.popularmovies.utils.Codes.getSortingName;

public class DetailActivity extends AppCompatActivity {
    public static final String DETAIL_INTENT_KEY = "com.example.ahmet.popularmovies.ui.detail";
    public static final String MOVIE_NUMBER_KEY = "com.example.ahmet.popularmovies.ui.movie_number";
    public static final String SORTING_KEY = "com.example.ahmet.popularmovies.ui.sorting";

    private static final String BUNDLE_VIDEOS = "videos";
    private static final String BUNDLE_REVIEWS = "reviews";

    private ActivityDetailBinding mBinding;
    private AppDatabase mDatabase;
    private boolean isFavorite;
    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;
    private Movie movie;
    private ApiClient mApiClient;
    private Executor executor;
    private int movieNumber;
    private int mSorting;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mBinding.adView.loadAd(adRequest);
        mBinding.adViewMedium.loadAd(adRequest);

        // Making Collapsing Toolbar Width / Height Ratio = 3 / 2
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = displaymetrics.widthPixels;
            mBinding.collapsingToolbar.getLayoutParams().height = (int) Math.round(width / 1.5);
        }
        // END OF Making Collapsing Toolbar Width / Height Ratio = 3 / 2

        mDatabase = AppDatabase.getDatabase(this);

        mApiClient = ServiceGenerator.createService(ApiClient.class);
        executor = new MyExecutor();

        Intent intent = getIntent();
        movieNumber = intent.getIntExtra(MOVIE_NUMBER_KEY, -1);
        mSorting = intent.getIntExtra(SORTING_KEY, -1);
        movie = intent.getParcelableExtra(DETAIL_INTENT_KEY);

        Bundle payload = new Bundle();
        payload.putString(FirebaseAnalytics.Param.ITEM_ID, movie.movieId);
        payload.putString(FirebaseAnalytics.Param.ITEM_NAME, movie.originalTitle);
        payload.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, getSortingName(this, mSorting));
        mFirebaseAnalytics.
                logEvent(FirebaseAnalytics.Event.VIEW_ITEM, payload);

        mBinding.setMovie(movie);
        mBinding.setPresenter(this);

        setSupportActionBar(mBinding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        populateUI();
        populateVideos(savedInstanceState);
        populateReviews(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BUNDLE_VIDEOS, mVideoAdapter.getList());
        outState.putParcelableArrayList(BUNDLE_REVIEWS, mReviewAdapter.getList());
    }

    /**
     * populates UI of Detail Activity except Videos and Reviews
     */
    private void populateUI() {

        Picasso.get()
                .load(Codes.BACKDROP_URL + movie.backdropPath)
                .error(R.drawable.error)
                .into(mBinding.backdrop);

        Picasso.get()
                .load(Codes.POSTER_URL + movie.posterPath)
                .error(R.drawable.error)
                .into(mBinding.movieDetails.poster);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                MiniMovie miniMovie = mDatabase.movieDao().getMovieById(movie.movieId);

                if (miniMovie != null) {
                    isFavorite = true;
                    mBinding.favoriteButton.setImageResource(R.drawable.ic_star_white_24px);
                } else {
                    isFavorite = false;
                    mBinding.favoriteButton.setImageResource(R.drawable.ic_star_border_white_24px);
                }
            }
        });
    }

    /**
     * fetch videos and populate their views
     */
    private void populateVideos(Bundle savedInstanceState) {
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mBinding.movieVideos.videosList.setLayoutManager(layoutManager);
        mBinding.movieVideos.videosList.setHasFixedSize(true);
        mBinding.movieVideos.videosList.setNestedScrollingEnabled(false);

        RecyclerView.ItemDecoration itemDecoration = new HorizontalItemDecoration(this);
        mBinding.movieVideos.videosList.addItemDecoration(itemDecoration);

        mVideoAdapter = new VideoAdapter(this);
        mBinding.movieVideos.videosList.setAdapter(mVideoAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_VIDEOS)) {
            mVideoAdapter.addVideosList(savedInstanceState.
                    <Video>getParcelableArrayList(BUNDLE_VIDEOS));
            if (mVideoAdapter.getItemCount() == 0) {
                mBinding.movieVideos.videosLabel.setVisibility(View.GONE);
            }
        } else {
            Call<ApiResponse<Video>> call = mApiClient.getVideos(movie.movieId);

            call.enqueue(new Callback<ApiResponse<Video>>() {
                @Override
                public void onResponse(Call<ApiResponse<Video>> call,
                                       Response<ApiResponse<Video>> response) {
                    List<Video> result = response.body().results;
                    mVideoAdapter.addVideosList(result);
                    if (result.size() == 0) {
                        mBinding.movieVideos.videosLabel.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Video>> call, Throwable t) {
                    Toast.makeText(DetailActivity.this,
                            getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * fetch reviews and populate their views
     */
    private void populateReviews(Bundle savedInstanceState) {
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mBinding.movieReviews.reviewsList.setLayoutManager(layoutManager);
        mBinding.movieReviews.reviewsList.setHasFixedSize(true);
        mBinding.movieReviews.reviewsList.setNestedScrollingEnabled(false);

        RecyclerView.ItemDecoration itemDecoration = new HorizontalItemDecoration(this);
        mBinding.movieReviews.reviewsList.addItemDecoration(itemDecoration);

        mReviewAdapter = new ReviewAdapter(this);
        mBinding.movieReviews.reviewsList.setAdapter(mReviewAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_REVIEWS)) {
            mReviewAdapter.addReviewsList(savedInstanceState.
                    <Review>getParcelableArrayList(BUNDLE_REVIEWS));
            if (mReviewAdapter.getItemCount() == 0) {
                mBinding.movieReviews.reviewsLabel.setVisibility(View.GONE);
            }
        } else {
            Call<ApiResponse<Review>> call = mApiClient.getReviews(movie.movieId);

            call.enqueue(new Callback<ApiResponse<Review>>() {
                @Override
                public void onResponse(Call<ApiResponse<Review>> call,
                                       Response<ApiResponse<Review>> response) {
                    List<Review> result = response.body().results;
                    mReviewAdapter.addReviewsList(result);
                    if (result.size() == 0) {
                        mBinding.movieReviews.reviewsLabel.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Review>> call, Throwable t) {
                    Toast.makeText(DetailActivity.this,
                            getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * adds the movie to favorite or remove it if it already exists
     * adding favorite means adds it to sql database
     */
    public void onClickFavoriteButton() {
        AppPreferences.setChangedMovie(this, movieNumber);

        String snackBarText;
        if (isFavorite) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    mDatabase.movieDao().delete(movie);
                }
            });
            isFavorite = false;
            mBinding.favoriteButton.setImageResource(R.drawable.ic_star_border_white_24px);
            snackBarText = getString(R.string.remove_favorite);
        } else {
            Bundle payload = new Bundle();
            payload.putString(FirebaseAnalytics.Param.ITEM_ID, movie.movieId);
            payload.putString(FirebaseAnalytics.Param.ITEM_NAME, movie.originalTitle);
            payload.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, getSortingName(this, mSorting));
            mFirebaseAnalytics.
                    logEvent("add_to_favorites", payload);

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    mDatabase.movieDao().insert(movie);
                }
            });
            isFavorite = true;
            mBinding.favoriteButton.setImageResource(R.drawable.ic_star_white_24px);
            snackBarText = getString(R.string.add_favorite);
        }
        Snackbar.make(mBinding.coordinatorLayout, snackBarText, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                String shareText = "themoviedb.org/movie/" + movie.movieId + " download Cinemate onelink.to/zxmp8t";
                ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(this)
                        .setText(shareText)
                        .setType("text/plain");

                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "movie");
                payload.putString(FirebaseAnalytics.Param.ITEM_ID, movie.movieId);
                payload.putString(FirebaseAnalytics.Param.METHOD, "movie detail share");
                mFirebaseAnalytics.
                        logEvent(FirebaseAnalytics.Event.SHARE, payload);

                try {
                    intentBuilder.startChooser();
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, R.string.no_app, Toast.LENGTH_LONG).show();
                }
                return true;
            case android.R.id.home:
                mBinding.favoriteButton.setVisibility(View.INVISIBLE);
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mBinding.favoriteButton.setVisibility(View.INVISIBLE);
        super.onBackPressed();
    }

    public String formatReleaseDate(String releaseDate) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = sdf.parse(releaseDate);
        } catch (ParseException e) {
            return releaseDate;
        }

        return DateFormat.getDateInstance(DateFormat.LONG).format(date);
    }

    public String getEnglishPlotSynopsis(String id) {
        Call<MovieDetail> call = mApiClient.getMovieById(id);

        call.enqueue(new Callback<MovieDetail>() {
            @Override
            public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                try {
                    String plotSynopsis = response.body().plotSynopsis;
                    mBinding.movieDetails.plotSynopsisTv.setText(plotSynopsis);
                } catch (NullPointerException e) {
                    Toast.makeText(DetailActivity.this,
                            getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MovieDetail> call, Throwable t) {
                Toast.makeText(DetailActivity.this,
                        getString(R.string.connection_error), Toast.LENGTH_LONG).show();
            }
        });

        return "";
    }

    public void onClickExpand(View view, Review review) {
        Bundle payload = new Bundle();
        payload.putString(FirebaseAnalytics.Param.ITEM_ID, movie.movieId);
        payload.putString(FirebaseAnalytics.Param.ITEM_NAME, movie.originalTitle);
        payload.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, getSortingName(this, mSorting));
        mFirebaseAnalytics.
                logEvent("review_expand", payload);

        Intent intent = new Intent(this, ReviewActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this,
                        view,
                        ViewCompat.getTransitionName(view));
        intent.putExtra(ReviewActivity.REVIEW_INTENT_KEY, review);
        intent.putExtra(ReviewActivity.MOVIE_TITLE_KEY, movie.originalTitle);
        startActivity(intent, options.toBundle());
    }

    public void onClickVideo(String videoUrl) {
        Bundle payload = new Bundle();
        payload.putString(FirebaseAnalytics.Param.ITEM_ID, movie.movieId);
        payload.putString(FirebaseAnalytics.Param.ITEM_NAME, movie.originalTitle);
        payload.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, getSortingName(this, mSorting));
        mFirebaseAnalytics.
                logEvent("video_click", payload);

        Intent appIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("vnd.youtube:" + videoUrl));

        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/watch?v=" + videoUrl));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }
}