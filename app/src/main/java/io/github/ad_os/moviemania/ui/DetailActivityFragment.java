package io.github.ad_os.moviemania.ui;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.ad_os.moviemania.R;
import io.github.ad_os.moviemania.model.Movie;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private Movie mMovie;
    public static final String TAG = DetailActivity.class.getSimpleName();
    @Bind(R.id.movie_release_date) TextView mReleaseDate;
    @Bind(R.id.ratingBar) RatingBar mRatingBar;
    @Bind(R.id.movie_synopsis) TextView mSynopsis;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);
        Intent intent = getActivity().getIntent();
        Parcelable parcelable = intent.getParcelableExtra(MainActivityFragment.MOVIE_DETAILS);
        mMovie = (Movie) parcelable;
        mReleaseDate.setText(mMovie.getReleaseDate());
        mRatingBar.setRating(Float.parseFloat(mMovie.getUserRating())/2);
        mSynopsis.setText(mMovie.getPlotSynopsis());
        return rootView;
    }
}
