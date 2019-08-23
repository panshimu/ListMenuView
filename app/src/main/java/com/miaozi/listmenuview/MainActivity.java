package com.miaozi.listmenuview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListMenuView mListMenuView;
    private List<String> mData;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addData();
        mListMenuView = findViewById(R.id.list_menu_view);
        ListMenuAdapter listMenuAdapter = new ListMenuAdapter(this, mData);
        mListMenuView.setAdapter(listMenuAdapter);
     }

    private void addData() {
        mData = new ArrayList<>();
        mData.add("推荐");
        mData.add("热点");
        mData.add("关注");
        mData.add("视频");
        mData.add("游戏");
    }
}
