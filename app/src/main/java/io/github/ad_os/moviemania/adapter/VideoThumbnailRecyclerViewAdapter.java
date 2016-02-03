package io.github.ad_os.moviemania.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.RequiresPermission;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import io.github.ad_os.moviemania.R;
import io.github.ad_os.moviemania.Utility;

/**
 * Created by adhyan on 3/2/16.
 */
public class VideoThumbnailRecyclerViewAdapter extends RecyclerView.Adapter<VideoThumbnailRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> mStrings;
    private Context mContext;
    public static String LOG_TAG = VideoThumbnailRecyclerViewAdapter.class.getSimpleName();
    public static final String YOUTUBE_BASE_URL = "http://img.youtube.com/vi/";
    public VideoThumbnailRecyclerViewAdapter(Context context, ArrayList<String> items) {
        mStrings = items;
        mContext =context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_thumnail_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String id = mStrings.get(position);
        String url = YOUTUBE_BASE_URL + id + "/default.jpg";
        Log.d(LOG_TAG, "onBindViewHolder: " + url);
        Utility.setImage(mContext, holder.mImageView, url);
//            imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    try{
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
//                        startActivity(intent);
//                    }catch (ActivityNotFoundException ex){
//                        Intent intent=new Intent(Intent.ACTION_VIEW,
//                                Uri.parse("http://www.youtube.com/watch?v="+id));
//                        startActivity(intent);
//                    }
//                }
//            });
    }

    @Override
    public int getItemCount() {
        Log.d(LOG_TAG, "getItemCount: " + mStrings.size());
        return mStrings.size();
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
    }

    public void addData(ArrayList<String> data) {
        mStrings.clear();
        mStrings.addAll(data);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.thumbnail_view);
        }
    }
}
