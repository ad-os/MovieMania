package io.github.ad_os.moviemania.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by adhyan on 29/12/15.
 */
public class Movie implements Parcelable{
    String mMovieTitle;
    String mPosterString;
    String mPlotSynopsis;
    String mUserRating;
    String mReleaseDate;
    String mBackPosterString;
    String mId;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public Movie() {}

    public String getBackPosterString() {
        return mBackPosterString;
    }

    public void setBackPosterString(String backPosterString) {
        mBackPosterString = backPosterString;
    }

    public String getMovieTitle() {
        return mMovieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        mMovieTitle = movieTitle;
    }

    public String getPosterString() {
        return mPosterString;
    }

    public void setPosterString(String posterString) {
        mPosterString = posterString;
    }

    public String getPlotSynopsis() {
        return mPlotSynopsis;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        mPlotSynopsis = plotSynopsis;
    }

    public String getUserRating() {
        return mUserRating;
    }

    public void setUserRating(String userRating) {
        mUserRating = userRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMovieTitle);
        dest.writeString(mPosterString);
        dest.writeString(mPlotSynopsis);
        dest.writeString(mUserRating);
        dest.writeString(mReleaseDate);
        dest.writeString(mBackPosterString);
    }

    private Movie(Parcel in) {
        mMovieTitle = in.readString();
        mPosterString = in.readString();
        mPlotSynopsis = in.readString();
        mUserRating = in.readString();
        mReleaseDate = in.readString();
        mBackPosterString = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
