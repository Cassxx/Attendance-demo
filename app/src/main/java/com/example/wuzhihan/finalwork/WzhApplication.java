package com.example.wuzhihan.finalwork;

import android.content.Context;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by Wuzhihan on 2017/4/30.
 */

public class WzhApplication extends android.app.Application {

    private static final String TAG = "wzhApplication";

    private static File privateDir;

    private static String mFileName = null;

    @Override
    public void onCreate() {
        createDefaultFiles();
        initImageLoader(this);
        super.onCreate();
    }

    private void createDefaultFiles() {
        File cacheDir = StorageUtils.getCacheDirectory(this);
        mFileName = cacheDir.getAbsolutePath();
        Log.d(TAG ," createDefaultFiles FileName = "+mFileName);
        File individualCacheDir = new File(cacheDir, "download");
        if (!individualCacheDir.exists()) {
            individualCacheDir.mkdir();
        }

        individualCacheDir = new File(cacheDir, "photo");
        if (!individualCacheDir.exists()) {
            individualCacheDir.mkdir();
        }

        individualCacheDir = new File(cacheDir, "log");
        if (!individualCacheDir.exists()) {
            individualCacheDir.mkdir();
        }

        privateDir = new File(getFilesDir(), "download");
        if (!privateDir.exists()) {
            privateDir.mkdir();
        }
    }

    private static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(100 * 1024 * 1024)
                // 100 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }


    public static final String getImagePhotoPath() {
        return mFileName + "/photo";
    }
}
