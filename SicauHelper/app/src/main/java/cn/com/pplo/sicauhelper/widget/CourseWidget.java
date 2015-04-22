package cn.com.pplo.sicauhelper.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ListView;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.model.Course;
import cn.com.pplo.sicauhelper.service.CourseWidgetService;
import cn.com.pplo.sicauhelper.ui.CourseActivity;


public class CourseWidget extends AppWidgetProvider {

    public static final String ACTION_ACCESS_ACTIVITY = "action_access_course_activity";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("winson", "点击了item。。。。。。。。" + intent.getAction());

        if (intent.getAction() == ACTION_ACCESS_ACTIVITY) {
            ArrayList<Course> data = intent.getParcelableArrayListExtra("data");
            CourseActivity.startCourseActivity(context, data, intent.getIntExtra("type", 0));
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Intent intent = new Intent(context, CourseWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.course_widget);
        views.setRemoteAdapter(R.id.course_listView, intent);
        views.setEmptyView(R.id.course_listView, R.id.empty_view);

        Intent accessIntent = new Intent(context, CourseWidget.class);
        accessIntent.setAction(ACTION_ACCESS_ACTIVITY);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent accessPI = PendingIntent.getBroadcast(context, 0, accessIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.course_listView, accessPI);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}


