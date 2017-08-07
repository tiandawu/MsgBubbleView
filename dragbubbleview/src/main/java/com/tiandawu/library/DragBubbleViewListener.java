package com.tiandawu.library;

import android.graphics.PointF;

/**
 * Created by tiandawu on 2017/7/25.
 */

public interface DragBubbleViewListener {
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
}
