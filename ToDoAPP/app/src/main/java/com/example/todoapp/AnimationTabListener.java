package com.example.todoapp;

import android.content.Context;
import android.view.View;
import android.widget.TabHost;

public class AnimationTabListener implements TabHost.OnTabChangeListener {

    private Context mContext;
    private TabHost mTabHost;
    private View mPreView;
    private View mCurrView;
    private static final int mANIMATION_TIME = 240;

    public AnimationTabListener(Context context, TabHost tabHost){
        mContext = context;
        mTabHost = tabHost;
        mPreView = mTabHost.getCurrentView();
    }

    @Override
    public void onTabChanged(String s) {

    }
}
