package cn.com.pplo.sicauhelper.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.avos.avoscloud.AVUser;

import java.io.File;

import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.provider.DatabaseOpenHelper;
import cn.com.pplo.sicauhelper.provider.TableContract;

/**
 * Created by winson on 2014/11/18.
 */
public class UserUtil {
    public static boolean isAdmin(AVUser avUser) {
        boolean result = false;
        int roleId = avUser.getInt(TableContract.TableUser._ROLE);
        if(roleId == 0) {
            result = true;
        }

        return result;
    }

    /**
     * 清除用户相关
     */
    public static void clearUserInfo(final Context context) {
        AVUser.logOut();             //清除缓存用户对象
        AVUser currentUser = AVUser.getCurrentUser(); // 现在的currentUser是null了
        SicauHelperApplication.clearStudent();
        //清空数据
        SQLiteDatabase db = new DatabaseOpenHelper(context).getWritableDatabase();
        String DELETE_STR = "delete from ";
        db.execSQL(DELETE_STR + TableContract.TableScore.TABLE_NAME);
        db.execSQL(DELETE_STR + TableContract.TableCourse.TABLE_NAME);
        db.execSQL(DELETE_STR + TableContract.TableLabCourse.TABLE_NAME);
//        db.execSQL(DELETE_STR + TableContract.TableNews.TABLE_NAME);
//        db.execSQL(DELETE_STR + TableContract.TableClassroom.TABLE_NAME);
        db.execSQL(DELETE_STR + TableContract.TableUser.TABLE_NAME);
        db.execSQL(DELETE_STR + TableContract.TableExam.TABLE_NAME);
    }
}
