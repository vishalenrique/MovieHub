
package com.example.bhati.moviehub;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "movies")
public class Result implements Parcelable{


    @SerializedName("vote_count")
    @Expose
    
    private Integer voteCount;
    @SerializedName("id")
    @Expose
    @PrimaryKey
    private Integer id;
    @SerializedName("video")
    @Expose
    
    private Boolean video;
    @SerializedName("vote_average")
    @Expose
    @ColumnInfo(name = "vote_average")
    private Double voteAverage;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("popularity")
    @Expose
    
    private Double popularity;
    @SerializedName("poster_path")
    @Expose
    @ColumnInfo(name = "poster_path")
    private String posterPath;
    @SerializedName("original_language")
    @Expose
    
    private String originalLanguage;
    @SerializedName("original_title")
    @Expose
    
    private String originalTitle;
    @SerializedName("genre_ids")
    @Expose

    @Ignore
    private List<Integer> genreIds = null;
    @SerializedName("backdrop_path")
    @Expose
    @ColumnInfo(name = "backdrop_path")
    private String backdropPath;
    @SerializedName("adult")
    @Expose
    
    private Boolean adult;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("release_date")
    @Expose
    private String releaseDate;

//    private boolean isFavorite;
//
//    public boolean isFavorite() {
//        return isFavorite;
//    }
//
//    public void setFavorite(boolean favorite) {
//        isFavorite = favorite;
//    }

    public Result(Integer id, Double voteAverage, String title, String posterPath, String backdropPath, String overview, String releaseDate) {
        this.id = id;
        this.voteAverage = voteAverage;
        this.title = title;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
//        this.isFavorite = isFavorite;
    }

    @Ignore
    protected Result(Parcel in) {
        voteCount = in.readInt();
        id = in.readInt();
        video =in.readByte() == 1;
        voteAverage = in.readDouble();
        title = in.readString();
        popularity = in.readDouble();
        posterPath = in.readString();
        originalLanguage = in.readString();
        originalTitle = in.readString();
        genreIds = new ArrayList<>();
        in.readList(genreIds,Integer.class.getClassLoader());
        backdropPath = in.readString();
        adult = in.readByte() == 1;
        overview = in.readString();
        releaseDate = in.readString();
    }

    public static final Parcelable.Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(voteCount);
        dest.writeInt(id);
        dest.writeByte((byte)(video?1:0));
        dest.writeDouble(voteAverage);
        dest.writeString(title);
        dest.writeDouble(popularity);
        dest.writeString(posterPath);
        dest.writeString(originalLanguage);
        dest.writeString(originalTitle);
        dest.writeList(genreIds);
        dest.writeString(backdropPath);
        dest.writeByte((byte)(adult?1:0));
        dest.writeString(overview);
        dest.writeString(releaseDate);

    }

    @Override
    public String toString() {
        return "Here"+"\n"
                +voteCount+"\n"
                +id+"\n"
                +video+"\n"
                +voteAverage+"\n"
                +title+"\n"
                +popularity+"\n"
                +posterPath+"\n"
                +originalLanguage+"\n"
                +originalTitle+"\n"
                +genreIds+"\n"
                +backdropPath+"\n"
                +adult+"\n"
                +overview+"\n"
                +releaseDate+"\n";
    }
}
