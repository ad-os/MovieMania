package io.github.ad_os.moviemania.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import io.github.ad_os.moviemania.R;
import io.github.ad_os.moviemania.Utility;

/**
 * Created by adhyan on 3/2/16.
 */
public class VideoThumbnailRecyclerViewAdapter extends RecyclerView.Adapter<VideoThumbnailRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> mVideos;
    private Context mContext;
    public static String LOG_TAG = VideoThumbnailRecyclerViewAdapter.class.getSimpleName();
    public static final String YOUTUBE_BASE_URL = "http://img.youtube.com/vi/";
    public VideoThumbnailRecyclerViewAdapter(Context context, ArrayList<String> items) {
        mVideos = items;
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
        String id = mVideos.get(position);
        String url = YOUTUBE_BASE_URL + id + "/0.jpg";
        Utility.setImage(mContext, holder.mImageView, url);
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
    }

    public void addData(ArrayList<String> data) {
        mVideos.clear();
        mVideos.addAll(data);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.thumbnail_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String id = mVideos.get(getLayoutPosition());
            try{
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
                mContext.startActivity(intent);
            }catch (ActivityNotFoundException ex){
                Intent intent=new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v="+id));
                mContext.startActivity(intent);
            }
        }
    }
}
