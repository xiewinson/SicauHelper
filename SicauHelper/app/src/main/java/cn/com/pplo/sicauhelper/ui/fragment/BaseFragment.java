package cn.com.pplo.sicauhelper.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import cn.com.pplo.sicauhelper.widget.PagerSlidingTabStrip;
import cn.com.pplo.sicauhelper.widget.ViewPadding;

/**
 * Created by winson on 2014/9/21.
 */
public class BaseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(!getSupportActionBar(getActivity()).isShowing()){
            getSupportActionBar(getActivity()).show();
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
        pagerSlidingTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics()));
        //背景色
        pagerSlidingTabStrip.setBackgroundResource(backgroundColor);
        //最下面的分隔条
        pagerSlidingTabStrip.setUnderlineHeight(0);
        pagerSlidingTabStrip.setUnderlineColorResource(backgroundColor);
        //字体样式
//        pagerSlidingTabStrip.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        //分隔条颜色
        pagerSlidingTabStrip.setDividerColor(getResources().getColor(backgroundColor));
    }

    /**
     * 取得v7包的actionBar
     * @param context
     * @return
     */
    public ActionBar getSupportActionBar(Context context) {
        return ((ActionBarActivity)context).getSupportActionBar();
    }

    /**
     * listView上下补点间距
     * @param listView
     */
    public void setListViewTopBottomPadding(ListView listView){
        View paddingTv = ViewPadding.getListViewPadding(getActivity());
        listView.addHeaderView(paddingTv);
        listView.addFooterView(paddingTv);
    }
}
