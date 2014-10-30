package cn.com.pplo.sicauhelper.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.com.pplo.sicauhelper.model.Course;
import cn.com.pplo.sicauhelper.model.News;
import cn.com.pplo.sicauhelper.model.Score;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.provider.TableContract;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SaveIntentService extends IntentService {
    private static final String ACTION_NEWS_ALL = "cn.com.pplo.sicauhelper.service.action.news_all";
    private static final String ACTION_SCORE_ALL = "cn.com.pplo.sicauhelper.service.action.score_all";
    private static final String ACTION_COURSE_ALL = "cn.com.pplo.sicauhelper.service.action.course_all";

    private static final String EXTRA_NEWS_LIST = "cn.com.pplo.sicauhelper.service.extra.newses";
    private static final String EXTRA_SCORE_LIST = "cn.com.pplo.sicauhelper.service.extra.scores";
    private static final String EXTRA_COURSE_LIST = "cn.com.pplo.sicauhelper.service.extra.courses";
    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method

    /**
     * 存储新闻列表
     *
     * @param context
     * @param newsList
     */
    public static void startActionNewsAll(Context context, List<News> newsList) {
        Intent intent = new Intent(context, SaveIntentService.class);
        intent.setAction(ACTION_NEWS_ALL);
        intent.putParcelableArrayListExtra(EXTRA_NEWS_LIST, (java.util.ArrayList<? extends android.os.Parcelable>) newsList);
        context.startService(intent);
    }

    /**
     * 存储成绩表
     *
     * @param context
     * @param scoreList
     */
    public static void startActionScoreAll(Context context, List<Score> scoreList) {
        Intent intent = new Intent(context, SaveIntentService.class);
        intent.setAction(ACTION_SCORE_ALL);
        intent.putParcelableArrayListExtra(EXTRA_SCORE_LIST, (java.util.ArrayList<? extends android.os.Parcelable>) scoreList);
        context.startService(intent);
    }

    /**
     * 存储课程表
     *
     * @param context
     * @param courseList
     */
    public static void startActionCourseAll(Context context, List<Course> courseList) {
        Intent intent = new Intent(context, SaveIntentService.class);
        intent.setAction(ACTION_COURSE_ALL);
        intent.putParcelableArrayListExtra(EXTRA_COURSE_LIST, (java.util.ArrayList<? extends android.os.Parcelable>) courseList);
        context.startService(intent);
    }


    public SaveIntentService() {
        super("SaveIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_NEWS_ALL.equals(action)) {
                ArrayList<News> newsList = intent.getParcelableArrayListExtra(EXTRA_NEWS_LIST);
                handleActionNewsList(newsList);
            } else if (ACTION_SCORE_ALL.equals(action)) {
                ArrayList<Score> scoreList = intent.getParcelableArrayListExtra(EXTRA_SCORE_LIST);
                handleActionScoreList(scoreList);
            } else if (ACTION_COURSE_ALL.equals(action)) {
                ArrayList<Course> courseList = intent.getParcelableArrayListExtra(EXTRA_COURSE_LIST);
                handleActionCourseList(courseList);
            }
        }
    }

    /**
     * 存储课程列表
     *
     */
    private void handleActionCourseList(ArrayList<Course> tempList) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        contentResolver.delete(Uri.parse(SicauHelperProvider.URI_COURSE_ALL), "", null);
        if (tempList != null && tempList.size() > 0) {
            for (int i = 0; i < tempList.size(); i++) {
                ContentValues values = new ContentValues();
                values.put(TableContract.TableCourse._NAME, tempList.get(i).getName());
                values.put(TableContract.TableCourse._CATEGORY, tempList.get(i).getCategory());
                values.put(TableContract.TableCourse._CREDIT, tempList.get(i).getCredit());
                values.put(TableContract.TableCourse._TIME, tempList.get(i).getTime());
                values.put(TableContract.TableCourse._CLASSROOM, tempList.get(i).getClassroom());
                values.put(TableContract.TableCourse._WEEK, tempList.get(i).getWeek());
                values.put(TableContract.TableCourse._TEACHER, tempList.get(i).getTeacher());
                values.put(TableContract.TableCourse._SCHEDULENUM, tempList.get(i).getScheduleNum());
                values.put(TableContract.TableCourse._SELECTNUM, tempList.get(i).getSelectedNum());
                contentResolver.insert(Uri.parse(SicauHelperProvider.URI_COURSE_ALL), values);
            }
        }
    }


    /**
     * 存储新闻列表
     *
     * @param newsList
     */
    private void handleActionNewsList(ArrayList<News> newsList) {
        if (newsList != null) {
            for (int i = 0; i < newsList.size(); i++) {
                ContentValues values = new ContentValues();
                values.put(TableContract.TableNews._ID, newsList.get(i).getId());
                values.put(TableContract.TableNews._TITLE, newsList.get(i).getTitle());
                values.put(TableContract.TableNews._DATE, newsList.get(i).getDate());
                values.put(TableContract.TableNews._URL, newsList.get(i).getUrl());
                values.put(TableContract.TableNews._CONTENT, newsList.get(i).getContent());
                values.put(TableContract.TableNews._SRC, newsList.get(i).getSrc());
                values.put(TableContract.TableNews._CATEGORY, newsList.get(i).getCategory());
                ContentResolver contentResolver = getApplicationContext().getContentResolver();
                //若数据库不存在该条数据便插入
                if (contentResolver.query(Uri.parse(SicauHelperProvider.URI_NEWS_SINGLE), null, TableContract.TableNews._ID + " = ?", new String[]{values.getAsString(TableContract.TableNews._ID)}, null).getCount() == 0) {
                    Log.d("winson", "不存在-->" + i);
                    contentResolver.insert(Uri.parse(SicauHelperProvider.URI_NEWS_SINGLE), values);
                }
            }
        }
    }

    /**
     * 存储成绩列表
     *
     * @param tempList
     */
    private void handleActionScoreList(ArrayList<Score> tempList) {
        if (tempList != null) {
            ContentResolver contentResolver = getApplicationContext().getContentResolver();
            contentResolver.delete(Uri.parse(SicauHelperProvider.URI_SCORE_ALL), "", null);
            for (int i = 0; i < tempList.size(); i++) {
                ContentValues values = new ContentValues();
                values.put(TableContract.TableScore._CATEGORY, tempList.get(i).getCategory());
                values.put(TableContract.TableScore._COURSE, tempList.get(i).getCourse());
                values.put(TableContract.TableScore._CREDIT, tempList.get(i).getCredit());
                values.put(TableContract.TableScore._GRADE, tempList.get(i).getGrade());
                values.put(TableContract.TableScore._MARK, tempList.get(i).getMark());
                contentResolver.insert(Uri.parse(SicauHelperProvider.URI_SCORE_ALL), values);
            }
        }
    }
}
