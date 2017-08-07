package com.tiandawu.msgbubbleview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.tiandawu.library.DragBubbleView;
import com.tiandawu.library.DragBubbleViewAdapter;

import java.util.List;
import java.util.Random;

/**
 * Created by tiandawu on 2017/7/26.
 */

public class MyAdapter extends BaseAdapter {
    private List<String> mDataList;
    private Context mContext;
    private Random random;

    public MyAdapter(Context context, List<String> mDataList) {
        this.mContext = context;
        this.mDataList = mDataList;
        random = new Random();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list, parent, false);
        }
        ViewHolder holder = ViewHolder.getViewHolder(convertView);

        holder.mItemText.setText(mDataList.get(position));
        holder.mDragView.setText(random.nextInt(100)+"");
        holder.mDragView.setOnDragListenerAdapter(new DragBubbleViewAdapter() {
            @Override
            public void onBubbleViewDismiss() {
                Toast.makeText(mContext, mDataList.get(position) + "气泡消失", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }


    private static class ViewHolder {
        private TextView mItemText;
        private DragBubbleView mDragView;

        private ViewHolder(View convertView) {
            mItemText = (TextView) convertView.findViewById(R.id.list_item);
            mDragView = (DragBubbleView) convertView.findViewById(R.id.mDragView);
        }

        public static ViewHolder getViewHolder(View convertView) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            return holder;
        }
    }
}

