package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.model.Course;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.service.SaveIntentService;
import cn.com.pplo.sicauhelper.ui.CourseActivity;
import cn.com.pplo.sicauhelper.ui.MainActivity;
import cn.com.pplo.sicauhelper.ui.adapter.CourseAdapter;
import cn.com.pplo.sicauhelper.util.CursorUtil;
import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.SharedPreferencesUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;
import cn.com.pplo.sicauhelper.widget.PagerSlidingTabStrip;
import cn.com.pplo.sicauhelper.widget.ZoomOutPageTransformer;

public class CourseFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int TYPE_COURSE_THEORY = 111;
    public static final int TYPE_COURSE_LAB = 222;
    public static final String TYPE_COURSE = "courseType";
    private int type = 0;

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private AlertDialog progressDialog;

    public static CourseFragment newInstance(int type) {
        CourseFragment fragment = new CourseFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE_COURSE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    public CourseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        type = getArguments().getInt(TYPE_COURSE);
        if(type == TYPE_COURSE_THEORY) {
            ((MainActivity) activity).onSectionAttached("理论课表");
        }
        else if(type == TYPE_COURSE_LAB) {
            ((MainActivity) activity).onSectionAttached("实验课表");
        }
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

        return inflater.inflate(R.layout.fragment_course, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUp(view);
    }

    private void setUp(View view) {
        progressDialog = UIUtil.getProgressDialog(getActivity(), "我正在从教务系统帮你找课表");
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tab_indicator);

        //设置actionBar颜色
        UIUtil.setActionBarColor(getActivity(), getSupportActionBar(getActivity()), R.color.color_primary_500);
//        int color = 0;
//        int tabColor = 0;
//        switch (SicauHelperApplication.getStudent().getInt(TableContract.TableUser._SCHOOL)){
//            case 0:
//                color = R.color.blue_500;
//                tabColor = R.color.blue_500;
//                break;
//            case 1:
//                color = R.color.orange_500;
//                tabColor = R.color.orange_500;
//                break;
//            case 2:
//                color = R.color.green_500;
//                tabColor = R.color.green_500;
//                break;
//        }
//        UIUtil.setActionBarColor(getActivity(), getSupportActionBar(getActivity()), color);
        setPagerSlidingTabStyle(pagerSlidingTabStrip, R.color.color_primary_500);
        getLoaderManager().initLoader(0, null, this);
    }

    private void initViewPager(Context context, List<List<Course>> data) {
        viewPagerAdapter = new ViewPagerAdapter(context, data);
        //page切换动画
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setAdapter(viewPagerAdapter);
        pagerSlidingTabStrip.setViewPager(viewPager);

        //根据星期几选定课程表
        Calendar calendar = Calendar.getInstance();
        int date = 0;
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
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
        }
        viewPager.setCurrentItem(date);
        final int finalDate = date;
        pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int position) {
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTime(new Date());
//                calendar.add(Calendar.DATE, position - finalDate);
//                getSupportActionBar(getActivity()).setTitle((calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日");
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            requestCourseList(getActivity());
        }
        return super.onOptionsItemSelected(item);
    }

    private class ViewPagerAdapter extends PagerAdapter {

        private Context context;
        private List<List<Course>> data;

        private ViewPagerAdapter(Context context, List<List<Course>> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "星期一";
            } else if (position == 1) {
                return "星期二";
            }
            else if (position == 2) {
                return "星期三";
            } else if (position == 3) {
                return "星期四";
            }
            else if (position == 4) {
                return "星期五";
            } else if (position == 5) {
                return "星期六";
            } else {
                return "星期天";
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if(data.size() < 7 ||  data.get(position).size() < 1) {
                TextView textView = (TextView) View.inflate(context, R.layout.course_empty_textview, null);
                if(type == TYPE_COURSE_THEORY) {
                    textView.setText("这天你没有理论课");
                }
                else {
                    textView.setText("这天你没有实验课");
                }
                container.addView(textView);
                return textView;
            }
            else {
                ListView listView = getDateListView(context, data.get(position), data);
                container.addView(listView);
                return listView;
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    /**
     * 取得每日课程表列表
     *
     * @param context
     * @param list
     * @return
     */
    private ListView getDateListView(final Context context, final List<Course> list, final List<List<Course>> data) {
        ListView listView = new ListView(context);
        listView.setDivider(getResources().getDrawable(android.R.color.transparent));
        listView.setDividerHeight(0);
        listView.setAdapter(new CourseAdapter(context, list, type));
        //打开新页面显示课程详情
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Course selectedCourse = list.get(position);
                List<Course> sendData = new ArrayList<Course>();
                for (int i = 0; i < data.size(); i++) {
                    for(Course course : data.get(i)) {
                        if(selectedCourse.getName().equals(course.getName())) {
                            String timeStr = "";
                            if (i == 0) {
                                timeStr = "星期一";
                            } else if (i == 1) {
                                timeStr = "星期二";
                            } else if (i == 2) {
                                timeStr = "星期三";
                            } else if (i == 3) {
                                timeStr = "星期四";
                            } else if (i == 4) {
                                timeStr = "星期五";
                            } else if (i == 5) {
                                timeStr = "星期六";
                            } else if (i == 6){
                                timeStr = "星期天";
                            }
                            try {
                                if(!TextUtils.isEmpty(timeStr)) {
                                    Course newCourse = course.clone();
                                    newCourse.setTime(timeStr + " " + newCourse.getTime() + "节");
                                    sendData.add(newCourse);
                                }
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
                CourseActivity.startCourseActivity(context, sendData, type);
            }
        });
        return listView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if(type == TYPE_COURSE_THEORY) {
            return new CursorLoader(getActivity(), Uri.parse(SicauHelperProvider.URI_COURSE_ALL), null, null, null, null);
        }
        else {
            return new CursorLoader(getActivity(), Uri.parse(SicauHelperProvider.URI_LAB_COURSE_ALL), null, null, null, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        if (cursor != null && cursor.getCount() > 0) {
            List<List<Course>> data = null;
            if(type == TYPE_COURSE_THEORY) {
                data = CursorUtil.parseCourseList(cursor);
            }
            else {
                data = CursorUtil.parseLabCourseList(cursor);
            }
            initViewPager(getActivity(), data);
        } else {
            requestCourseList(getActivity());
        }

    }

    private void requestCourseList(final Context context) {
        progressDialog.show();
        //此处需要修改
        Map<String, String> params = new HashMap<String, String>();
        params.put("user", SharedPreferencesUtil.get(context, SharedPreferencesUtil.LOGIN_SID, "").toString());
        params.put("pwd", SharedPreferencesUtil.get(context, SharedPreferencesUtil.LOGIN_PSWD, "").toString());
        params.put("lb", "S");

        if(type == TYPE_COURSE_THEORY) {
            NetUtil.getCourseHtmlStr(context, params, new NetUtil.NetCallback(context) {
                @Override
                public void onSuccess(String result) {
                    final List<Course> tempList = StringUtil.parseCourseInfo(result);
                    initViewPager(context, StringUtil.parseCourseDateInfo(tempList));
                    UIUtil.dismissProgressDialog(progressDialog);
                    SaveIntentService.startActionCourseAll(context, tempList);
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    UIUtil.dismissProgressDialog(progressDialog);
                    super.onErrorResponse(volleyError);
                }
            });
        }
        else if(type == TYPE_COURSE_LAB) {
            NetUtil.getLabCourseHtmlStr(context, params, new NetUtil.NetCallback(context) {
                @Override
                public void onSuccess(String result) {
                    final List<List<Course>> tempList = StringUtil.parseLabCourseDateInfo(result);
                    initViewPager(context, tempList);
                    UIUtil.dismissProgressDialog(progressDialog);
                    //存储
                    List<Course> saveList = new ArrayList<Course>();
                    for(List<Course> date : tempList) {
                        for (Course course : date) {
                            saveList.add(course);
                        }
                    }
                    SaveIntentService.startActionLabCourseAll(context, saveList);
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    UIUtil.dismissProgressDialog(progressDialog);
                    super.onErrorResponse(volleyError);
                }
            });
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
