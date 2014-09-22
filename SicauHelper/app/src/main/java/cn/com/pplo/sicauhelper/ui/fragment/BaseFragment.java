package cn.com.pplo.sicauhelper.ui.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.util.TypedValue;

import cn.com.pplo.sicauhelper.widget.PagerSlidingTabStrip;

/**
 * Created by winson on 2014/9/21.
 */
public class BaseFragment extends Fragment {
    protected  void setPagerSlidingTabStyle(PagerSlidingTabStrip pagerSlidingTabStrip, int backgroundColor){
        //tab会横向填充满屏幕
        pagerSlidingTabStrip.setShouldExpand(true);
        //文字大小
        pagerSlidingTabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
        //文字颜色
        pagerSlidingTabStrip.setTextColor(Color.parseColor("#88ffffff"));
        //指示条颜色
        pagerSlidingTabStrip.setIndicatorColor(Color.WHITE);
        //指示条高度
//        pagerSlidingTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics()));
        //背景色

        pagerSlidingTabStrip.setBackgroundColor(getResources().getColor(backgroundColor));
        //字体样式
//        pagerSlidingTabStrip.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        //分隔条颜色
        pagerSlidingTabStrip.setDividerColor(getResources().getColor(backgroundColor));
    }
}
