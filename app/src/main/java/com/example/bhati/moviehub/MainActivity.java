package com.example.bhati.moviehub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
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

public class MainActivity extends AppCompatActivity implements MovieAdapter.onClicked{

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private List<Result> mResults = new ArrayList<>();
    private MovieAdapter mMovieAdapter;
    private GridLayoutManager mLayoutManager;

    String mCategory;
    int mPage=1;

    boolean mIsScrolling;
    int mCurrentItems=0, mTotalItems=0, mScrolledOutItems=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCategory = getString(R.string.popular);
        mProgressBar = findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);

        // Setting up the RecyclerView
        mRecyclerView = findViewById(R.id.rv_main);
        mLayoutManager = new GridLayoutManager(this, calculateColumns());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mMovieAdapter = new MovieAdapter(this, mResults,this);
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
        });

        getData();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_popular) {
            if(!mCategory.equals(getString(R.string.popular))) {
                mCategory = getString(R.string.popular);
                mPage=1;
                mResults.clear();
                getData();
            }
            return true;
        }else if(id == R.id.action_rating){
            if(!mCategory.equals(getString(R.string.top_rating))) {
                mCategory = getString(R.string.top_rating);
                mPage = 1;
                mResults.clear();
                mMovieAdapter.notifyDataSetChanged();
                getData();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getData() {
        Call<MovieList> movieListCall = MovieAPI.getService().getMovieList(mCategory, mPage);
        movieListCall.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                mProgressBar.setVisibility(View.GONE);
                MovieList movieList = response.body();
                mResults.addAll(movieList.getResults());
                mMovieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClicked(Result result) {
        Intent intent=new Intent(this,DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_RESULT_OBJECT,result);
        startActivity(intent);
        }

    public int calculateColumns() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 150);
        return noOfColumns;
    }
}
