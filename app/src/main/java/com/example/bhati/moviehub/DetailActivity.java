package com.example.bhati.moviehub;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

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
        mDatabase = AppDatabase.getInstance(this.getApplicationContext());

        getSupportActionBar().setTitle(mResult.getTitle());
        setupFavoriteIcon();
        initializeUI();

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

}
