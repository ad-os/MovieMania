package io.github.ad_os.moviemania.model;

/**
 * Created by adhyan on 29/12/15.
 */
public class Movie {
    String mMovieTitle;
    String mPosterString;
    String mPlotSynopsis;
    String mUserRating;
    String mReleaseDate;

    public Movie() {}

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

}
