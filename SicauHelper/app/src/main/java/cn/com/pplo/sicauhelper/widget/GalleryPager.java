package cn.com.pplo.sicauhelper.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by winson on 2014/11/21.
 */
public class GalleryPager extends ViewPager {
    private boolean isLocked;
    public GalleryPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        isLocked = false;
    }
    public GalleryPager(Context context){
        super(context);
        isLocked = false;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isLocked) {
            try {
                return super.onInterceptTouchEvent(ev);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isLocked) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    public void toggleLock() {
        isLocked = !isLocked;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public boolean isLocked() {
        return isLocked;
    }
}
