package com.yyw.android.bestnow.archframework;

/**
 * Created by yangyongwen on 16/11/19.
 */

public interface BasePresenter {
    void start();

    void onResume();

    void onPause();

    void onDestroy();
}
