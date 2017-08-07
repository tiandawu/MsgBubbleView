package com.tiandawu.msgbubbleview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private List<String> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        mListView = (ListView) findViewById(R.id.mList);
        mListView.setAdapter(new MyAdapter(this, mDataList));
    }

    private void initData() {
        mDataList = new ArrayList<String>();
        for (int i = 0; i < 50; i++) {
            mDataList.add("list item " + i);
        }
    }
}
