package com.example.wuzhihan.finalwork;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Wuzhihan on 2017/4/12.
 */

public class Util {
    private static final String TAG = "Util";

    /**
     * 启动拍照
     */
    public static final int REQUEST_CODE_FROM_CAMERA = 3;

    public static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = cm.getActiveNetworkInfo();

        return info != null && info.isConnected();
    }



    private static int mWidth = -1;

    private static int mHeight = -1;

    private static float mDensity = 2.0f;

    public static void setScreenWidth(int width) {
        mWidth = width;
    }

    public static void setScreenHeight(int height) {
        mHeight = height;
    }

    public static int getScreenWidth() {
        return mWidth;
    }

    public static int getScreenHeight() {
        return mHeight;
    }

    public static void setScreenDensity(float density) {
        mDensity = density;
    }

    public static float getScreenDensity() {
        return mDensity;
    }


}
