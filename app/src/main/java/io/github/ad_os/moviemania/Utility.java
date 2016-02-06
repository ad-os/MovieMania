package io.github.ad_os.moviemania;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import static io.github.ad_os.moviemania.R.string.content_authority;

/**
 * Created by adhyan on 28/1/16.
 */
public class Utility {

    public static void setUserChoice(Context context, String choice) {
        SharedPreferences preferences = context.getSharedPreferences(String.valueOf(content_authority), Context.MODE_PRIVATE);
        SharedPreferences.Editor e = preferences.edit();
        e.putString("view", choice);
        e.apply();
    }

    public static String getNextCount(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(String.valueOf(R.string.content_authority),
                Context.MODE_PRIVATE);
        int count = 0;
        int value = preferences.getInt(context.getString(R.string.picture_count), count);
        SharedPreferences.Editor e = preferences.edit();
        e.putInt(context.getString(R.string.picture_count), ++value);
        e.apply();
        return "_" + value;
    }

    public static void setImage(Context context, ImageView imageView, String url) {
        Picasso.with(context)
                .load(url)
                .placeholder(R.mipmap.background)
                .into(imageView);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
