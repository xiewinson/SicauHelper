package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.ui.AddGoodsActivity;
import cn.com.pplo.sicauhelper.ui.MainActivity;
import cn.com.pplo.sicauhelper.widget.PagerSlidingTabStrip;
import cn.com.pplo.sicauhelper.widget.ZoomOutPageTransformer;

public class MarketFragment extends BaseFragment {

    private ViewPager viewPager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private ViewPagerAdapter viewPagerAdapter;

    private String[] schoolArray;

    public static MarketFragment newInstance() {
        MarketFragment fragment = new MarketFragment();
        return fragment;
    }

    public MarketFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getResources().getString(R.string.title_market));
        schoolArray = getResources().getStringArray(R.array.school);
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
        return inflater.inflate(R.layout.fragment_market, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUp(getActivity(), view);
    }

    private void setUp(Context context, View view) {
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        pagerSlidingTabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tab_indicator);
        getSupportActionBar(context).setBackgroundDrawable(getResources().getDrawable(R.color.blue_500));
        setPagerSlidingTabStyle(pagerSlidingTabStrip, R.color.blue_700);
        viewPager.setAdapter(viewPagerAdapter);
        pagerSlidingTabStrip.setViewPager(viewPager);

        pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int position) {
                ActionBar actionBar = getSupportActionBar(getActivity());
                if(actionBar.isShowing() == false){
                    actionBar.show();
                }
                int color = 0;
                int tabColor = 0;
                switch (position){
                    case 0:
                        color = R.color.blue_500;
                        tabColor = R.color.blue_700;
                        break;
                    case 1:
                        color = R.color.orange_500;
                        tabColor = R.color.orange_700;
                        break;
                    case 2:
                        color = R.color.green_500;
                        tabColor = R.color.green_700;
                        break;
                }
                actionBar.setBackgroundDrawable(getResources().getDrawable(color));
                setPagerSlidingTabStyle(pagerSlidingTabStrip, tabColor);
            }
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        //选中当前登录用户校区
        viewPager.setCurrentItem(SicauHelperApplication.getStudent(context).getSchool())     ;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.market, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_add) {
            AddGoodsActivity.startNewGoodsActivity(getActivity());
        }
        return super.onOptionsItemSelected(item);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return SchoolMarketFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return schoolArray[position];
        }
    }
}
