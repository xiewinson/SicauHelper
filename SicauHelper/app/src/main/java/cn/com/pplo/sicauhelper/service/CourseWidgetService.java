package cn.com.pplo.sicauhelper.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.model.Course;
import cn.com.pplo.sicauhelper.model.Score;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.util.CursorUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;

public class CourseWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Cursor cursor = getContentResolver().query(Uri.parse(SicauHelperProvider.URI_COURSE_ALL), null, null, null, null);
        List<List<Course>> list = CursorUtil.parseCourseList(cursor);
        Log.d("winson", "星期一的课：" + list.get(0).size());
        return new CourseRemoteViewsFactory(getApplicationContext(), list.get(0));
    }

    public CourseWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       return  null;
    }

    private class CourseRemoteViewsFactory implements RemoteViewsFactory {
        private Context context;
        private List<Course> data;

        private CourseRemoteViewsFactory(Context context, List<Course> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {
            data.clear();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.item_fragment_course_date_list);
            remoteViews.setTextViewText(R.id.name_tv, "课程啊....");
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 0;
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
