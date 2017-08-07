package com.tiandawu.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.tiandawu.library.utils.CommonUtils;


/**
 * Created by tiandawu on 2017/7/21.
 */

public class BubbleView extends View {

    private float mBubbleRadius;
    private int mBubbleColor;
    private float mDistOfBubble;
    private float mFixBubbleRadius;
    private float mMaxDragDistance;
    private PointF mFixBubbleCenter;
    private PointF mDragBubbleCenter;
    private Paint mBubblePaint;
    private BubbleState mBubbleState;
    private Bitmap mCacheBitmap;
    private RectF mBurstRect;
    private Path mBezierPath;
    private DragBubbleView mDragBubbleView;
    private Bitmap[] mBitmapArray;//存放爆炸动画的bitmap
    private int mCurrentBitmapIndex;//当前爆炸动画图片的资源id
    private int[] mDrawableId = {R.drawable.dismiss1, R.drawable.dismiss2,
            R.drawable.dismiss3, R.drawable.dismiss4, R.drawable.dismiss5};
    private DragBubbleViewListener mListener;
    private DragBubbleViewAdapter mListenerAdapter;
    private ViewGroup mScrollParent;
    private float mBurstRectOffset;
    private float mCacheBitmapOffsetWidth;
    private float mCacheBitmapOffsetHeight;
    private float mFraction;
    private FloatEvaluator mFloatEvaluator;
    private float mMinFixRadius;

    public BubbleView(Context context, DragBubbleView dragBubbleView, Bitmap cacheBitmap, ViewGroup mScrollParent) {
        super(context);
        this.mCacheBitmap = cacheBitmap;
        this.mDragBubbleView = dragBubbleView;
        this.mScrollParent = mScrollParent;
        init();
    }

