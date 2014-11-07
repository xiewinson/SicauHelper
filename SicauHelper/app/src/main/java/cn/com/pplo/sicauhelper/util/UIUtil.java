package cn.com.pplo.sicauhelper.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.listener.OnScrollHideOrShowActionBarListener;
import cn.com.pplo.sicauhelper.listener.OnScrollHideOrShowActionBarListener2;
import cn.com.pplo.sicauhelper.ui.fragment.ProgressFragment;

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
    public static AlertDialog getProgressDialog(Context context, String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.progress_dialog, null);
        TextView tv = (TextView) view.findViewById(R.id.progress_dialog_tv);
        if(!TextUtils.isEmpty(text)) {
            tv.setText(text);
        }
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static void dismissProgressDialog(AlertDialog progressDialog){
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.cancel();
        }
    }

    /**
     * 
     * @param context
     * @param text
     * @return
     */
    public static ProgressFragment getProgressFragment(Context context, String text){
        ProgressFragment progressFragment = ProgressFragment.newInstance(text);
        progressFragment.setCancelable(false);
        return progressFragment;
    }

    public static void dismissProgressFragment(ProgressFragment progressDialog){
        if(progressDialog != null ){
            progressDialog.dismiss();
        }
    }

    /**
     * 设置actionbar颜色
     * @param context
     * @param actionBarColor
     * @param resId
     */
    public static void setActionBarColor(Context context, ActionBar actionBarColor, int resId){
        actionBarColor.setBackgroundDrawable(context.getResources().getDrawable(resId));
    }

    public static void setListViewInitAnimation(String type, AbsListView absListView, BaseAdapter baseAdapter) {
        AnimationAdapter animationAdapter = null;
        if(type == "bottom") {
            animationAdapter = new SwingBottomInAnimationAdapter(baseAdapter);
        }
        else if(type == "left") {
            animationAdapter = new SwingLeftInAnimationAdapter(baseAdapter);
        }
        else if(type == "right") {
            animationAdapter = new SwingRightInAnimationAdapter(baseAdapter);
        }

        animationAdapter.setAbsListView(absListView);
        absListView.setAdapter(animationAdapter);
    }

    /**
     * 设置滑动监听隐藏/显示actionBar
     */
    public static void setListViewScrollHideOrShowActionBar(Context context, ListView listView, ActionBar actionBar) {
        if(listView.getMaxScrollAmount() > 0) {
            listView.setOnScrollListener(new OnScrollHideOrShowActionBarListener(actionBar));
        }
    }

    /**
     * dp转换px
     * @param context
     * @param dp
     * @return
     */
    public static float parseDpToPx(Context context, int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转换px
     * @param context
     * @param sp
     * @return
     */
    public static float parseSpToPx(Context context, int sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
}
