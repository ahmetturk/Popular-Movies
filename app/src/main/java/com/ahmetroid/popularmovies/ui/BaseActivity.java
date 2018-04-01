package com.ahmetroid.popularmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.ahmetroid.popularmovies.R;
import com.ahmetroid.popularmovies.adapter.MovieFragmentPager;
import com.ahmetroid.popularmovies.adapter.PagerItem;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import static com.ahmetroid.popularmovies.utils.Codes.ADMOB_APP_ID;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String BUNDLE_SELECTED = "selected";

    private DrawerLayout mDrawerLayout;
    private int mSelected;
    public FirebaseAnalytics mFirebaseAnalytics;
    private int exitCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        exitCount = 1;

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        MobileAds.initialize(this, ADMOB_APP_ID);

        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        adView.loadAd(adRequest);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MovieFragmentPager pagerAdapter = new MovieFragmentPager(getSupportFragmentManager(), getPagerItems());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

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
                        Bundle payload = new Bundle();

                        switch (menuItem.getItemId()) {
                            case R.id.nav_movie_list:
                                intent = new Intent(BaseActivity.this, MovieListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                mSelected = 0;
                                payload.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Movies");
                                payload.putString(FirebaseAnalytics.Param.ITEM_NAME, "Movies Lists");
                                break;
                            case R.id.nav_movie_genre:
                                intent = new Intent(BaseActivity.this, MovieGenreActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                mSelected = 1;
                                payload.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Movies");
                                payload.putString(FirebaseAnalytics.Param.ITEM_NAME, "Movies Genres");
                                break;
                            case R.id.nav_about:
                            default:
                                payload.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Others");
                                payload.putString(FirebaseAnalytics.Param.ITEM_NAME, "About");
                                intent = new Intent(BaseActivity.this, AboutActivity.class);
                                break;
                        }

                        mFirebaseAnalytics.
                                logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, payload);

                        mDrawerLayout.closeDrawers();

                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                        return true;
                    }
                });

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(getCheckedItem());
        } else {
            mSelected = savedInstanceState.getInt(BUNDLE_SELECTED);
            navigationView.setCheckedItem(mSelected);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(BUNDLE_SELECTED, mSelected);
        super.onSaveInstanceState(outState);
    }

    protected abstract ArrayList<PagerItem> getPagerItems();

    protected abstract int getCheckedItem();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        /*SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_search:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.dialog_message)
                        .setTitle(R.string.dialog_title);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (exitCount > 0) {
            mDrawerLayout.openDrawer(Gravity.START);
            exitCount--;
            return;
        }
        super.onBackPressed();
    }
}