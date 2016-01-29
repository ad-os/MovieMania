package io.github.ad_os.moviemania.model;

/**
 * Created by adhyan on 30/1/16.
 */
public class MovieImageUrl {
    private String mThumbnailImageUrl;
    private String mBackPosterImageUrl;

    public String getBackPosterImageUrl() {
        return mBackPosterImageUrl;
    }

    public void setBackPosterImageUrl(String backPosterImageUrl) {
        mBackPosterImageUrl = backPosterImageUrl;
    }

    public String getThumbnailImageUrl() {
        return mThumbnailImageUrl;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        mThumbnailImageUrl = thumbnailImageUrl;
    }
}
