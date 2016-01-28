package io.github.ad_os.moviemania;

import android.content.Context;
import android.content.SharedPreferences;

import io.github.ad_os.moviemania.ui.Main;

/**
 * Created by adhyan on 28/1/16.
 */
public class Utility {

    public static void userChoice(Context context, String choice) {
        SharedPreferences preferences = context.getSharedPreferences(Main.MOVIE_VIEW, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = preferences.edit();
        e.putString("view", choice);
        e.commit();
    }

}
