package cn.com.pplo.sicauhelper.service;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.com.pplo.sicauhelper.model.Classroom;
import cn.com.pplo.sicauhelper.model.Course;
import cn.com.pplo.sicauhelper.model.Exam;
import cn.com.pplo.sicauhelper.model.News;
import cn.com.pplo.sicauhelper.model.Score;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.util.SharedPreferencesUtil;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class SaveIntentService extends IntentService {
    private static final String ACTION_NEWS_ALL = "cn.com.pplo.sicauhelper.service.action.news_all";
    private static final String ACTION_SCORE_ALL = "cn.com.pplo.sicauhelper.service.action.score_all";
    private static final String ACTION_COURSE_ALL = "cn.com.pplo.sicauhelper.service.action.course_all";
    private static final String ACTION_LAB_COURSE_ALL = "cn.com.pplo.sicauhelper.service.action.lab_course_all";
    private static final String ACTION_CLASSROOM_ALL = "cn.com.pplo.sicauhelper.service.action.classroom_all";
    private static final String ACTION_EXAM_ALL = "cn.com.pplo.sicauhelper.service.action.exam_all";

    private static final String EXTRA_NEWS_LIST = "cn.com.pplo.sicauhelper.service.extra.newses";
    private static final String EXTRA_SCORE_LIST = "cn.com.pplo.sicauhelper.service.extra.scores";
    private static final String EXTRA_COURSE_LIST = "cn.com.pplo.sicauhelper.service.extra.courses";
    private static final String EXTRA_LAB_COURSE_LIST = "cn.com.pplo.sicauhelper.service.extra.lab_courses";
    private static final String EXTRA_CLASSROOM_LIST = "cn.com.pplo.sicauhelper.service.extra.classroomes";
    private static final String EXTRA_EXAM_LIST = "cn.com.pplo.sicauhelper.service.extra.exam";
    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

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

    /**
     * 存储实验课表
     *
     * @param context
     * @param courseList
     */
    public static void startActionLabCourseAll(Context context, List<Course> courseList) {
        Intent intent = new Intent(context, SaveIntentService.class);
        intent.setAction(ACTION_LAB_COURSE_ALL);
        intent.putParcelableArrayListExtra(EXTRA_LAB_COURSE_LIST, (java.util.ArrayList<? extends android.os.Parcelable>) courseList);
        context.startService(intent);
    }

    /**
     * 存储空闲教室
     *
     * @param context
     * @param classroomList
     */
    public static void startActionClassroomAll(Context context, List<Classroom> classroomList) {
        Intent intent = new Intent(context, SaveIntentService.class);
        intent.setAction(ACTION_CLASSROOM_ALL);
        intent.putParcelableArrayListExtra(EXTRA_CLASSROOM_LIST, (java.util.ArrayList<? extends android.os.Parcelable>) classroomList);
        context.startService(intent);
    }

    /**
     * 存储考试安排
     *
     * @param context
     * @param examList
     */
    public static void startActionExamAll(Context context, List<Exam> examList) {
        Intent intent = new Intent(context, SaveIntentService.class);
        intent.setAction(ACTION_EXAM_ALL);
        intent.putParcelableArrayListExtra(EXTRA_EXAM_LIST, (java.util.ArrayList<? extends android.os.Parcelable>) examList);
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
            } else if (ACTION_LAB_COURSE_ALL.equals(action)) {
                ArrayList<Course> courseList = intent.getParcelableArrayListExtra(EXTRA_LAB_COURSE_LIST);
                handleActionLabCourseList(courseList);
            } else if (ACTION_CLASSROOM_ALL.equals(action)) {
                ArrayList<Classroom> classroomList = intent.getParcelableArrayListExtra(EXTRA_CLASSROOM_LIST);
                handleActionClassroomList(classroomList);
            } else if (ACTION_EXAM_ALL.equals(action)) {
                ArrayList<Exam> examList = intent.getParcelableArrayListExtra(EXTRA_EXAM_LIST);
                handleActionExamList(examList);
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
            ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
            for (int i = 0; i < tempList.size(); i++) {
                Course course = tempList.get(i);
                ContentValues values = new ContentValues();
                values.put(TableContract.TableCourse._NAME, course.getName());
                values.put(TableContract.TableCourse._CATEGORY, course.getCategory());
                values.put(TableContract.TableCourse._CREDIT, course.getCredit());
                values.put(TableContract.TableCourse._TIME, course.getTime());
                values.put(TableContract.TableCourse._CLASSROOM, course.getClassroom());
                values.put(TableContract.TableCourse._WEEK, course.getWeek());
                values.put(TableContract.TableCourse._TEACHER, course.getTeacher());
                values.put(TableContract.TableCourse._SCHEDULENUM, course.getScheduleNum());
                values.put(TableContract.TableCourse._SELECTNUM, course.getSelectedNum());

                operations.add(ContentProviderOperation.newInsert(Uri.parse(SicauHelperProvider.URI_COURSE_ALL)).withValues(values).build());
            }
            try {
                contentResolver.applyBatch(SicauHelperProvider.AUTHORITY, operations);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 存储实验课程列表
     *
     */
    private void handleActionLabCourseList(ArrayList<Course> tempList) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        contentResolver.delete(Uri.parse(SicauHelperProvider.URI_LAB_COURSE_ALL), "", null);
        if (tempList != null && tempList.size() > 0) {
            ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
            for (int i = 0; i < tempList.size(); i++) {
                Course course = tempList.get(i);
                ContentValues values = new ContentValues();
                values.put(TableContract.TableLabCourse._NAME, course.getName());
                values.put(TableContract.TableLabCourse._CATEGORY, course.getCategory());
                values.put(TableContract.TableLabCourse._CREDIT, course.getCredit());
                values.put(TableContract.TableLabCourse._TIME, course.getTime());
                values.put(TableContract.TableLabCourse._CLASSROOM, course.getClassroom());
                values.put(TableContract.TableLabCourse._WEEK, course.getWeek());
                values.put(TableContract.TableLabCourse._TEACHER, course.getTeacher());
                values.put(TableContract.TableLabCourse._SCHEDULENUM, course.getScheduleNum());
                values.put(TableContract.TableLabCourse._SELECTNUM, course.getSelectedNum());

                operations.add(ContentProviderOperation.newInsert(Uri.parse(SicauHelperProvider.URI_LAB_COURSE_ALL)).withValues(values).build());
            }
            try {
                contentResolver.applyBatch(SicauHelperProvider.AUTHORITY, operations);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 存储新闻列表
     *
     * @param newsList
     */
    private void handleActionNewsList(ArrayList<News> newsList) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        contentResolver.delete(Uri.parse(SicauHelperProvider.URI_NEWS_ALL), "", null);
        if (newsList != null) {
            ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
            for (int i = 0; i < newsList.size(); i++) {
                News news = newsList.get(i);
                ContentValues values = new ContentValues();
                values.put(TableContract.TableNews._ID, news.getId());
                values.put(TableContract.TableNews._TITLE, news.getTitle());
                values.put(TableContract.TableNews._DATE, news.getDate());
                values.put(TableContract.TableNews._URL, news.getUrl());
                values.put(TableContract.TableNews._CONTENT, news.getContent());
                values.put(TableContract.TableNews._SRC, news.getSrc());
                values.put(TableContract.TableNews._CATEGORY, news.getCategory());
                 operations.add(ContentProviderOperation.newInsert(Uri.parse(SicauHelperProvider.URI_NEWS_ALL)).withValues(values).build());
            }
            try {
                contentResolver.applyBatch(SicauHelperProvider.AUTHORITY, operations);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
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
            ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
            for (int i = 0; i < tempList.size(); i++) {
                Score score = tempList.get(i);
                ContentValues values = new ContentValues();
                values.put(TableContract.TableScore._CATEGORY, score.getCategory());
                values.put(TableContract.TableScore._COURSE, score.getCourse());
                values.put(TableContract.TableScore._CREDIT, score.getCredit());
                values.put(TableContract.TableScore._GRADE, score.getGrade());
                values.put(TableContract.TableScore._MARK, score.getMark());
                operations.add(ContentProviderOperation.newInsert(Uri.parse(SicauHelperProvider.URI_SCORE_ALL)).withValues(values).build());
            }
            try {
                contentResolver.applyBatch(SicauHelperProvider.AUTHORITY, operations);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 存储空闲教室列表
     *
     * @param tempList
     */
    private void handleActionClassroomList(ArrayList<Classroom> tempList) {
        if (tempList != null) {
            ContentResolver contentResolver = getApplicationContext().getContentResolver();
            contentResolver.delete(Uri.parse(SicauHelperProvider.URI_CLASSROOM_ALL), "", null);
            ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
            for (int i = 0; i < tempList.size(); i++) {
                ContentValues values = new ContentValues();
                Classroom classroom = tempList.get(i);
                values.put(TableContract.TableClassroom._TIME, classroom.getTime());
                values.put(TableContract.TableClassroom._NAME, classroom.getName());
                values.put(TableContract.TableClassroom._SCHOOL, classroom.getSchool());
                operations.add(ContentProviderOperation.newInsert(Uri.parse(SicauHelperProvider.URI_CLASSROOM_ALL)).withValues(values).build());
            }
            try {
                contentResolver.applyBatch(SicauHelperProvider.AUTHORITY, operations);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
            SharedPreferencesUtil.put(getApplicationContext(), SharedPreferencesUtil.LAST_SYNC_CLASSROMM, System.currentTimeMillis());
        }
    }

    /**
     * 存储考试安排列表
     *
     */
    private void handleActionExamList(ArrayList<Exam> tempList) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        contentResolver.delete(Uri.parse(SicauHelperProvider.URI_EXAM_ALL), "", null);
        if (tempList != null && tempList.size() > 0) {
            ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
            for (int i = 0; i < tempList.size(); i++) {
                Exam exam = tempList.get(i);
                ContentValues values = new ContentValues();
                values.put(TableContract.TableExam._COURSE, exam.getCourse());
                values.put(TableContract.TableExam._CLASSROOM, exam.getClassroom());
                values.put(TableContract.TableExam._NUM, exam.getNum());
                values.put(TableContract.TableExam._TIME, exam.getTime());

                operations.add(ContentProviderOperation.newInsert(Uri.parse(SicauHelperProvider.URI_EXAM_ALL)).withValues(values).build());
            }
            try {
                contentResolver.applyBatch(SicauHelperProvider.AUTHORITY, operations);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
        }
    }

}
