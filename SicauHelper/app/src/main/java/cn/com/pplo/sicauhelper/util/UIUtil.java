package cn.com.pplo.sicauhelper.util;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.listener.OnScrollHideOrShowActionBarListener;

/**
 * Created by winson on 2014/9/14.
 */
public class UIUtil {

    public static final String LISTVIEW_ANIM_BOTTOM = "bottom";

    public static Toast toast;
    private static Object lockObj = new Object();

    /**显示短Toast
     *
     * @param context
     * @param title
     */
    public static void showShortToast(Context context, String title) {
        if(toast == null) {
            synchronized (lockObj) {
                if(toast == null && context != null) {
                    toast = new Toast(context);
                }
            }
        }
        try {
            View toastView = View.inflate(context, R.layout.textview_toast, null);
            TextView textView = (TextView) toastView.findViewById(R.id.toast_textview);
            textView.setText(title);
            toast.setView(toastView);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * 设置actionbar颜色
     * @param context
     * @param actionBarColor
     * @param resId
     */
    public static void setActionBarColor(Context context, ActionBar actionBarColor, int resId){
        actionBarColor.setBackgroundDrawable(context.getResources().getDrawable(resId));
    }
        /**
     * 设置actionbar颜色根据校区
     * @param context
     * @param actionBarColor
     */
    public static void setActionBarColorBySchool(Context context, int school, ActionBar actionBarColor){

        int color = 0;
        if (school == 0) {
            color = R.color.red_500;
        } else if (school == 1) {
            color = R.color.orange_500;
        } else if (school == 2){
            color = R.color.green_500;
        } else {
            color = R.color.color_primary_500;
        }

        UIUtil.setActionBarColor(context, actionBarColor, color);
    }

    /**
     * 初始化fab
     * @param context
     * @param fab
     * @param listView
     * @param normalColor
     * @param pressColor
     * @param rippleColor
     * @param listener
     */
    public static void initFab(final Context context, FloatingActionButton fab, ListView listView, int normalColor, int pressColor, int rippleColor, View.OnClickListener listener, FloatingActionButton.FabOnScrollListener fabOnScrollListener) {

        fab.setColorNormalResId(normalColor);
        fab.setColorPressedResId(pressColor);
        fab.setColorRippleResId(rippleColor);
        fab.attachToListView(listView, fabOnScrollListener);
        fab.setOnClickListener(listener);
    }


    /**
     * 设置listView动画
     * @param type
     * @param absListView
     * @param baseAdapter
     */
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
//        if(listView.getMaxScrollAmount() > 0) {
            listView.setOnScrollListener(new OnScrollHideOrShowActionBarListener(actionBar));
//        }
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

    /**
     * 隐藏软键盘
     * @param context
     * @param view
     */
    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
