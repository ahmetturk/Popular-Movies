package com.ahmetroid.popularmovies.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ahmetroid.popularmovies.R;
import com.ahmetroid.popularmovies.adapter.MovieAdapter;
import com.ahmetroid.popularmovies.databinding.FragmentMovieListBinding;
import com.ahmetroid.popularmovies.model.Movie;
import com.ahmetroid.popularmovies.utils.Codes;
import com.ahmetroid.popularmovies.utils.GridItemDecoration;
import com.ahmetroid.popularmovies.utils.RecyclerViewScrollListener;
import com.ahmetroid.popularmovies.viewmodel.MovieListViewModel;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static com.ahmetroid.popularmovies.utils.Codes.getSortingName;

public class MovieListFragment extends Fragment {

    public static final String SORTING_CODE = "sorting_code";

    private static final String BUNDLE_PAGE = "page";
    private static final String BUNDLE_COUNT = "count";
    private static final String BUNDLE_RECYCLER = "recycler";

    private FragmentMovieListBinding mBinding;
    private MovieAdapter mMoviesAdapter;
    private RecyclerViewScrollListener mScrollListener;
    private GridLayoutManager mGridLayoutManager;
    private int mSorting;
    private Bundle mSavedInstanceState;
    private MovieListViewModel mViewModel;
    private FirebaseAnalytics firebaseAnalytics;

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

        mSorting = getArguments().getInt(SORTING_CODE, 0);

        mGridLayoutManager = new GridLayoutManager(getContext(), numberOfColumns());
        mMoviesAdapter = new MovieAdapter(getActivity(), mSorting);

        mBinding.moviesList.setLayoutManager(mGridLayoutManager);
        mBinding.moviesList.addItemDecoration(new GridItemDecoration(getContext()));
        mBinding.moviesList.setAdapter(mMoviesAdapter);

        mBinding.swipeRefreshLayout.setEnabled(false);

        mScrollListener = new RecyclerViewScrollListener(mGridLayoutManager) {
            @Override
            public void onLoadMore(int page) {
                mViewModel.loadMovies(mSorting, page);
            }
        };

        mBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.getStatus().setValue(0);
                populateUI();
            }
        });

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BaseActivity baseActivity = (BaseActivity) getActivity();
        firebaseAnalytics = baseActivity.mFirebaseAnalytics;

        Bundle payload = new Bundle();
        payload.putString(FirebaseAnalytics.Param.ITEM_CATEGORY,
                getSortingName(getContext(), mSorting));
        firebaseAnalytics.
                logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, payload);

        mViewModel = ViewModelProviders.of(this).get(MovieListViewModel.class);

        if (mSorting != Codes.FAVORITES) {
            showInternetStatus();
        }

        populateUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.getFavoriteList().removeObservers(this);
        mViewModel.getMovieList(mSorting).removeObservers(this);
        mViewModel.getStatus().removeObservers(this);
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
        outState.putInt(BUNDLE_PAGE, mScrollListener.getPage());
        outState.putInt(BUNDLE_COUNT, mScrollListener.getCount());
        outState.putParcelable(BUNDLE_RECYCLER, mGridLayoutManager.onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    private void populateUI() {
        mMoviesAdapter.clearList();
        hideStatus();

        if (mSorting == Codes.FAVORITES) {
            mBinding.swipeRefreshLayout.setEnabled(false);
            mViewModel.getFavoriteList().observe(this,
                    new Observer<List<Movie>>() {
                        @Override
                        public void onChanged(@Nullable List<Movie> movies) {
                            if (movies != null) {
                                if (mMoviesAdapter.getItemCount() < movies.size()) {
                                    hideStatus();
                                    mMoviesAdapter.addList(movies);
                                } else if (movies.size() == 0) {
                                    showNoFavoriteStatus();
                                }
                            }
                        }
                    });
        } else {
            mViewModel.getMovieList(mSorting).observe(this,
                    new Observer<List<Movie>>() {
                        @Override
                        public void onChanged(@Nullable List<Movie> movies) {
                            if (movies != null) {
                                mMoviesAdapter.addList(movies);
                            }
                        }
                    });
        }

        if (mSavedInstanceState != null) {
            if (mSorting == Codes.FAVORITES) {
                mBinding.moviesList.clearOnScrollListeners();
            } else {
                mScrollListener.setState(
                        mSavedInstanceState.getInt(BUNDLE_PAGE),
                        mSavedInstanceState.getInt(BUNDLE_COUNT));
                mBinding.moviesList.addOnScrollListener(mScrollListener);
            }
            mGridLayoutManager
                    .onRestoreInstanceState(mSavedInstanceState.getParcelable(BUNDLE_RECYCLER));
        } else {
            if (mSorting == Codes.FAVORITES) {
                mBinding.moviesList.clearOnScrollListeners();
            } else {
                mScrollListener.resetState();
                mBinding.moviesList.addOnScrollListener(mScrollListener);
            }
        }
    }

    /**
     * calculates number of columns
     *
     * @return the number of columns in the grid layout of main ui
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

    private void showInternetStatus() {
        mViewModel.getStatus().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer status) {
                switch (status) {
                    case Codes.LOADING:
                        mBinding.swipeRefreshLayout.setRefreshing(true);
                        hideStatus();
                        break;
                    case Codes.SUCCESS:
                        mBinding.swipeRefreshLayout.setRefreshing(false);
                        mBinding.swipeRefreshLayout.setEnabled(false);
                        hideStatus();
                        break;
                    case Codes.ERROR:
                        mBinding.swipeRefreshLayout.setRefreshing(false);
                        mBinding.swipeRefreshLayout.setEnabled(true);
                        mBinding.statusImage.setImageResource(R.drawable.ic_signal_wifi_off_white_24px);
                        mBinding.statusImage.setVisibility(View.VISIBLE);
                        mBinding.statusText.setText(R.string.no_internet);
                        mBinding.statusText.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
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

    public void notThisStar() {
        Snackbar.make(mBinding.mainLayout, getString(R.string.not_this_star), Snackbar.LENGTH_LONG).show();
    }
}