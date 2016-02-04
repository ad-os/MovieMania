package io.github.ad_os.moviemania.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.ad_os.moviemania.R;
import io.github.ad_os.moviemania.model.Review;

/**
 * Created by adhyan on 4/2/16.
 */
public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Review> mReviews;
    private Context mContext;
    private static String LOG_TAG = ReviewRecyclerViewAdapter.class.getSimpleName();
    public ReviewRecyclerViewAdapter(Context context, ArrayList<Review> items) {
        mContext = context;
        mReviews = items;
    }

    public void addData(ArrayList<Review> data) {
        mReviews.clear();
        mReviews.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Review review = mReviews.get(position);
        holder.authorTextView.setText(review.getAuthor());
        holder.authorTextView.setMovementMethod(new ScrollingMovementMethod());
        holder.contentTextView.setText(review.getContent());
        holder.contentTextView.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView authorTextView;
        public TextView contentTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            authorTextView = (TextView) itemView.findViewById(R.id.author_text);
            contentTextView = (TextView) itemView.findViewById(R.id.review_content);
        }
    }
}
