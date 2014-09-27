package cn.com.pplo.sicauhelper.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import cn.com.pplo.sicauhelper.R;

/**
 * Created by winson on 2014/9/14.
 */
public class UIUtil {
    /**显示短Toast
     *
     * @param context
     * @param title
     */
    public static void showShortToast(Context context, String title) {
        Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
    }
}
