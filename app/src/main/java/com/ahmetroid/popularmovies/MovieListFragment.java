package com.ahmetroid.popularmovies;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ahmetroid.popularmovies.adapter.MovieAdapter;
import com.ahmetroid.popularmovies.data.AppDatabase;
import com.ahmetroid.popularmovies.data.PopMovDatabase;
import com.ahmetroid.popularmovies.databinding.FragmentMovieListBinding;
import com.ahmetroid.popularmovies.model.ApiResponse;
import com.ahmetroid.popularmovies.model.Movie;
import com.ahmetroid.popularmovies.rest.ApiClient;
import com.ahmetroid.popularmovies.rest.ServiceGenerator;
import com.ahmetroid.popularmovies.utils.Codes;
import com.ahmetroid.popularmovies.utils.GridItemDecoration;
import com.ahmetroid.popularmovies.utils.MyExecutor;
import com.ahmetroid.popularmovies.utils.RecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieListFragment extends Fragment implements MovieAdapter.ListenerMovieAdapter {

    public static final String SORTING_CODE = "sorting_code";

    private static final String BUNDLE_MOVIES = "movies";
    private static final String BUNDLE_PAGE = "page";
    private static final String BUNDLE_COUNT = "count";
    private static final String BUNDLE_RECYCLER = "recycler";

    private FragmentMovieListBinding mBinding;
    private AppDatabase mDatabase;
    private MovieAdapter mMoviesAdapter;
    private RecyclerViewScrollListener mScrollListener;
    private GridLayoutManager mGridLayoutManager;
    private ApiClient mApiClient;
    private Executor executor;
    private int mSorting;
    private Bundle mSavedInstanceState;

    public MovieListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mSavedInstanceState = savedInstanceState;

        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_movie_list, container, false);
        mBinding.setPresenter(this);
        mDatabase = PopMovDatabase.getInstance(getContext());

        mSorting = getArguments().getInt(SORTING_CODE, 0);

        mApiClient = ServiceGenerator.createService(ApiClient.class);

        executor = new MyExecutor();

        mGridLayoutManager = new GridLayoutManager(getContext(), numberOfColumns());
        mBinding.moviesList.setLayoutManager(mGridLayoutManager);

        mBinding.moviesList.addItemDecoration(new GridItemDecoration(getContext()));

        mMoviesAdapter = new MovieAdapter(getActivity(), this, mDatabase, mSorting);
        mBinding.moviesList.setAdapter(mMoviesAdapter);

        mScrollListener = new RecyclerViewScrollListener(mGridLayoutManager) {
            @Override
            public void onLoadMore(int page) {
                fetchNewMovies(page, mSorting);
            }
        };

        mBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateUI();
            }
        });

        populateUI();

        return mBinding.getRoot();
    }


    /**
     * Main reason is refresh the movie's favorite icon if it is selected in Detail Activity
     */
    @Override
    public void onResume() {
        super.onResume();
        mMoviesAdapter.refreshFavorite();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(BUNDLE_MOVIES, mMoviesAdapter.getList());
        outState.putInt(BUNDLE_PAGE, mScrollListener.getPage());
        outState.putInt(BUNDLE_COUNT, mScrollListener.getCount());
        outState.putParcelable(BUNDLE_RECYCLER, mGridLayoutManager.onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    /**
     * populates UI,
     * first,
     * it gets the selected sorting method from PopMovPreferences
     * if it is favorites,
     * it starts Cursor Loader
     * else,
     * it gets saved data or calls fetchNewMovies
     */
    private void populateUI() {
        mMoviesAdapter.clearMoviesList();
        if (mSorting == Codes.FAVORITES) {
            // FAVORITES SELECTED
            mBinding.moviesList.clearOnScrollListeners();
            mBinding.swipeRefreshLayout.setEnabled(false);

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    final List<Movie> movies = mDatabase.movieDao().getAll();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mMoviesAdapter.addMoviesList(movies);
                            if (mMoviesAdapter.getItemCount() == 0) {
                                showNoFavoriteStatus();
                            } else {
                                hideStatus();
                            }
                        }
                    });
                }

            });

        } else {
            if (mSavedInstanceState != null) {
                // NOT FAVORITES SELECTED BUT THERE IS SAVED DATA
                mScrollListener.setState(
                        mSavedInstanceState.getInt(BUNDLE_PAGE),
                        mSavedInstanceState.getInt(BUNDLE_COUNT));

                ArrayList<Movie> list = mSavedInstanceState.getParcelableArrayList(BUNDLE_MOVIES);
                if ((list == null || list.isEmpty()) && !isOnline()) {
                    showNoInternetStatus();
                }
                mMoviesAdapter.addMoviesList(list);
                mGridLayoutManager
                        .onRestoreInstanceState(mSavedInstanceState.getParcelable(BUNDLE_RECYCLER));
                mBinding.swipeRefreshLayout.setRefreshing(false);
            } else {
                // NOT FAVORITES SELECTED AND THERE IS NOT SAVED DATA
                mScrollListener.resetState();
                fetchNewMovies(1, mSorting);
            }
            mBinding.moviesList.addOnScrollListener(mScrollListener);
        }
    }


    /**
     * fetch movies from the TheMovieDB API
     *
     * @param page    the number of the page that will be fetched from API
     * @param sortingCode selected sortingCode method by user
     *                0 = most popular
     *                1 = highest rated
     */
    private void fetchNewMovies(int page, int sortingCode) {
        Call<ApiResponse<Movie>> call;

        if (sortingCode < 4) {
            switch (sortingCode) {
                case Codes.POPULAR:
                    call = mApiClient.getPopularMovies(getString(R.string.language),
                            String.valueOf(page));
                    break;
                case Codes.TOP_RATED:
                    call = mApiClient.getTopRatedMovies(getString(R.string.language),
                            String.valueOf(page));
                    break;
                case Codes.NOW_PLAYING:
                    call = mApiClient.getNowPlayingMovies(getString(R.string.language),
                            String.valueOf(page));
                    break;
                case Codes.UPCOMING:
                default:
                    call = mApiClient.getUpcomingMovies(getString(R.string.language),
                            String.valueOf(page));
                    break;
            }
        } else {
            call = mApiClient.getGenreMovies(getString(R.string.language),
                    String.valueOf(page), Codes.getGenreCode(sortingCode));
        }

        if (!isOnline()) {
            showNoInternetStatus();
        }

        call.enqueue(new Callback<ApiResponse<Movie>>() {
            @Override
            public void onResponse(Call<ApiResponse<Movie>> call, Response<ApiResponse<Movie>> response) {
                try {
                    List<Movie> result = response.body().getResults();
                    if (result != null) {
                        hideStatus();
                        mBinding.swipeRefreshLayout.setEnabled(false);
                        mMoviesAdapter.addMoviesList(result);
                    }
                } catch (NullPointerException e) {
                    Toast.makeText(getContext(),
                            getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                } finally {
                    mBinding.swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Movie>> call, Throwable t) {
                Toast.makeText(getContext(),
                        getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                mBinding.swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * calculates number of columns
     *
     * @return the number of columns in the grid layout of main activity
     */
    private int numberOfColumns() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        if (getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
            if (width > 1000) {
                return 3;
            } else {
                return 2;
            }
        } else {
            if (width > 1700) {
                return 5;
            } else if (width > 1200) {
                return 4;
            } else {
                return 3;
            }
        }
    }

    /**
     * get the internet connection status
     *
     * @return true if the device has internet connection, false otherwise
     */
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
        return false;
    }

    /**
     * shows no internet text view and enables swipe refresh
     */
    private void showNoInternetStatus() {
        mBinding.statusImage.setImageResource(R.drawable.ic_signal_wifi_off_white_24px);
        mBinding.statusImage.setVisibility(View.VISIBLE);
        mBinding.statusText.setText(R.string.no_internet);
        mBinding.statusText.setVisibility(View.VISIBLE);
        mBinding.swipeRefreshLayout.setEnabled(true);
    }

    /**
     * shows no favorite text view
     */
    private void showNoFavoriteStatus() {
        mBinding.statusImage.setImageResource(R.drawable.ic_star_border_white_24px);
        mBinding.statusImage.setVisibility(View.VISIBLE);
        mBinding.statusText.setText(R.string.no_favorite);
        mBinding.statusText.setVisibility(View.VISIBLE);
    }

    /**
     * hides status text view
     */
    private void hideStatus() {
        mBinding.statusImage.setVisibility(View.INVISIBLE);
        mBinding.statusText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onEmpty() {
        showNoFavoriteStatus();
    }

    public void notThisStar() {
        Snackbar.make(mBinding.mainLayout, getString(R.string.not_this_star), Snackbar.LENGTH_LONG).show();
    }
}