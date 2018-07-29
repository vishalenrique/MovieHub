package com.example.bhati.moviehub;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bhati.moviehub.movieList.MovieAdapter;
import com.example.bhati.moviehub.movieList.MovieList;
import com.example.bhati.moviehub.movieList.Result;
import com.example.bhati.moviehub.network.MovieAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieAdapter.onClicked {

    private static final String TAG = "MainActivity";
    private static final String PAGE_KEY = "pageKey";
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private ArrayList<Result> mResults = new ArrayList<>();
    private MovieAdapter mMovieAdapter;
    private GridLayoutManager mLayoutManager;
    String mCategory = "";
    int mPage = 1;
    boolean mIsScrolling;
    int mCurrentItems = 0, mTotalItems = 0, mScrolledOutItems = 0;
    private static final String RESULTS_KEY = "parcelableObjects";
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isConnected = isNetworkAvailable();
        mProgressBar = findViewById(R.id.progress_bar);

        // Setting up the RecyclerView
        mRecyclerView = findViewById(R.id.rv_main);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new GridLayoutManager(this, 2);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLayoutManager = new GridLayoutManager(this, 3);
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
        mMovieAdapter = new MovieAdapter(this, mResults, this);
        mRecyclerView.setAdapter(mMovieAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!isFavoriteMode()) {
                    mCurrentItems = mLayoutManager.getChildCount();
                    mTotalItems = mLayoutManager.getItemCount();
                    mScrolledOutItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (mIsScrolling && (mCurrentItems + mScrolledOutItems == mTotalItems)) {
                        mIsScrolling = false;
                        mPage++;
                        mProgressBar.setVisibility(View.VISIBLE);
                        getData();
                    }
                }
            }
        });


        if (savedInstanceState == null) {
            if (!isFavoriteMode()) {
                mCategory = getString(R.string.popular);
                getData();
            }
        } else {
            if (savedInstanceState.containsKey(RESULTS_KEY)){
                mPage = savedInstanceState.getInt(PAGE_KEY);
                mResults.addAll(savedInstanceState.<Result>getParcelableArrayList(RESULTS_KEY));
                mCategory = savedInstanceState.getString("category");
                mMovieAdapter.notifyDataSetChanged();
                if(mResults.size()>0)
                mProgressBar.setVisibility(View.GONE);
            }
        }

        if (isFavoriteMode()) {
            setupFavoriteMode();
        } else if (!isFavoriteMode() && !isConnected) {
            Snackbar.make(mRecyclerView, R.string.network_unavailable, Snackbar.LENGTH_SHORT).show();
        }

    }

    private boolean isFavoriteMode() {
        return mCategory.equals(getString(R.string.favorite));
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null &&
                activeNetworkInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("category", mCategory);
        outState.putParcelableArrayList(RESULTS_KEY, mResults);
        outState.putInt(PAGE_KEY, mPage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_popular) {
            if (!mCategory.equals(getString(R.string.popular))) {
                mCategory = getString(R.string.popular);
                refreshUI();
            }else{
                Snackbar.make(mRecyclerView,"Sorted by "+mCategory,Snackbar.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.action_rating) {
            if (!mCategory.equals(getString(R.string.top_rating))) {
                mCategory = getString(R.string.top_rating);
                refreshUI();
            }else{
                Snackbar.make(mRecyclerView,"Sorted by "+mCategory,Snackbar.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.action_favorite) {
            if (!isFavoriteMode()) {
                setupFavoriteMode();
            }else{
                Snackbar.make(mRecyclerView,"Sorted by "+mCategory,Snackbar.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupFavoriteMode() {
        mCategory = getString(R.string.favorite);
        mProgressBar.setVisibility(View.VISIBLE);
        resetRecyclerViewPosition();
        setupViewModel();
    }

    private void refreshUI() {
        mProgressBar.setVisibility(View.VISIBLE);
        resetRecyclerViewPosition();
        getData();
    }

    private void setupViewModel() {
        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getResults().observe(this, new Observer<List<Result>>() {
            @Override
            public void onChanged(@Nullable List<Result> results) {
                if(results.size()==0){
                    mProgressBar.setVisibility(View.VISIBLE);
                    Snackbar.make(mRecyclerView,R.string.no_favorite,Snackbar.LENGTH_SHORT).show();
                }else{
                    mProgressBar.setVisibility(View.GONE);
                }
                    if (isFavoriteMode()) {
                        mResults.clear();
                        mResults.addAll(results);
                        mMovieAdapter.setResults(mResults);
                    }
            }
        });
    }

    private void resetRecyclerViewPosition() {
        mPage = 1;
        mResults.clear();
        mRecyclerView.scrollToPosition(0);
        mMovieAdapter.setResults(mResults);
    }

    private void getData() {
        Call<MovieList> movieListCall = MovieAPI.getService().getMovieList(mCategory, mPage);
        movieListCall.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                mProgressBar.setVisibility(View.GONE);
                MovieList movieList = response.body();
                if (movieList != null) {
                    mResults.addAll(movieList.getResults());
                    mMovieAdapter.notifyDataSetChanged();
                } else {
                    Snackbar.make(mRecyclerView, R.string.data_unavailable, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                Snackbar.make(mRecyclerView, R.string.network_unavailable, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClicked(Result result) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_RESULT_OBJECT, result);
        startActivity(intent);
    }
}
