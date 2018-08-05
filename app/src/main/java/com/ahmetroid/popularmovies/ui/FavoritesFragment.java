package com.ahmetroid.popularmovies.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.view.View;

import com.ahmetroid.popularmovies.R;
import com.ahmetroid.popularmovies.model.Movie;
import com.ahmetroid.popularmovies.viewmodel.BaseMoviesViewModel;
import com.ahmetroid.popularmovies.viewmodel.FavoritesViewModel;

import java.util.List;

public class FavoritesFragment extends BaseMoviesFragment {

    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    @Override
    BaseMoviesViewModel getViewModel() {
        return ViewModelProviders.of(this).get(FavoritesViewModel.class);
    }

    @Override
    void onMovieChanged(List<Movie> movies) {
        if (movies.size() == 0) {
            showNoFavoriteStatus();
        } else {
            hideStatus();
        }
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
}
