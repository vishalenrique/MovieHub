package com.example.bhati.moviehub;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bhati.moviehub.database.AppDatabase;
import com.example.bhati.moviehub.movieList.Result;
import com.example.bhati.moviehub.network.MovieAPI;
import com.example.bhati.moviehub.reviews.DetailReviewAdapter;
import com.example.bhati.moviehub.reviews.MovieReviews;
import com.example.bhati.moviehub.videos.DetailAdapter;
import com.example.bhati.moviehub.videos.MovieVideos;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements DetailAdapter.OnClickTrailer {

    private static final String TAG = DetailActivity.class.getSimpleName();
    public static final String EXTRA_RESULT_OBJECT = "resultObject";
    Result mResult;

    private ImageView mMovieBackdropImage;
    private ImageView mMoviePosterImage;
    private TextView mMovieTitle;
    private TextView mMovieReleaseDate;
    private TextView mMovieRating;
    private TextView mMovieOverview;
    private ImageView mMovieFavorite;
    private RecyclerView mRecyclerView;
    private RecyclerView mReviewRecyclerView;
    private DetailReviewAdapter mReviewAdapter;
    private DetailAdapter mAdapter;

    boolean isFavorite;
    AppDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMovieBackdropImage = findViewById(R.id.iv_movie_backdrop_detail);
        mMoviePosterImage = findViewById(R.id.iv_movie_poster_detail);
        mMovieTitle = findViewById(R.id.tv_movie_title_detail);
        mMovieReleaseDate = findViewById(R.id.tv_movie_release_detail);
        mMovieRating = findViewById(R.id.tv_movie_rating_detail);
        mMovieOverview = findViewById(R.id.tv_movie_overview_detail);
        mMovieFavorite = findViewById(R.id.iv_movie_favorite_detail);
        mResult = getIntent().getParcelableExtra(EXTRA_RESULT_OBJECT);
        mRecyclerView = findViewById(R.id.rv_movie_detail);
        mReviewRecyclerView = findViewById(R.id.rv_movie_review_detail);
        mDatabase = AppDatabase.getInstance(this.getApplicationContext());

        //setting up recycler view for trailers

        mAdapter = new DetailAdapter(null,this,mResult.getPosterPath(),this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        //settting up recycler view for reviews
        mReviewAdapter = new DetailReviewAdapter(null,this);
        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mReviewRecyclerView.setLayoutManager(reviewLayoutManager);
        mReviewRecyclerView.setAdapter(mReviewAdapter);


        getSupportActionBar().setTitle(mResult.getTitle());
        setupFavoriteIcon();
        initializeUI();
        setupTrailers();
        setupReviews();

        mMovieFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFavorite){
                    mMovieFavorite.setImageResource(R.drawable.ic_favorite_border_red_48dp);
                }else{
                    mMovieFavorite.setImageResource(R.drawable.ic_favorite_red_48dp);
                }
                databaseOperations(isFavorite);
                isFavorite=!isFavorite;
            }
        });

    }

    private void setupReviews() {
        Call<MovieReviews> reviews = MovieAPI.getService().getMovieReviews(mResult.getId(), 1);
        reviews.enqueue(new Callback<MovieReviews>() {
            @Override
            public void onResponse(Call<MovieReviews> call, Response<MovieReviews> response) {
                MovieReviews movieReviews = response.body();
              if(movieReviews != null){
                  Log.d(TAG,"Reviews : "+movieReviews.getResults().size()+"");
                  mReviewAdapter.setResults(movieReviews.getResults());
              }
            }

            @Override
            public void onFailure(Call<MovieReviews> call, Throwable t) {

            }
        });
    }

    private void setupTrailers() {
        Call<MovieVideos> videos = MovieAPI.getService().getMovieVideos(mResult.getId());
        videos.enqueue(new Callback<MovieVideos>() {
            @Override
            public void onResponse(Call<MovieVideos> call, Response<MovieVideos> response) {
                MovieVideos movieVideos = response.body();
              if( movieVideos != null) {
                  Log.d(TAG, "Videos : " + movieVideos.getResults().size());
                  mAdapter.setResults(movieVideos.getResults());
              }
            }

            @Override
            public void onFailure(Call<MovieVideos> call, Throwable t) {

            }
        });
    }

    private void databaseOperations(final boolean isFavorite) {
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(isFavorite) {
                    mDatabase.resultDao().deleteMovie(mResult);
                }else{
                    mDatabase.resultDao().insertMovie(mResult);
                }
            }
        });
    }

    private void initializeUI() {
        Picasso.with(this)
                .load(MovieAPI.IMAGE_URL_ORIGINAL + mResult.getBackdropPath())
                .into(mMovieBackdropImage);
        Picasso.with(this)
                .load(MovieAPI.IMAGE_URL + mResult.getPosterPath())
                .into(mMoviePosterImage);

        mMovieTitle.setText(mResult.getTitle());
        mMovieReleaseDate.setText(mResult.getReleaseDate());
        mMovieRating.setText(getString(R.string.rating,String.valueOf(mResult.getVoteAverage())));
        mMovieOverview.setText(mResult.getOverview());


    }

    private void setupFavoriteIcon() {
        AddMovieViewModelFactory viewModelFactory = new AddMovieViewModelFactory(mDatabase,mResult);
        final AddMovieViewModel viewModel = ViewModelProviders.of(this,viewModelFactory).get(AddMovieViewModel.class);
        viewModel.getResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(@Nullable Result result) {
                viewModel.getResult().removeObserver(this);
                isFavorite = result!=null;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(isFavorite){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mMovieFavorite.setImageResource(R.drawable.ic_favorite_red_48dp);
                                }
                            });
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mMovieFavorite.setImageResource(R.drawable.ic_favorite_border_red_48dp);
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onClick(String key) {
       watchYoutubeVideo(this, key);
    }

    public static void watchYoutubeVideo(Context context, String key){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + key));
        Log.d(TAG,"http://www.youtube.com/watch?v=" + key);
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }
}
