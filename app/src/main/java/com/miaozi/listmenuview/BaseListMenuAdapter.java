package com.miaozi.listmenuview;

import android.view.View;
import android.view.ViewGroup;

/**
 * created by panshimu
 * on 2019/8/23
 */
public abstract class BaseListMenuAdapter {
    public abstract int getCount();
    public abstract View getTabView(int position, ViewGroup parent);
    public abstract View getContentView(int position,ViewGroup parent);
    public abstract void contentViewClose(View tabView);
    public abstract void contentViewOpen(View tabView);
}
