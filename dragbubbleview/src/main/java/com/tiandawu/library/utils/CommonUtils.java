package com.tiandawu.library.utils;

import android.content.Context;

/**
 * Created by tiandawu on 2017/7/21.
 */

public class CommonUtils {

    /**
     * dip 转换成 px
     */
    public static int dip2Px(Context context, int dip) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

}
