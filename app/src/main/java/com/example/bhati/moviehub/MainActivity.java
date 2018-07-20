package com.example.bhati.moviehub;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieAdapter.onClicked {

    private static final String PAGE_KEY = "pageKey";
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private ArrayList<Result> mResults = new ArrayList<>();
    private MovieAdapter mMovieAdapter;
    private GridLayoutManager mLayoutManager;
    String mCategory;
    int mPage = 1;
    boolean mIsScrolling;
    int mCurrentItems = 0, mTotalItems = 0, mScrolledOutItems = 0;
    private static final String RESULTS_KEY = "parcelableObjects";
    static boolean isFavoriteMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCategory = getString(R.string.popular);
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

                if(!isFavoriteMode) {
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
            getData();
        } else {
            if (savedInstanceState.containsKey(RESULTS_KEY)) {
                mPage = savedInstanceState.getInt(PAGE_KEY);
                mResults.addAll(savedInstanceState.<Result>getParcelableArrayList(RESULTS_KEY));
                mMovieAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
            }
        }
        if(isFavoriteMode){
            mCategory = getString(R.string.favorite);
            setupViewModel();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RESULTS_KEY, mResults);
        outState.putInt(PAGE_KEY,mPage);
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
            }
            return true;
        } else if (id == R.id.action_rating) {
            if (!mCategory.equals(getString(R.string.top_rating))) {
                mCategory = getString(R.string.top_rating);
                refreshUI();
            }
            return true;
        } else if(id == R.id.action_favorite){
            if(!mCategory.equals(getString(R.string.favorite))) {
                mCategory = getString(R.string.favorite);
                isFavoriteMode = true;
                mProgressBar.setVisibility(View.GONE);
                resetRecyclerViewPosition();
                setupViewModel();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshUI() {
        isFavoriteMode = false;
        mProgressBar.setVisibility(View.VISIBLE);
        resetRecyclerViewPosition();
        getData();
    }

    private void setupViewModel() {
        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getResults().observe(this, new Observer<List<Result>>() {
            @Override
            public void onChanged(@Nullable List<Result> results) {
                mMovieAdapter.setResults(results);
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
                   Snackbar.make(mRecyclerView, R.string.data_unavailable,Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                Snackbar.make(mRecyclerView, R.string.network_unavailable,Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClicked(Result result) {
        isFavoriteMode = false;
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_RESULT_OBJECT, result);
        startActivity(intent);
    }
}
