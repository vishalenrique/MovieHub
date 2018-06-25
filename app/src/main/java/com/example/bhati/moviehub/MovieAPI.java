package com.example.bhati.moviehub;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class MovieAPI {

    private static final String KEY = "";
    private static final String MOVIE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String IMAGE_URL = "http://image.tmdb.org/t/p/w500/";
    public static MovieService sMovieService;

    public static MovieService getService() {
        if (sMovieService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MOVIE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            sMovieService = retrofit.create(MovieService.class);
        }
        return sMovieService;
    }

    public interface MovieService {
        @GET("{category}/?api_key=" + KEY)
        Call<MovieList> getMovieList(@Path("category") String category, @Query("page") int page);
    }

}
