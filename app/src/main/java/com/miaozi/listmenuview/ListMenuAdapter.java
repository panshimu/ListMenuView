package com.miaozi.listmenuview;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * created by panshimu
 * on 2019/8/23
 */
public class ListMenuAdapter extends BaseListMenuAdapter{

    private List<String> mData;
    private LayoutInflater mInflater;


    public ListMenuAdapter(Context context,List<String> mData) {
        this.mData = mData;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public View getTabView(int position, ViewGroup parent) {
        View tabView = mInflater.inflate(R.layout.ui_tab_view,parent,false);
        TextView tvTab = tabView.findViewById(R.id.tv_tab);
        tvTab.setText(mData.get(position));
        return tabView;
    }

    @Override
    public View getContentView(int position, ViewGroup parent) {
        View contentView = mInflater.inflate(R.layout.ui_content_view,parent,false);
        TextView tvContent = contentView.findViewById(R.id.tv_content);
        tvContent.setText(mData.get(position));
        tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeMenu();
            }
        });
        return contentView;
    }

    @Override
    public void contentViewClose(View tabView) {
        TextView tvTab = tabView.findViewById(R.id.tv_tab);
        tvTab.setTextColor(Color.GRAY);
    }

    @Override
    public void contentViewOpen(View tabView) {
        TextView tvTab = tabView.findViewById(R.id.tv_tab);
        tvTab.setTextColor(Color.RED);
    }
}
