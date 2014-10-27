package cn.com.pplo.sicauhelper.listener;

import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;

/**
 * Created by Administrator on 2014/9/25.
 */
public class OnScrollHideOrShowActionBarListener implements AbsListView.OnScrollListener {

    private int lastVisibleItem = 0;
    private ActionBar actionBar;

    public OnScrollHideOrShowActionBarListener(ActionBar actionBar) {
        this.actionBar = actionBar;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(firstVisibleItem > lastVisibleItem){
            if(actionBar.isShowing()){
                actionBar.hide();
            }
        }
        else if(firstVisibleItem < lastVisibleItem){
            if(!actionBar.isShowing()){
                actionBar.show();
            }
        }
        lastVisibleItem = firstVisibleItem;
    }
}
