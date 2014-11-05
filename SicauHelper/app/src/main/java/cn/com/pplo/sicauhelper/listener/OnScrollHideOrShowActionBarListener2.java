package cn.com.pplo.sicauhelper.listener;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;

import cn.com.pplo.sicauhelper.util.UIUtil;

/**
 * Created by Administrator on 2014/9/25.
 */
public class OnScrollHideOrShowActionBarListener2 implements View.OnTouchListener {

    private Context context;
    private ActionBar actionBar;
    private int startX;
    private int startY;

    public OnScrollHideOrShowActionBarListener2(Context context, ActionBar actionBar) {
        this.actionBar = actionBar;
        this.context = context;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            startX = (int) event.getX();
            startY = (int) event.getY();
        }
        else if(event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_UP) {
            int endX = (int) event.getX();
            int endY = (int) event.getY();
            if((endY - startY) > 5) {
                if(!actionBar.isShowing()) {
                    actionBar.show();
                }
            }
            else if((startY - endY) >  UIUtil.parseDpToPx(context, 56)) {
                if(actionBar.isShowing()) {
                    actionBar.hide();
                }
            }
        }
        return false;
    }
}
