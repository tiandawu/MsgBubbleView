# MsgBubbleView
这是一个模仿可拖拽的QQ小红点库，使用简单，功能强大.

使用方法：
#### 1.添加依赖
```
compile 'com.tiandawu.library:dragbubbleview:1.0.0'
```
#### 2.添加DragBubbleView
在需要使用的地方可以像使用一般自定义控件一样引入：
```
    <com.tiandawu.library.DragBubbleView
        android:id="@+id/mDragView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentRight="true"
        android:textColor="@android:color/white" />
```

#### 3.回调需要的方法：
控件支持回调如下三个方法：
```
    /**
     * 气泡消失
     */
    void onBubbleViewDismiss();

    /**
     * 气泡拖拽中
     *
     * @param dragPoint 拖拽点
     */
    void onBubbleViewDragging(PointF dragPoint);

    /**
     * 气泡还原
     */
    void onBubbleViewReset();
```

为了方便支持了两种回调方式，第一种针对需要实现所有回调方法；第二种则可以实现自己需要回调的方法。
- 3.1 回调的第一种方式：
```
    mDragView.setOnDragListener(new DragBubbleViewListener() {
            @Override
            public void onBubbleViewDismiss() {

            }

            @Override
            public void onBubbleViewDragging(PointF dragPoint) {

            }

            @Override
            public void onBubbleViewReset() {

            }
        });
```

- 3.2 回调方的第二种方式：
```
    mDragView.setOnDragListenerAdapter(new DragBubbleViewAdapter() {
            @Override
            public void onBubbleViewDismiss() {
                Toast.makeText(mContext, mDataList.get(position) + "气泡消失", Toast.LENGTH_SHORT).show();
            }
        });
```

#### 4.效果图：
![效果图](https://static.oschina.net/uploads/img/201708/13215031_OqjK.gif "效果图")