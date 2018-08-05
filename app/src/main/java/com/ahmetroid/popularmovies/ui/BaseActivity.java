package com.ahmetroid.popularmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ahmetroid.popularmovies.R;
import com.ahmetroid.popularmovies.adapter.MovieFragmentPager;
import com.ahmetroid.popularmovies.adapter.PagerItem;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

import static com.ahmetroid.popularmovies.utils.Codes.ADMOB_APP_ID;

public abstract class BaseActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private int exitCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        exitCount = 1;

        MobileAds.initialize(this, ADMOB_APP_ID);

        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("521D6B4183AE010C9D5AB685FF5F0B25")
                .build();
        adView.loadAd(adRequest);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MovieFragmentPager pagerAdapter = getPager();

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);

        final TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        Intent intent;

                        switch (menuItem.getItemId()) {
                            case R.id.nav_movie_list:
                                intent = MovieListActivity.newIntent(BaseActivity.this);
                                break;
                            case R.id.nav_movie_genre:
                                intent = MovieGenreActivity.newIntent(BaseActivity.this);
                                break;
                            case R.id.nav_favorites:
                                intent = FavoritesActivity.newIntent(BaseActivity.this);
                                break;
                            case R.id.nav_about:
                            default:
                                intent = AboutActivity.newIntent(BaseActivity.this);
                                break;
                        }

                        mDrawerLayout.closeDrawers();

                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                        return true;
                    }
                });

        navigationView.setCheckedItem(getCheckedItem());
    }

    abstract ArrayList<PagerItem> getPagerItems();

    abstract int getCheckedItem();

    MovieFragmentPager getPager() {
        return new MovieFragmentPager(getSupportFragmentManager(), getPagerItems());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                menuItem.collapseActionView();
                startActivity(SearchActivity.newIntent(BaseActivity.this, query));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (exitCount > 0) {
            mDrawerLayout.openDrawer(Gravity.START);
            exitCount--;
            Toast.makeText(this, getString(R.string.press_again), Toast.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
    }
}