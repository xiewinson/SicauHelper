package cn.com.pplo.sicauhelper.service;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.model.Course;
import cn.com.pplo.sicauhelper.model.Score;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.ui.fragment.CourseFragment;
import cn.com.pplo.sicauhelper.util.CursorUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;
import cn.com.pplo.sicauhelper.widget.CourseWidget;

public class CourseWidgetService extends RemoteViewsService {
    private int pos = 0;
    private List<List<Course>> list = new ArrayList<>();
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new CourseRemoteViewsFactory(getApplicationContext(), intent);
    }

    public CourseWidgetService() {
    }

    private class CourseRemoteViewsFactory implements RemoteViewsFactory {
        private Context context;
        private List<Course> data;

        private CourseRemoteViewsFactory(Context context, Intent intent) {
            this.context = context;
            int mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            //根据星期几选定课程表
            Calendar calendar = Calendar.getInstance();
            pos = 0;
            switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.MONDAY:
                    pos = 0;
                    break;
                case Calendar.TUESDAY:
                    pos = 1;
                    break;
                case Calendar.WEDNESDAY:
                    pos = 2;
                    break;
                case Calendar.THURSDAY:
                    pos = 3;
                    break;
                case Calendar.FRIDAY:
                    pos = 4;
                    break;
                case Calendar.SATURDAY:
                    pos = 5;
                    break;
                case Calendar.SUNDAY:
                    pos = 6;
                    break;
            }
            Cursor theoryCursor = getContentResolver().query(Uri.parse(SicauHelperProvider.URI_COURSE_ALL), null, null, null, null);
            Cursor labCursor = getContentResolver().query(Uri.parse(SicauHelperProvider.URI_LAB_COURSE_ALL), null, null, null, null);
            list.clear();
            list.addAll(CursorUtil.parseCourseList(theoryCursor));
            List<List<Course>> labLists = CursorUtil.parseLabCourseList(labCursor);

            for (int i = 0; i < list.size(); i ++) {
                list.get(i).addAll(labLists.get(i));
                Collections.sort(list.get(i));
            }

           data = list.get(pos);
            Log.d("winson", "一共有：" + data.size());
            if(theoryCursor != null) {
                theoryCursor.close();
            }
            if(labCursor != null) {
                labCursor.close();
            }
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.item_widget_course);
            Course course = data.get(position);
            String time = course.getTime();
            remoteViews.setTextViewText(R.id.name_tv, course.getName());
            remoteViews.setTextViewText(R.id.classroom_tv, course.getClassroom());
            remoteViews.setTextViewText(R.id.time_tv, time);

            int circleShape = 0;
            int color = 0;
            if (time.equals("1-2")) {
                circleShape = R.drawable.circle_cyan;
                color = context.getResources().getColor(R.color.cyan_500);
            } else if (time.equals("3-4")) {
                circleShape = R.drawable.circle_amber;
                color = context.getResources().getColor(R.color.amber_500);
            } else if (time.equals("5-6")) {
                circleShape = R.drawable.circle_deep_orange;
                color = context.getResources().getColor(R.color.deep_orange_500);
            } else if (time.equals("7-8")) {
                circleShape = R.drawable.circle_blue;
                color = context.getResources().getColor(R.color.blue_500);
            } else if (time.equals("9-10")) {
                circleShape = R.drawable.circle_indigo;
                color = context.getResources().getColor(R.color.indigo_500);
            } else if (time.contains("1-") || time.contains("2-") || time.contains("3-") || time.contains("4-")) {
                circleShape = R.drawable.circle_amber;
                color = context.getResources().getColor(R.color.amber_500);
            }else if (time.contains("5-") || time.contains("6-") || time.contains("7-") || time.contains("8-")) {
                circleShape = R.drawable.circle_deep_orange;
                color = context.getResources().getColor(R.color.deep_orange_500);
            }else {
                circleShape = R.drawable.circle_indigo;
                color = context.getResources().getColor(R.color.indigo_500);
            }
            remoteViews.setImageViewResource(R.id.course_iv, circleShape);
            Intent fillIntent = new Intent();
            List<Course> sendData = new ArrayList<Course>();
            for (int i = 0; i < list.size(); i++) {
                for(Course mCourse : list.get(i)) {
                    if(course.getName().equals(mCourse.getName())) {
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
                                Course newCourse = mCourse.clone();
                                newCourse.setTime(timeStr + " " + newCourse.getTime() + "节");
                                sendData.add(newCourse);
                            }
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
            fillIntent.putParcelableArrayListExtra("data", (java.util.ArrayList<? extends android.os.Parcelable>) sendData);
            fillIntent.putExtra("type", CourseFragment.TYPE_COURSE_THEORY);
            remoteViews.setOnClickFillInIntent(R.id.item_id, fillIntent);

            return remoteViews;
        }



        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
