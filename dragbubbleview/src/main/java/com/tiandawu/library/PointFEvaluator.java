package com.tiandawu.library;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * Created by tiandawu on 2017/7/13.
 */

public class PointFEvaluator implements TypeEvaluator<PointF> {

    private PointF mPoint;

    public PointFEvaluator() {

    }

    public PointFEvaluator(PointF pointF) {
        this.mPoint = pointF;
    }

    @Override
    public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
        float x = startValue.x + (endValue.x - startValue.x) * fraction;
        float y = startValue.y + (endValue.y - startValue.y) * fraction;

        if (mPoint != null) {
            mPoint.set(x, y);
            return mPoint;
        } else {
            return new PointF(x, y);
        }
    }
}
