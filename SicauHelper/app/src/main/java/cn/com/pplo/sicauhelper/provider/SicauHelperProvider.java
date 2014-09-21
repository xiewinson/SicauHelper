package cn.com.pplo.sicauhelper.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class SicauHelperProvider extends ContentProvider {
    public static final String AUTHORITY = "cn.com.pplo.sicauhelper.provider";
    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final String SCORE_SINGLE = "score/#";
    private static final int CODE_SCORE_SINGLE = 10;
    public static final String URI_SCORE_SINGLE = "content://" + AUTHORITY + "/" +SCORE_SINGLE;


    private static final String SCORE_ALL = "score";
    private static final int CODE_SCORE_ALL = 20;
    public static final String URI_SCORE_ALL = "content://" + AUTHORITY + "/" + SCORE_ALL + "";


    static {
        uriMatcher.addURI(AUTHORITY, SCORE_ALL, CODE_SCORE_ALL);
        uriMatcher.addURI(AUTHORITY, SCORE_SINGLE, CODE_SCORE_SINGLE);
    }

    private SQLiteOpenHelper sqliteOpenHelper;

    public SicauHelperProvider() {
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        sqliteOpenHelper = new DatabaseOpenHelpre(getContext());
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
                long id = sqliteDatabase.insert(TableContract.TableScore.TABLE_NAME, null, values);
//                getContext().getContentResolver().notifyChange(Uri.parse(SicauHelperProvider.URI_SCORE_ALL), null);
                return Uri.withAppendedPath(uri, id + "");
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase sqliteDatabase = sqliteOpenHelper.getWritableDatabase();
        int count = 0;
        try {
            int matched = uriMatcher.match(uri);
            Uri insertUri = null;
            if(matched == CODE_SCORE_ALL || matched == CODE_SCORE_SINGLE){
                insertUri = Uri.parse(URI_SCORE_ALL);
            }
            sqliteDatabase.beginTransaction();
            for(ContentValues contentValues : values){
                Uri resultUri = insert(insertUri, contentValues);
                if(resultUri != null){
                    count++;
                }
            }

        }catch (Exception e){
        }finally {
            sqliteDatabase.endTransaction();
            return count;
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
                Cursor cursor =sqliteDatabase.query(TableContract.TableScore.TABLE_NAME, null,selection,selectionArgs,null, null, null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            case CODE_SCORE_SINGLE:
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
