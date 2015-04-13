package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.model.Course;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.service.SaveIntentService;
import cn.com.pplo.sicauhelper.ui.CourseActivity;
import cn.com.pplo.sicauhelper.ui.MainActivity;
import cn.com.pplo.sicauhelper.ui.adapter.WeekCourseAdapter;
import cn.com.pplo.sicauhelper.util.CursorUtil;
import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.SharedPreferencesUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;

public class WeekCourseFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private WeekCourseFragmentCallback callback;
    public static final int TYPE_COURSE_THEORY = 111;
    public static final int TYPE_COURSE_LAB = 222;
    public static final String TYPE_COURSE = "courseType";
    private int type = 0;

    private AlertDialog progressDialog;

    private LinearLayout emptyLayout;
    private Button importBtn;

    private LinearLayout weekLayout;

    private LinearLayout table00;
    private LinearLayout table01;
    private LinearLayout table02;
    private LinearLayout table03;
    private LinearLayout table04;
    private LinearLayout table05;
    private LinearLayout table06;

    private LinearLayout table10;
    private LinearLayout table11;
    private LinearLayout table12;
    private LinearLayout table13;
    private LinearLayout table14;
    private LinearLayout table15;
    private LinearLayout table16;

    private LinearLayout table20;
    private LinearLayout table21;
    private LinearLayout table22;
    private LinearLayout table23;
    private LinearLayout table24;
    private LinearLayout table25;
    private LinearLayout table26;

    private LinearLayout table30;
    private LinearLayout table31;
    private LinearLayout table32;
    private LinearLayout table33;
    private LinearLayout table34;
    private LinearLayout table35;
    private LinearLayout table36;

    private LinearLayout table40;
    private LinearLayout table41;
    private LinearLayout table42;
    private LinearLayout table43;
    private LinearLayout table44;
    private LinearLayout table45;
    private LinearLayout table46;

    private List<LinearLayout> containerList = new ArrayList<>();

    public static WeekCourseFragment newInstance(int type) {
        WeekCourseFragment fragment = new WeekCourseFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE_COURSE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    public WeekCourseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        type = getArguments().getInt(TYPE_COURSE);
        if (type == TYPE_COURSE_THEORY) {
            ((MainActivity) activity).onSectionAttached("理论课表");
        } else if (type == TYPE_COURSE_LAB) {
            ((MainActivity) activity).onSectionAttached("实验课表");
        }
        if (activity != null) {
            callback = (WeekCourseFragmentCallback) activity;
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

        return inflater.inflate(R.layout.fragment_week_course, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUp(view);
    }

    private void setUp(View view) {
        emptyLayout = (LinearLayout) view.findViewById(R.id.empty_layout);
        importBtn = (Button) view.findViewById(R.id.import_btn);
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCourseList(getActivity());
            }
        });

        weekLayout = (LinearLayout) view.findViewById(R.id.week_layout);
        weekLayout.setBackgroundResource(SicauHelperApplication.getPrimaryColor(getActivity(), false));

        table00 = (LinearLayout) view.findViewById(R.id.course_table_00);
        containerList.add(table00);
        table01 = (LinearLayout) view.findViewById(R.id.course_table_01);
        containerList.add(table01);
        table02 = (LinearLayout) view.findViewById(R.id.course_table_02);
        containerList.add(table02);
        table03 = (LinearLayout) view.findViewById(R.id.course_table_03);
        containerList.add(table03);
        table04 = (LinearLayout) view.findViewById(R.id.course_table_04);
        containerList.add(table04);
        table05 = (LinearLayout) view.findViewById(R.id.course_table_05);
        containerList.add(table05);
        table06 = (LinearLayout) view.findViewById(R.id.course_table_06);
        containerList.add(table06);

        table10 = (LinearLayout) view.findViewById(R.id.course_table_10);
        containerList.add(table10);
        table11 = (LinearLayout) view.findViewById(R.id.course_table_11);
        containerList.add(table11);
        table12 = (LinearLayout) view.findViewById(R.id.course_table_12);
        containerList.add(table12);
        table13 = (LinearLayout) view.findViewById(R.id.course_table_13);
        containerList.add(table13);
        table14 = (LinearLayout) view.findViewById(R.id.course_table_14);
        containerList.add(table14);
        table15 = (LinearLayout) view.findViewById(R.id.course_table_15);
        containerList.add(table15);
        table16 = (LinearLayout) view.findViewById(R.id.course_table_16);
        containerList.add(table16);

        table20 = (LinearLayout) view.findViewById(R.id.course_table_20);
        containerList.add(table20);
        table21 = (LinearLayout) view.findViewById(R.id.course_table_21);
        containerList.add(table21);
        table22 = (LinearLayout) view.findViewById(R.id.course_table_22);
        containerList.add(table22);
        table23 = (LinearLayout) view.findViewById(R.id.course_table_23);
        containerList.add(table23);
        table24 = (LinearLayout) view.findViewById(R.id.course_table_24);
        containerList.add(table24);
        table25 = (LinearLayout) view.findViewById(R.id.course_table_25);
        containerList.add(table25);
        table26 = (LinearLayout) view.findViewById(R.id.course_table_26);
        containerList.add(table26);

        table30 = (LinearLayout) view.findViewById(R.id.course_table_30);
        containerList.add(table30);
        table31 = (LinearLayout) view.findViewById(R.id.course_table_31);
        containerList.add(table31);
        table32 = (LinearLayout) view.findViewById(R.id.course_table_32);
        containerList.add(table32);
        table33 = (LinearLayout) view.findViewById(R.id.course_table_33);
        containerList.add(table33);
        table34 = (LinearLayout) view.findViewById(R.id.course_table_34);
        containerList.add(table34);
        table35 = (LinearLayout) view.findViewById(R.id.course_table_35);
        containerList.add(table35);
        table36 = (LinearLayout) view.findViewById(R.id.course_table_36);
        containerList.add(table36);

        table40 = (LinearLayout) view.findViewById(R.id.course_table_40);
        containerList.add(table40);
        table41 = (LinearLayout) view.findViewById(R.id.course_table_41);
        containerList.add(table41);
        table42 = (LinearLayout) view.findViewById(R.id.course_table_42);
        containerList.add(table42);
        table43 = (LinearLayout) view.findViewById(R.id.course_table_43);
        containerList.add(table43);
        table44 = (LinearLayout) view.findViewById(R.id.course_table_44);
        containerList.add(table44);
        table45 = (LinearLayout) view.findViewById(R.id.course_table_45);
        containerList.add(table45);
        table46 = (LinearLayout) view.findViewById(R.id.course_table_46);
        containerList.add(table46);

        //设置actionBar颜色
        UIUtil.setActionBarColor(getActivity(), getSupportActionBar(getActivity()), SicauHelperApplication.getPrimaryColor(getActivity(), false));

        getLoaderManager().initLoader(0, null, this);
    }

    private void initCourseTable(Context context, List<List<Course>> data) {
        for (View tableView : containerList) {
            tableView.setBackgroundColor(Color.WHITE);
        }
        Log.d("winson", "data的长度：" + data.size());
        emptyLayout.setVisibility(View.GONE);

//        int tableWidth = getResources().getDisplayMetrics().widthPixels/7;

        for (LinearLayout layout : containerList) {
//            layout.setLayoutParams(new GridLayout.LayoutParams(tableWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.removeAllViews();
        }

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
        int iLength = data.size();
        for (int i = 0; i < iLength; i++) {
            int jLength = data.get(i).size();
            for (int j = 0; j < jLength; j++) {
                Log.d("winson", "result:" + i);
                Course course = data.get(i).get(j);
                String timeStr = course.getTime();
                LinearLayout container = null;

                int color = 0;

                if (timeStr.contains("1-") || timeStr.contains("2-")) {
                    container = containerList.get(7 * 0 + i);
                    color = context.getResources().getColor(R.color.cyan_500);
                } else if (timeStr.contains("3-") || timeStr.contains("4-")) {
                    container = containerList.get(7 * 1 + i);
                    color = context.getResources().getColor(R.color.amber_500);
                } else if (timeStr.contains("5-") || timeStr.contains("6-")) {
                    container = containerList.get(7 * 2 + i);
                    color = context.getResources().getColor(R.color.deep_orange_500);
                } else if (timeStr.contains("7-") || timeStr.contains("8-")) {
                    container = containerList.get(7 * 3 + i);
                    color = context.getResources().getColor(R.color.blue_500);
                } else {
                    container = containerList.get(7 * 4 + i);
                    color = context.getResources().getColor(R.color.indigo_500);
                }
                container.setBackgroundColor(color);
                container.addView(initCourseView(context, data, data.get(i).get(j)));
            }
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_week_course, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            requestCourseList(getActivity());
        } else if (id == R.id.action_day_course) {
            if (callback != null) {
                callback.onClickDayBtn(type);
            }
        }
        return super.onOptionsItemSelected(item);
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
        listView.setAdapter(new WeekCourseAdapter(context, list, type));
        //打开新页面显示课程详情
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Course selectedCourse = list.get(position);
//                List<Course> sendData = new ArrayList<Course>();
//                for (int i = 0; i < data.size(); i++) {
//                    for(Course course : data.get(i)) {
//                        if(selectedCourse.getName().equals(course.getName())) {
//                            String timeStr = "";
//                            if (i == 0) {
//                                timeStr = "星期一";
//                            } else if (i == 1) {
//                                timeStr = "星期二";
//                            } else if (i == 2) {
//                                timeStr = "星期三";
//                            } else if (i == 3) {
//                                timeStr = "星期四";
//                            } else if (i == 4) {
//                                timeStr = "星期五";
//                            } else if (i == 5) {
//                                timeStr = "星期六";
//                            } else if (i == 6){
//                                timeStr = "星期天";
//                            }
//                            try {
//                                if(!TextUtils.isEmpty(timeStr)) {
//                                    Course newCourse = course.clone();
//                                    newCourse.setTime(timeStr + " " + newCourse.getTime() + "节");
//                                    sendData.add(newCourse);
//                                }
//                            } catch (CloneNotSupportedException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }
//                }
//                CourseActivity.startCourseActivity(context, sendData, type);
//            }
//        });
        return listView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (type == TYPE_COURSE_THEORY) {
            return new CursorLoader(getActivity(), Uri.parse(SicauHelperProvider.URI_COURSE_ALL), null, null, null, null);
        } else {
            return new CursorLoader(getActivity(), Uri.parse(SicauHelperProvider.URI_LAB_COURSE_ALL), null, null, null, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        if (cursor != null) {
            List<List<Course>> data = null;
            if (type == TYPE_COURSE_THEORY) {
                data = CursorUtil.parseCourseList(cursor);
            } else {
                data = CursorUtil.parseLabCourseList(cursor);
            }
            Log.d("winson", "data的长度：" + data.size() + "    " + data);

            initCourseTable(getActivity(), data);
        } else {
            //显示导入课表按钮


            emptyLayout.setVisibility(View.VISIBLE);
//            requestCourseList(getActivity());
        }

    }

    private void requestCourseList(final Context context) {
        progressDialog = UIUtil.getProgressDialog(getActivity(), "我正在从教务系统帮你找课表", true);
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                requestQueue.stop();
            }
        });
        progressDialog.show();
        //此处需要修改
        Map<String, String> params = new HashMap<String, String>();
        params.put("user", SharedPreferencesUtil.get(context, SharedPreferencesUtil.LOGIN_SID, "").toString());
        params.put("pwd", SharedPreferencesUtil.get(context, SharedPreferencesUtil.LOGIN_PSWD, "").toString());
        params.put("lb", "S");

        if (type == TYPE_COURSE_THEORY) {
            new NetUtil().getCourseHtmlStr(context, requestQueue, params, new NetUtil.NetCallback(context) {
                @Override
                public void onSuccess(String result) {
                    final List<Course> tempList = StringUtil.parseCourseInfo(result);
                    initCourseTable(context, StringUtil.parseCourseDateInfo(tempList));
                    UIUtil.dismissProgressDialog(progressDialog);
                    SaveIntentService.startActionCourseAll(context, tempList);
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    UIUtil.dismissProgressDialog(progressDialog);
                    super.onErrorResponse(volleyError);
                }
            });
        } else if (type == TYPE_COURSE_LAB) {
            new NetUtil().getLabCourseHtmlStr(context, requestQueue, params, new NetUtil.NetCallback(context) {
                @Override
                public void onSuccess(String result) {
                    final List<List<Course>> tempList = StringUtil.parseLabCourseDateInfo(result);
                    initCourseTable(context, tempList);
                    UIUtil.dismissProgressDialog(progressDialog);
                    //存储
                    List<Course> saveList = new ArrayList<Course>();
                    for (List<Course> date : tempList) {
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

    /**
     * 生成课程
     *
     * @param context
     * @param course
     * @return
     */
    private View initCourseView(final Context context, final List<List<Course>> data, final Course course) {

        View convertView = View.inflate(context, R.layout.item_fragment_week_course_date_list, null);
        TextView timeTv = (TextView) convertView.findViewById(R.id.time_tv);
        TextView nameTv = (TextView) convertView.findViewById(R.id.name_tv);
        TextView categoryTv = (TextView) convertView.findViewById(R.id.category_tv);
        TextView classroomTv = (TextView) convertView.findViewById(R.id.classroom_tv);
        TextView weekTv = (TextView) convertView.findViewById(R.id.week_tv);

        String time = course.getTime();
        int circleShape = 0;
        int color = 0;
        if (time.contains("1-") || time.contains("2-")) {
            circleShape = R.drawable.circle_cyan;
            color = context.getResources().getColor(R.color.cyan_500);
        } else if (time.contains("3-") || time.contains("4-")) {
            circleShape = R.drawable.circle_amber;
            color = context.getResources().getColor(R.color.amber_500);
        } else if (time.contains("5-") || time.contains("6-")) {
            circleShape = R.drawable.circle_deep_orange;
            color = context.getResources().getColor(R.color.deep_orange_500);
        } else if (time.contains("7-") || time.contains("8-")) {
            circleShape = R.drawable.circle_blue;
            color = context.getResources().getColor(R.color.blue_500);
        } else {
            circleShape = R.drawable.circle_indigo;
            color = context.getResources().getColor(R.color.indigo_500);
        }

        //设置课程名
        nameTv.setText(course.getName());
        //设置课程时间
        timeTv.setText(course.getTime());
        timeTv.setBackgroundResource(circleShape);

        //设置课程类型
        categoryTv.setText(course.getCategory() + "");
        //设置教室
        classroomTv.setText(course.getClassroom());
        if (type == CourseFragment.TYPE_COURSE_THEORY) {
            //设置周次
            weekTv.setText(course.getWeek() + "周");
        } else {
            //设置周次
            weekTv.setText(course.getWeek());
            categoryTv.setVisibility(View.GONE);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Course selectedCourse = course;
                List<Course> sendData = new ArrayList<Course>();
                for (int i = 0; i < data.size(); i++) {
                    for (Course course : data.get(i)) {
                        if (selectedCourse.getName().equals(course.getName())) {
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
                            } else if (i == 6) {
                                timeStr = "星期天";
                            }
                            try {
                                if (!TextUtils.isEmpty(timeStr)) {
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
                CourseActivity.startCourseActivity(getActivity(), sendData, type);
            }
        });
        convertView.setBackgroundColor(color);
        return convertView;
    }

    public void reloadData() {

        getLoaderManager().restartLoader(0, null, this);
    }

    public interface WeekCourseFragmentCallback {
        public void onClickDayBtn(int type);
    }
}
