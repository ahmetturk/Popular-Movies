package com.ahmetroid.popularmovies.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;

import com.ahmetroid.popularmovies.viewmodel.BaseMoviesViewModel;
import com.ahmetroid.popularmovies.viewmodel.MoviesViewModel;

public class MoviesFragment extends BaseMoviesFragment {

    private static final String SORTING_CODE = "sorting_code";

    public static MoviesFragment newInstance(int sortingCode) {
        MoviesFragment moviesFragment = new MoviesFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(SORTING_CODE, sortingCode);
        moviesFragment.setArguments(bundle);
        return moviesFragment;
    }

    @Override
    BaseMoviesViewModel getViewModel() {
        MoviesViewModel viewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);
        viewModel.setSortingCode(getArguments().getInt(SORTING_CODE));
        return viewModel;
    }
}