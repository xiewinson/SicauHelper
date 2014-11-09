package cn.com.pplo.sicauhelper.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class SicauHelperProvider extends ContentProvider {
    public static final String AUTHORITY = "cn.com.pplo.sicauhelper.provider";
    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final String SCORE_SINGLE = "score/#";
    private static final int CODE_SCORE_SINGLE = 10;
    public static final String URI_SCORE_SINGLE = "content://" + AUTHORITY + "/" + SCORE_SINGLE;

    private static final String SCORE_ALL = "score";
    private static final int CODE_SCORE_ALL = 20;
    public static final String URI_SCORE_ALL = "content://" + AUTHORITY + "/" + SCORE_ALL + "";

    //Course
    private static final String COURSE_SINGLE = "course/#";
    private static final int CODE_COURSE_SINGLE = 30;
    public static final String URI_COURSE_SINGLE = "content://" + AUTHORITY + "/" + COURSE_SINGLE;

    private static final String COURSE_ALL = "course";
    private static final int CODE_COURSE_ALL = 40;
    public static final String URI_COURSE_ALL = "content://" + AUTHORITY + "/" + COURSE_ALL + "";

    //News
    private static final String NEWS_SINGLE = "news/#";
    private static final int CODE_NEWS_SINGLE = 50;
    public static final String URI_NEWS_SINGLE = "content://" + AUTHORITY + "/" + NEWS_SINGLE;

    private static final String NEWS_ALL = "news";
    private static final int CODE_NEWS_ALL = 60;
    public static final String URI_NEWS_ALL = "content://" + AUTHORITY + "/" + NEWS_ALL + "";

    //Classroom
    private static final String CLASSROOM_SINGLE = "classroom/#";
    private static final int CODE_CLASSROOM_SINGLE = 70;
    public static final String URI_CLASSROOM_SINGLE = "content://" + AUTHORITY + "/" + CLASSROOM_SINGLE;

    private static final String CLASSROOM_ALL = "classroom";
    private static final int CODE_CLASSROOM_ALL = 80;
    public static final String URI_CLASSROOM_ALL = "content://" + AUTHORITY + "/" + CLASSROOM_ALL + "";

    //Student
    private static final String STUDENT_SINGLE = "student/#";
    private static final int CODE_STUDENT_SINGLE = 90;
    public static final String URI_STUDENT_SINGLE = "content://" + AUTHORITY + "/" + STUDENT_SINGLE;

    private static final String STUDENT_ALL = "student";
    private static final int CODE_STUDENT_ALL = 100;
    public static final String URI_STUDENT_ALL = "content://" + AUTHORITY + "/" + STUDENT_ALL + "";


    static {
        uriMatcher.addURI(AUTHORITY, SCORE_ALL, CODE_SCORE_ALL);
        uriMatcher.addURI(AUTHORITY, SCORE_SINGLE, CODE_SCORE_SINGLE);

        uriMatcher.addURI(AUTHORITY, COURSE_ALL, CODE_COURSE_ALL);
        uriMatcher.addURI(AUTHORITY, COURSE_SINGLE, CODE_COURSE_SINGLE);

        uriMatcher.addURI(AUTHORITY, NEWS_ALL, CODE_NEWS_ALL);
        uriMatcher.addURI(AUTHORITY, NEWS_SINGLE, CODE_NEWS_SINGLE);

        uriMatcher.addURI(AUTHORITY, CLASSROOM_ALL, CODE_CLASSROOM_ALL);
        uriMatcher.addURI(AUTHORITY, CLASSROOM_SINGLE, CODE_CLASSROOM_SINGLE);

        uriMatcher.addURI(AUTHORITY, STUDENT_ALL, CODE_STUDENT_ALL);
        uriMatcher.addURI(AUTHORITY, STUDENT_SINGLE, CODE_STUDENT_SINGLE);
    }

    private SQLiteOpenHelper sqliteOpenHelper;

    public SicauHelperProvider() {
    }

    @Override
    public boolean onCreate() {
        sqliteOpenHelper = new DatabaseOpenHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase sqliteDatabase = sqliteOpenHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)){

            case CODE_SCORE_ALL:
                long scoreAllId = sqliteDatabase.insert(TableContract.TableScore.TABLE_NAME, null, values);
                return Uri.withAppendedPath(uri, scoreAllId + "");

            case CODE_COURSE_ALL:
                long courseAllId = sqliteDatabase.insert(TableContract.TableCourse.TABLE_NAME, null, values);
                return Uri.withAppendedPath(uri, courseAllId + "");

            case CODE_NEWS_ALL:
                long newsAllId = sqliteDatabase.insert(TableContract.TableNews.TABLE_NAME, null, values);
                return Uri.withAppendedPath(uri, newsAllId + "");

            case CODE_CLASSROOM_ALL:
                long classroomAllId = sqliteDatabase.insert(TableContract.TableClassroom.TABLE_NAME, null, values);
                return Uri.withAppendedPath(uri, classroomAllId + "");

            case CODE_STUDENT_ALL:
                long studentId = sqliteDatabase.insert(TableContract.TableStudent.TABLE_NAME, null, values);
                return Uri.withAppendedPath(uri, studentId + "");
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteDatabase sqliteDatabase = sqliteOpenHelper.getWritableDatabase();
        Log.d("winson", "match:" + uriMatcher.match(uri));
        switch (uriMatcher.match(uri)){

            case CODE_SCORE_ALL:
                Cursor scoureAllCursor = sqliteDatabase.query(TableContract.TableScore.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                return  scoureAllCursor;
            case CODE_SCORE_SINGLE:
                return null;

            case CODE_COURSE_ALL:
                Cursor  courseAllCursor = sqliteDatabase.query(TableContract.TableCourse.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                return courseAllCursor;
            case CODE_COURSE_SINGLE:
                return null;

            case CODE_NEWS_ALL:
                Cursor  newsAllCursor = sqliteDatabase.query(TableContract.TableNews.TABLE_NAME, projection, selection, selectionArgs, null, null, "_id desc");
                return newsAllCursor;
            case CODE_NEWS_SINGLE:
                Cursor  newsCursor = sqliteDatabase.query(TableContract.TableNews.TABLE_NAME, projection, selection, selectionArgs, null, null, "_id desc");
                return newsCursor;

            case CODE_CLASSROOM_ALL:
                Cursor  classroomAllCursor = sqliteDatabase.query(TableContract.TableClassroom.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                return classroomAllCursor;
            case CODE_CLASSROOM_SINGLE:
                return null;

            case CODE_STUDENT_ALL:
                Cursor  studentAllCursor = sqliteDatabase.query(TableContract.TableStudent.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                return studentAllCursor;
            case CODE_STUDENT_SINGLE:
                return null;

            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        SQLiteDatabase sqliteDatabase = sqliteOpenHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)){
            case CODE_SCORE_ALL:
                return sqliteDatabase.delete(TableContract.TableScore.TABLE_NAME, selection, selectionArgs);
            case CODE_SCORE_SINGLE:
                return 0;
            case CODE_COURSE_ALL:
                return sqliteDatabase.delete(TableContract.TableCourse.TABLE_NAME, selection, selectionArgs);
            case CODE_CLASSROOM_ALL:
                return sqliteDatabase.delete(TableContract.TableClassroom.TABLE_NAME, selection, selectionArgs);
            case CODE_STUDENT_ALL:
                return sqliteDatabase.delete(TableContract.TableStudent.TABLE_NAME, selection, selectionArgs);

            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        SQLiteDatabase sqliteDatabase = sqliteOpenHelper.getWritableDatabase();
        Log.d("winson", uri + "哪类型：" + uriMatcher.match(uri));
        switch (uriMatcher.match(uri)){
            case CODE_SCORE_ALL:
                return 0;
            case CODE_SCORE_SINGLE:
                return 0;

            //更新新闻
            case CODE_NEWS_SINGLE:
                return sqliteDatabase.update(TableContract.TableNews.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);

            //更新学生
            case CODE_NEWS_ALL:
                return sqliteDatabase.update(TableContract.TableStudent.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);

            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

    }
}
