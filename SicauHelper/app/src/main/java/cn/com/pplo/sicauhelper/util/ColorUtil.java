package cn.com.pplo.sicauhelper.util;

import android.content.Context;

import cn.com.pplo.sicauhelper.R;

/**
 * Created by winson on 2014/11/25.
 */
public class ColorUtil {
    /**
     * 根据校区取得颜色
     * @param context
     * @param school
     * @return
     */
    public static int getColorBySchool(Context context, int school) {
        int color = 0;
        if (school == 0) {
            color = R.color.red_500;
        } else if (school == 1) {
            color = R.color.orange_500;
        } else {
            color = R.color.green_500;
        }
        return color;
    }
}
