package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.ui.MainActivity;
import cn.com.pplo.sicauhelper.util.UIUtil;
import cn.com.pplo.sicauhelper.widget.PagerSlidingTabStrip;

public class ScoreFragment extends BaseFragment {

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private PagerSlidingTabStrip pagerSlidingTabStrip;

    public static ScoreFragment newInstance() {
        ScoreFragment fragment = new ScoreFragment();
        return fragment;
    }

    public ScoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached("成绩");
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
        setHasOptionsMenu(true);
        super.onCreateView(inflater,container, savedInstanceState);
        getSupportActionBar(getActivity()).setBackgroundDrawable(getResources().getDrawable(R.color.indigo_500));
        return inflater.inflate(R.layout.fragment_score, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUp(view);
    }

    private void setUp(final View view) {
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tab_indicator);
        setPagerSlidingTabStyle(pagerSlidingTabStrip, R.color.indigo_500);
        pagerSlidingTabStrip.setViewPager(viewPager);
        pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

//        //此处需要修改
//        Map<String, String> params = new HashMap<String, String>();
//        Student student = SicauHelperApplication.getStudent();
//        if (student != null) {
//            params.put("user", student.getSid() + "");
//            params.put("pwd", student.getPswd());
//            NetUtil.getScoreHtmlStr(getActivity(), params, new NetUtil.NetCallbcak(getActivity()) {
//                @Override
//                public void onResponse(String result) {
//                    super.onResponse(result);
//                    StringUtil.parseScoreInfo(result, new StringUtil.Callback() {
//                        @Override
//                        public void handleParseResult(List<Score> tempList) {
//                            if(tempList != null){
//                                scores.addAll(tempList);
//
//                                //此处仍将修改
//                                ContentResolver contentResolver = getActivity().getContentResolver();
//                                ContentValues[] contentValueses = new ContentValues[tempList.size()];
//                                for (int i = 0; i < contentValueses.length; i++) {
//                                    contentValueses[i] = new ContentValues();
//                                    contentValueses[i].put(TableContract.TableScore._CATEGORY, tempList.get(i).getCategory());
//                                    contentValueses[i].put(TableContract.TableScore._COURSE, tempList.get(i).getCourse());
//                                    contentValueses[i].put(TableContract.TableScore._CREDIT, tempList.get(i).getCredit());
//                                    contentValueses[i].put(TableContract.TableScore._GRADE, tempList.get(i).getGrade());
//                                    contentValueses[i].put(TableContract.TableScore._MARK, tempList.get(i).getMark());
//                                }
//                                getActivity().getContentResolver().notifyChange(Uri.parse(SicauHelperProvider.URI_SCORE_ALL), null);
//                                for(ContentValues c : contentValueses){
//                                    getActivity().getContentResolver().insert(Uri.parse(SicauHelperProvider.URI_SCORE_ALL), c);
//                                }
//                                //int i = contentResolver.bulkInsert(Uri.parse(SicauHelperProvider.URI_SCORE_ALL), contentValueses);
//                                //Log.d("winson",  "插入了" + i + "条" );
//
//                                Cursor cursor = getActivity().getContentResolver().query(Uri.parse(SicauHelperProvider.URI_SCORE_ALL), null,null,null,null);
//                                if(cursor == null){
//                                    Log.d("winson", "没有");
//                                }
//                                else {
//                                    Log.d("winson", "有" + cursor.getCount());
//                                }
//                            }
//                        }
//                    });
//                }
//
//                @Override
//                public void onErrorResponse(VolleyError volleyError) {
//                    super.onErrorResponse(volleyError);
//                }
//            });
//        }
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
            Fragment pageFragment = null;
            if(position == 0){
                pageFragment = ScoreDetailFragment.newInstance();
            }
            else {
                pageFragment = ScoreStatsFragment.newInstance();
            }
            return pageFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            if(position == 0){
                return "详情";
            }
            else {
                return "统计";
            }
        }
    }
}
