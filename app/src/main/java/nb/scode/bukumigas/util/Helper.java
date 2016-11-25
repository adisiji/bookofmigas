package nb.scode.bukumigas.util;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nb.scode.bukumigas.R;

public class Helper extends Application {

    public static int getGridSpanCount(Activity activity) {

        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        float screenWidth  = displayMetrics.widthPixels;
        float cellWidth = activity.getResources().getDimension(R.dimen.item_size);
        return Math.round(screenWidth / cellWidth);
    }

}
