package com.example.bhati.moviehub;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();
    public static final String EXTRA_RESULT_OBJECT = "resultObject";
    Result mResult;

    private ImageView mMoviebackdropImage;
    private ImageView mMoviePosterImage;
    private TextView mMovieTitle;
    private TextView mMovieReleaseDate;
    private TextView mMovieRating;
    private TextView mMovieOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMoviebackdropImage = findViewById(R.id.iv_movie_backdrop_detail);
        mMoviePosterImage = findViewById(R.id.iv_movie_poster_detail);
        mMovieTitle = findViewById(R.id.tv_movie_title_detail);
        mMovieReleaseDate = findViewById(R.id.tv_movie_release_detail);
        mMovieRating = findViewById(R.id.tv_movie_rating_detail);
        mMovieOverview = findViewById(R.id.tv_movie_overview_detail);
        mResult = getIntent().getParcelableExtra(EXTRA_RESULT_OBJECT);

        initializeUI();

    }

    private void initializeUI() {
        String imageUrl = MovieAPI.IMAGE_URL_ORIGINAL + mResult.getBackdropPath();

        Picasso.with(this)
                .load(imageUrl)
                .into(mMoviebackdropImage);
        Picasso.with(this)
                .load(MovieAPI.IMAGE_URL + mResult.getPosterPath())
                .into(mMoviePosterImage);

        mMovieTitle.setText(mResult.getTitle());
        mMovieReleaseDate.setText(mResult.getReleaseDate());
        mMovieRating.setText(getString(R.string.rating,String.valueOf(mResult.getVoteAverage())));
        mMovieOverview.setText(mResult.getOverview());

    }

}
