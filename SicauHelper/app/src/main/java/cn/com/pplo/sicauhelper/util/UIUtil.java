package cn.com.pplo.sicauhelper.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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

    /**
     * 
     * @param context
     * @param text
     * @return
     */
    public static ProgressDialog getProgressDialog(Context context, String text){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(text);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    public static void dismissProgressDialog(ProgressDialog progressDialog){
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    public static void setActionBarColor(Context context, ActionBar actionBarColor, int resId){
        actionBarColor.setBackgroundDrawable(context.getResources().getDrawable(resId));
    }
}
