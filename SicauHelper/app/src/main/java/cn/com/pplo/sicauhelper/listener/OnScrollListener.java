package cn.com.pplo.sicauhelper.listener;

import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2014/9/25.
 */
public class OnScrollListener implements View.OnTouchListener {

    private int startX;
    private int startY;
    private ActionBar actionBar;

    public  OnScrollListener(ActionBar actionBar){
        this.actionBar = actionBar;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            startX = (int) event.getX();
            startY = (int) event.getY();
        }
        else if(event.getAction() == MotionEvent.ACTION_UP){

            int endX = (int) event.getX();
            int endY = (int) event.getY();
            Log.d("winson", (startY - endY) + "差距");
            if((endY - startY) > 10 && (endY - startY) > (endX - startX)){
                if(!actionBar.isShowing()){
                    actionBar.show();
                }
            }
            else if((startY - endY) > 10 && (startY - endY) > (startX - endX)){
                if(actionBar.isShowing()){
                    actionBar.hide();
                }
            }
        }
        return false;
    }
}
