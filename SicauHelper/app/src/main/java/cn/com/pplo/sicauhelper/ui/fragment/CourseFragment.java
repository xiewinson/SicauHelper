package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.model.Course;
import cn.com.pplo.sicauhelper.model.Student;
import cn.com.pplo.sicauhelper.service.CourseService;
import cn.com.pplo.sicauhelper.ui.MainActivity;
import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;
import cn.com.pplo.sicauhelper.widget.PagerSlidingTabStrip;

public class CourseFragment extends BaseFragment {

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private PagerSlidingTabStrip pagerSlidingTabStrip;

    public static CourseFragment newInstance() {
        CourseFragment fragment = new CourseFragment();
        return fragment;
    }

    public CourseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        ((MainActivity) activity).onSectionAttached("课程表");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        UIUtil.getSupportActionBar(getActivity()).setBackgroundDrawable(getResources().getDrawable(R.color.green_500));
        return inflater.inflate(R.layout.fragment_course, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUp(view);
    }

    private void setUp(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tab_indicator);
        setPagerSlidingTabStyle(pagerSlidingTabStrip, R.color.green_500);
        pagerSlidingTabStrip.setViewPager(viewPager);
        pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int position) {
                ActionBar actionBar = UIUtil.getSupportActionBar(getActivity());
                if(actionBar.isShowing() == false){
                    actionBar.show();
                }
                int color = 0;
                switch (position){
                    case 0:
                        color = R.color.green_500;
                        break;
                    case 1:
                        color = R.color.deep_purple_500;
                        break;
                    case 2:
                        color = R.color.red_500;
                        break;
                    case 3:
                        color = R.color.blue_500;
                        break;
                    case 4:
                        color = R.color.teal_500;
                        break;
                    case 5:
                        color = R.color.deep_orange_500;
                        break;
                    case 6:
                        color = R.color.brown_500;
                        break;
                }
                actionBar.setBackgroundDrawable(getResources().getDrawable(color));
                setPagerSlidingTabStyle(pagerSlidingTabStrip, color);
            }
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        //根据星期几选定课程表
        Calendar calendar = Calendar.getInstance();
        int date = 0;
        switch (calendar.get(Calendar.DAY_OF_WEEK)){
            case Calendar.MONDAY:
                date = 0;
                break;
            case Calendar.TUESDAY:
                date = 1;
                break;
            case Calendar.WEDNESDAY:
                date = 2;
                break;
            case Calendar.THURSDAY:
                date = 3;
                break;
            case Calendar.FRIDAY:
                date = 4;
                break;
            case Calendar.SATURDAY:
                date = 5;
                break;
            case Calendar.SUNDAY:
                date = 6;
                break;
        };
        viewPager.setCurrentItem(date);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment pageFragment = CourseDateFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            pageFragment.setArguments(bundle);
            return pageFragment;
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            if(position == 0){
                return "星期一";
            }
            else if(position == 1){
                return "星期二";
            }
            if(position == 2){
                return "星期三";
            }
            else if(position == 3){
                return "星期四";
            }
            if(position == 4){
                return "星期五";
            }
            else if(position == 5){
                return "星期六";
            }
            else {
                return "星期天";
            }
        }
    }
}
