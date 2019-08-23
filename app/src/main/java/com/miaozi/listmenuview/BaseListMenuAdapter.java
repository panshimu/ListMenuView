package com.miaozi.listmenuview;

import android.view.View;
import android.view.ViewGroup;

/**
 * created by panshimu
 * on 2019/8/23
 */
public abstract class BaseListMenuAdapter {
    private BaseMenuObserver mObserver;
    public void registerObserver(BaseMenuObserver menuObserver){
        this.mObserver = menuObserver;
    }
    public void unRegisterObserver(BaseMenuObserver menuObserver){
        this.mObserver = null;
    }
    public void closeMenu(){
        if(mObserver != null){
            mObserver.closeMenu();
        }
    }

    public abstract int getCount();
    public abstract View getTabView(int position, ViewGroup parent);
    public abstract View getContentView(int position,ViewGroup parent);
    public abstract void contentViewClose(View tabView);
    public abstract void contentViewOpen(View tabView);
}
