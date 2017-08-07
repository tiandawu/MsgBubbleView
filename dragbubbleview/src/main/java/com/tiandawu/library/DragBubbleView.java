package com.tiandawu.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ScrollView;

import java.util.List;


/**
 * Created by tiandawu on 2017/7/21.
 */

public class DragBubbleView extends android.support.v7.widget.AppCompatTextView {

    private int[] location = new int[2];
    private float mBubbleRadius;
    private BubbleView bubbleView;
    private int mBubbleViewColor;
    private DragBubbleViewListener mListener;
    private DragBubbleViewAdapter mAdapter;
    private ViewGroup mScrollParent;
    private final int DEFAULT_TEXT_SIZE = 39;

    public DragBubbleView(Context context) {
        super(context);
        init();
    }

    public DragBubbleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragBubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);
        if (DEFAULT_TEXT_SIZE == getTextSize()) {
            setTextSize(12);
        }
        Drawable drawable = getBackground();
        if (drawable == null) {
            setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.red_bg));
            mBubbleViewColor = Color.parseColor("#F74C31");
        } else {
            try {
                //不出现异常说明是使用的颜色背景
                ColorDrawable colorDrawable = (ColorDrawable) drawable;
                mBubbleViewColor = colorDrawable.getColor();
                Drawable mShapeDrawable = getContext().getResources().getDrawable(R.drawable.red_bg);
                mShapeDrawable.setColorFilter(mBubbleViewColor, PorterDuff.Mode.SRC_IN);
                setBackgroundDrawable(mShapeDrawable);
            } catch (Exception e) {
                e.printStackTrace();
                //出现异常说明使用的不是颜色背景
                Bitmap bitmap = drawableToBitmap(drawable);
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        //找一个颜色
                        List<Palette.Swatch> swatchList = palette.getSwatches();
                        for (Palette.Swatch swatch : swatchList) {
                            if (swatch != null) {
                                mBubbleViewColor = swatch.getRgb();
                                return;
                            }
                        }
                        //一个都没找到就默认一个颜色
                        mBubbleViewColor = Color.parseColor("#000000");
                    }
                });
            }
        }
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否在在View或者surfaceView里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBubbleRadius = Math.min(w, h) / 2;
    }


    @Override
    public void setBackgroundColor(@ColorInt int color) {
        super.setBackgroundColor(color);
        this.mBubbleViewColor = color;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View root = getRootView();
            if (root == null || !(root instanceof ViewGroup)) {
                return false;
            }
            getLocationOnScreen(location);
            mScrollParent = getScrollParent();
            if (mScrollParent != null) {
                mScrollParent.requestDisallowInterceptTouchEvent(true);
            }
            setDrawingCacheEnabled(true);
            bubbleView = new BubbleView(getContext(), this, getDrawingCache(), mScrollParent);
            bubbleView.setLayoutParams(new ViewGroup.LayoutParams(root.getWidth(), root.getHeight()));
            bubbleView.setFixBubbleCenter(location[0] + getMeasuredWidth() / 2, location[1] + getMeasuredHeight() / 2);
            bubbleView.setDragBubbleCenter(event.getRawX(), event.getRawY());
            bubbleView.setBubbleRadius(mBubbleRadius);
            bubbleView.setBubbleColor(mBubbleViewColor);
            bubbleView.setOnDragListener(mListener);
            bubbleView.setOnDragListenerAdapter(mAdapter);
            ((ViewGroup) root).addView(bubbleView);
            setVisibility(INVISIBLE);
            return true;
        }
        bubbleView.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    public void setOnDragListener(DragBubbleViewListener listener) {
        this.mListener = listener;
    }

    public void setOnDragListenerAdapter(DragBubbleViewAdapter adapter) {
        this.mAdapter = adapter;
    }

    private ViewGroup getScrollParent() {
        View p = this;
        while (true) {
            View v;
            try {
                v = (View) p.getParent();
            } catch (ClassCastException e) {
                return null;
            }
            if (v == null)
                return null;
            if (v instanceof AbsListView || v instanceof ScrollView
                    || v instanceof ViewPager || v instanceof RecyclerView || v instanceof ViewGroup) {
                return (ViewGroup) v;
            }
            p = v;
        }
    }

}
