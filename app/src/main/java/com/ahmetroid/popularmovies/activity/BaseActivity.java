package com.ahmetroid.popularmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ahmetroid.popularmovies.R;
import com.ahmetroid.popularmovies.adapter.MovieListPager;
import com.ahmetroid.popularmovies.adapter.PagerItem;

import java.util.ArrayList;

public abstract class BaseActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MovieListPager mPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPagerAdapter = new MovieListPager(getSupportFragmentManager(), getPagerItems());

        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setAdapter(mPagerAdapter);

        mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Intent intent;

                        switch (menuItem.getItemId()) {
                            case R.id.nav_movie_list:
                                intent = new Intent(BaseActivity.this, MovieListActivity.class);
                                break;
                            case R.id.nav_movie_genre:
                                intent = new Intent(BaseActivity.this, MovieGenreActivity.class);
                                break;
                            case R.id.nav_about:
                            default:
                                intent = new Intent(BaseActivity.this, AboutActivity.class);
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

    public abstract ArrayList<PagerItem> getPagerItems();

    public abstract int getCheckedItem();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
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
}
