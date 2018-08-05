package com.ahmetroid.popularmovies.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;

import com.ahmetroid.popularmovies.viewmodel.BaseMoviesViewModel;
import com.ahmetroid.popularmovies.viewmodel.SearchViewModel;

public class SearchFragment extends BaseMoviesFragment {

    private static final String SEARCH_CODE = "search_code";

    public static SearchFragment newInstance(String searchString) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SEARCH_CODE, searchString);
        searchFragment.setArguments(bundle);
        return searchFragment;
    }

    @Override
    BaseMoviesViewModel getViewModel() {
        SearchViewModel viewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        viewModel.setSearchString(getArguments().getString(SEARCH_CODE));
        return viewModel;
    }
}
