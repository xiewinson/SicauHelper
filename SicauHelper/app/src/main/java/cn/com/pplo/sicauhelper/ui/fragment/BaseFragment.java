package cn.com.pplo.sicauhelper.ui.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import cn.com.pplo.sicauhelper.listener.OnScrollListener;
import cn.com.pplo.sicauhelper.util.UIUtil;
import cn.com.pplo.sicauhelper.widget.PagerSlidingTabStrip;

/**
 * Created by winson on 2014/9/21.
 */
public class BaseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(!UIUtil.getSupportActionBar(getActivity()).isShowing()){
            UIUtil.getSupportActionBar(getActivity()).show();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

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
        pagerSlidingTabStrip.setBackgroundResource(backgroundColor);
        //指示条
//        pagerSlidingTabStrip.setUnderlineHeight(0);
//        pagerSlidingTabStrip.setUnderlineColorResource(backgroundColor);
        //字体样式
//        pagerSlidingTabStrip.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        //分隔条颜色
        pagerSlidingTabStrip.setDividerColor(getResources().getColor(backgroundColor));
    }

    protected void setScrollHideOrShowActionBar(final ListView listView){
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if(visibleItemCount != 0 && ((totalItemCount - 2) > visibleItemCount)){
                    ;
                    listView.setOnTouchListener(new OnScrollListener(UIUtil.getSupportActionBar(getActivity())));
                    listView.setOnScrollListener(null);
                }
            }
        });
    }
}
