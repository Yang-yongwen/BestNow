package com.yyw.android.bestnow.data.appusage;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.ArrayMap;
import android.util.LruCache;

import com.yyw.android.bestnow.NowApplication;
import com.yyw.android.bestnow.common.utils.LogUtils;

import java.util.Map;

/**
 * Created by yangyongwen on 16/12/2.
 */

public class AppInfoProvider {
    private static final String TAG = LogUtils.makeLogTag(AppInfoProvider.class);
    private static final int CACHE_DRAWABLE_SIZE = 170;
    private LruCache<String, Drawable> drawableCache;
    private Map<String, String> appLabels;
    private Context context;
    private PackageManager packageManager;

    private static class AppInfoProviderHolder {
        public static final AppInfoProvider INSTANCE = new AppInfoProvider();
    }

    public static AppInfoProvider getInstance() {
        return AppInfoProviderHolder.INSTANCE;
    }

    private AppInfoProvider() {
        drawableCache = new LruCache<>(CACHE_DRAWABLE_SIZE);  //不知道怎么获取 drawable 大小。。。
        appLabels = new ArrayMap<>();
        context = NowApplication.getInstance();
        packageManager = context.getPackageManager();
    }

    public String getAppLabel(String packageName) {
        String label = appLabels.get(packageName);
        if (label == null) {
            try {
                PackageInfo info = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                label = packageManager.getApplicationLabel(info.applicationInfo).toString();
                appLabels.put(packageName, label);
            } catch (PackageManager.NameNotFoundException e) {
                LogUtils.d(TAG, "package name not found: " + packageName);
            } catch (Exception e) {
                label = "";
                LogUtils.d(TAG, "get " + packageName + " label failed. exception: " + e.getMessage());
            }
        }
        return label;
    }

    public Drawable getAppIcon(String packageName) {
        Drawable drawable = drawableCache.get(packageName);
        if (drawable == null) {
            try {
                drawable = packageManager.getApplicationIcon(packageName);
                drawableCache.put(packageName, drawable);
            } catch (PackageManager.NameNotFoundException e) {
                LogUtils.d(TAG, "package name not found: " + packageName);
            } catch (Exception e) {
                LogUtils.d(TAG, "exception" + e.getMessage());
            }
        }
        return drawable;
    }

    public void clear() {
        appLabels.clear();
        drawableCache.evictAll();
    }
}
