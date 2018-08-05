package com.ahmetroid.popularmovies.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.ahmetroid.popularmovies.R;

public class SearchActivity extends AppCompatActivity {

    private static final String SEARCH_CODE = "search_code";

    public static Intent newIntent(Context context, String searchString) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(SEARCH_CODE, searchString);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle(getString(R.string.search_title, getIntent().getStringExtra(SEARCH_CODE)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container,
                            SearchFragment.newInstance(getIntent().getStringExtra(SEARCH_CODE)))
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
