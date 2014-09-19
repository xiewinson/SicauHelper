package cn.com.pplo.sicauhelper.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2014/9/19.
 */
public class DatabaseOpenHelpre extends SQLiteOpenHelper {
    public static final String DB_NAME = "SICAU_HELPER_DB";
    public static final int DB_VERSION = 1;


    public static final String createScoreSql = "create table " + TableContract.TableScore.TABLE_NAME + "("
            + TableContract.TableScore._ID + " integer primary key autoincrement, "
            + TableContract.TableScore._COURSE + " text, "
            + TableContract.TableScore._MARK + " text, "
            + TableContract.TableScore._CREDIT + " real, "
            + TableContract.TableScore._CATEGORY + " text, "
            + TableContract.TableScore._GRADE + " real"
            + ")";


    public DatabaseOpenHelpre(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createScoreSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