    private void init() {
        mBubbleState = BubbleState.CONNECT;
        mBubbleRadius = CommonUtils.dip2Px(getContext(), 10);
        mMaxDragDistance = 6 * mBubbleRadius;
        mMinFixRadius = 2 * mBubbleRadius / 5;
        mBurstRectOffset = (float) (1.5 * mBubbleRadius);
        mBubbleColor = Color.parseColor("#F74C31");//默认颜色
        mFixBubbleCenter = new PointF();
        mDragBubbleCenter = new PointF();
        mBubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBubblePaint.setDither(true);
        mBubblePaint.setColor(mBubbleColor);
        mBezierPath = new Path();
        mBurstRect = new RectF();
        mCacheBitmapOffsetWidth = mCacheBitmap.getWidth() / 2;
        mCacheBitmapOffsetHeight = mCacheBitmap.getHeight() / 2;
        mBitmapArray = new Bitmap[mDrawableId.length];
        for (int i = 0; i < mDrawableId.length; i++) {
            mBitmapArray[i] = BitmapFactory.decodeResource(getResources(), mDrawableId[i]);
        }
        mFloatEvaluator = new FloatEvaluator();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mBubbleState == BubbleState.DISMISS) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                mDragBubbleCenter.set(event.getRawX(), event.getRawY());
                mDistOfBubble = (float) Math.hypot(mDragBubbleCenter.x - mFixBubbleCenter.x,
                        mDragBubbleCenter.y - mFixBubbleCenter.y);
                if (mBubbleState == BubbleState.CONNECT) {
                    if (mDistOfBubble < mMaxDragDistance) {
                        mFraction = mDistOfBubble / mMaxDragDistance;
                        mFixBubbleRadius = mFloatEvaluator.evaluate(mFraction, mBubbleRadius, mMinFixRadius);
                    } else {
                        mBubbleState = BubbleState.DISCONNECT;
                    }
                }
                if (mListener != null) {
                    mListener.onBubbleViewDragging(mDragBubbleCenter);
                }
                if (mListenerAdapter != null) {
                    mListenerAdapter.onBubbleViewDragging(mDragBubbleCenter);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (mScrollParent != null) {
                    mScrollParent.requestDisallowInterceptTouchEvent(false);
                }
                if (mBubbleState == BubbleState.CONNECT) {
                    startBubbleRestAnim();
                } else if (mBubbleState == BubbleState.DISCONNECT) {
                    if (mDistOfBubble < mMaxDragDistance) {
                        startBubbleRestAnim();
                    } else {
                        startBubbleDismissAnim();
                    }
                }
                break;
        }
        return true;
    }

    private void startBubbleRestAnim() {
        ValueAnimator anim = ValueAnimator.ofObject(new PointFEvaluator(),
                new PointF(mDragBubbleCenter.x, mDragBubbleCenter.y),
                new PointF(mFixBubbleCenter.x, mFixBubbleCenter.y));
        anim.setDuration(300);
        anim.setInterpolator(new OvershootInterpolator(5f));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDragBubbleCenter = (PointF) animation.getAnimatedValue();
                mDistOfBubble = (float) Math.hypot(mDragBubbleCenter.x - mFixBubbleCenter.x,
                        mDragBubbleCenter.y - mFixBubbleCenter.y);
                mFraction = mDistOfBubble / mMaxDragDistance;
                mFixBubbleRadius = mFloatEvaluator.evaluate(mFraction, mBubbleRadius, mMinFixRadius);
                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mFixBubbleRadius = mBubbleRadius;
                mBubbleState = BubbleState.CONNECT;
                mDragBubbleView.setVisibility(VISIBLE);
                if (BubbleView.this.getParent() != null) {
                    ((ViewGroup) BubbleView.this.getParent()).removeView(BubbleView.this);
                }
                if (mListener != null) {
                    mListener.onBubbleViewReset();
                }
                if (mListenerAdapter != null) {
                    mListenerAdapter.onBubbleViewReset();
                }
            }
        });
        anim.start();
    }

    private void startBubbleDismissAnim() {
        mBubbleState = BubbleState.DISMISS;
        ValueAnimator anim = ValueAnimator.ofInt(0, mDrawableId.length - 1);
        anim.setDuration(800);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentBitmapIndex = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (BubbleView.this.getParent() != null) {
                    ((ViewGroup) BubbleView.this.getParent()).removeView(BubbleView.this);
                }
                if (mListener != null) {
                    mListener.onBubbleViewDismiss();
                }
                if (mListenerAdapter != null) {
                    mListenerAdapter.onBubbleViewDismiss();
                }
            }
        });
        anim.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mBubbleState) {
            case CONNECT:
                //1.画固定气泡
                canvas.drawCircle(mFixBubbleCenter.x, mFixBubbleCenter.y, mFixBubbleRadius, mBubblePaint);
                Log.e("tt", "x = " + mFixBubbleCenter.x + "   y = " + mFixBubbleCenter.y + "    radius = " + mFixBubbleRadius);
                //2.画连接线
                //获取控制点
                PointF mControlPoint = new PointF((mFixBubbleCenter.x + mDragBubbleCenter.x) / 2,
                        (mFixBubbleCenter.y + mDragBubbleCenter.y) / 2);
                //获取切点
                PointF[] mFixContactPoints = getContactPoints(mFixBubbleCenter, mFixBubbleRadius, mDistOfBubble);
                PointF[] mDragContactPoints = getContactPoints(mDragBubbleCenter, mBubbleRadius, mDistOfBubble);
                //画path
                mBezierPath.reset();
                mBezierPath.moveTo(mFixContactPoints[0].x, mFixContactPoints[0].y);
                mBezierPath.quadTo(mControlPoint.x, mControlPoint.y, mDragContactPoints[0].x, mDragContactPoints[0].y);
                mBezierPath.lineTo(mDragContactPoints[1].x, mDragContactPoints[1].y);
                mBezierPath.quadTo(mControlPoint.x, mControlPoint.y, mFixContactPoints[1].x, mFixContactPoints[1].y);
                mBezierPath.close();
                canvas.drawPath(mBezierPath, mBubblePaint);
                //3.画拖拽气泡
                canvas.drawBitmap(mCacheBitmap, mDragBubbleCenter.x - mCacheBitmapOffsetWidth,
                        mDragBubbleCenter.y - mCacheBitmapOffsetHeight, mBubblePaint);
                break;
            case DISCONNECT:
                //画拖拽的缓存bitmap
                canvas.drawBitmap(mCacheBitmap, mDragBubbleCenter.x - mCacheBitmapOffsetWidth,
                        mDragBubbleCenter.y - mCacheBitmapOffsetHeight, mBubblePaint);
                break;
            case DISMISS:
                //画爆炸动画
                mBurstRect.set(mDragBubbleCenter.x - mBurstRectOffset, mDragBubbleCenter.y - mBurstRectOffset,
                        mDragBubbleCenter.x + mBurstRectOffset, mDragBubbleCenter.y + mBurstRectOffset);
                canvas.drawBitmap(mBitmapArray[mCurrentBitmapIndex], null, mBurstRect, mBubblePaint);
                break;
        }
    }

    /**
     * 获取贝塞尔曲线与园的切点
     *
     * @param mPoint        圆心
     * @param mRadius       半径
     * @param mDistOfBubble 两个圆心的距离
     */
    private PointF[] getContactPoints(PointF mPoint, float mRadius, float mDistOfBubble) {
        float cosTheta = (mDragBubbleCenter.x - mFixBubbleCenter.x) / mDistOfBubble;
        float sinTheta = (mDragBubbleCenter.y - mFixBubbleCenter.y) / mDistOfBubble;
        PointF[] mPoints = new PointF[2];
        mPoints[0] = new PointF(mPoint.x - mRadius * sinTheta, mPoint.y + mRadius * cosTheta);
        mPoints[1] = new PointF(mPoint.x + mRadius * sinTheta, mPoint.y - mRadius * cosTheta);
        return mPoints;
    }

    public void setFixBubbleCenter(float x, float y) {
        mFixBubbleCenter.set(x, y);
    }

    public void setDragBubbleCenter(float x, float y) {
        mDragBubbleCenter.set(x, y);
    }

    public BubbleState getBubbleState() {
        return mBubbleState;
    }

    public void setBubbleState(BubbleState mBubbleState) {
        this.mBubbleState = mBubbleState;
    }

    public float getBubbleRadius() {
        return mBubbleRadius;
    }

    public void setBubbleRadius(float mBubbleRadius) {
        this.mBubbleRadius = mBubbleRadius;
    }

    public int getBubbleColor() {
        return mBubbleColor;
    }

    public void setBubbleColor(int mBubbleColor) {
        this.mBubbleColor = mBubbleColor;
        mBubblePaint.setColor(mBubbleColor);
    }

    public void setOnDragListener(DragBubbleViewListener mListener) {
        this.mListener = mListener;
    }

    public void setOnDragListenerAdapter(DragBubbleViewAdapter mListenerAdapter) {
        this.mListenerAdapter = mListenerAdapter;
    }

    /**
     * 气泡状态
     */
    public enum BubbleState {

        /**
         * 相连状态
         */
        CONNECT,
        /**
         * 断开状态
         */
        DISCONNECT,

        /**
         * 消失状态
         */
        DISMISS
    }
}
