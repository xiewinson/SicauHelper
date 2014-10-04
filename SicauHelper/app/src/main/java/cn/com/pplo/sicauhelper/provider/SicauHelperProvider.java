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


    static {
        uriMatcher.addURI(AUTHORITY, SCORE_ALL, CODE_SCORE_ALL);
        uriMatcher.addURI(AUTHORITY, SCORE_SINGLE, CODE_SCORE_SINGLE);

        uriMatcher.addURI(AUTHORITY, COURSE_ALL, CODE_COURSE_ALL);
        uriMatcher.addURI(AUTHORITY, COURSE_SINGLE, CODE_COURSE_SINGLE);
    }

    private SQLiteOpenHelper sqliteOpenHelper;

    public SicauHelperProvider() {
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        sqliteOpenHelper = new DatabaseOpenHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        SQLiteDatabase sqliteDatabase = sqliteOpenHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)){

            case CODE_SCORE_ALL:
                long scoreAllId = sqliteDatabase.insert(TableContract.TableScore.TABLE_NAME, null, values);
//                getContext().getContentResolver().notifyChange(Uri.parse(SicauHelperProvider.URI_SCORE_ALL), null);
                return Uri.withAppendedPath(uri, scoreAllId + "");

            case CODE_COURSE_ALL:
                long courseAllId = sqliteDatabase.insert(TableContract.TableCourse.TABLE_NAME, null, values);
//                getContext().getContentResolver().notifyChange(Uri.parse(SicauHelperProvider.URI_SCORE_ALL), null);
                return Uri.withAppendedPath(uri, courseAllId + "");
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        SQLiteDatabase sqliteDatabase = sqliteOpenHelper.getReadableDatabase();
        Log.d("winson", "match:" + uriMatcher.match(uri));
        switch (uriMatcher.match(uri)){

            case CODE_SCORE_ALL:
                Cursor scoureAllCursor = sqliteDatabase.query(TableContract.TableScore.TABLE_NAME, null,selection,selectionArgs,null, null, null);
                scoureAllCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return  scoureAllCursor;
            case CODE_SCORE_SINGLE:
                return null;

            case CODE_COURSE_ALL:
                Cursor  courseAllCursor = sqliteDatabase.query(TableContract.TableCourse.TABLE_NAME, null,selection,selectionArgs,null, null, null);
                courseAllCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return courseAllCursor;
            case CODE_COURSE_SINGLE:
                return null;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        switch (uriMatcher.match(uri)){
            case CODE_SCORE_ALL:
                return 0;
            case CODE_SCORE_SINGLE:
                return 0;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        switch (uriMatcher.match(uri)){
            case CODE_SCORE_ALL:
                return 0;
            case CODE_SCORE_SINGLE:
                return 0;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

    }
}
