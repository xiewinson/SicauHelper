package cn.com.pplo.sicauhelper.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by winson on 2014/9/14.
 */
public class UIUtil {
    //显示短Toast
    public static void showShortToast(Context context, String title){
        Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
    };
}
