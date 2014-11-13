package cn.com.pplo.sicauhelper.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import cn.com.pplo.sicauhelper.model.Student;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.provider.TableContract;

/**
 * Created by Administrator on 2014/11/6.
 */
public class SQLiteUtil {

    /**
     * 存储登录用户
     * @param context
     * @param student
     */
    public static void saveLoginStudent(Context context, Student student){
        Log.d("winson", "存进去的student:" + student);
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(Uri.parse(SicauHelperProvider.URI_STUDENT_ALL), TableContract.TableStudent._SID + " =?", new String[]{student.getSid()});
        ContentValues values = new ContentValues();
        values.put(TableContract.TableStudent._ID, student.getId());
        values.put(TableContract.TableStudent._SID, student.getSid());
        values.put(TableContract.TableStudent._OBJECTID, student.getObjectId());
        values.put(TableContract.TableStudent._NAME, student.getName());
        values.put(TableContract.TableStudent._NICKNAME, student.getNickName());
        values.put(TableContract.TableStudent._PSWD, student.getPswd());
        values.put(TableContract.TableStudent._SCHOOL, student.getSchool());
        values.put(TableContract.TableStudent._PROFILE_URL, student.getProfileUrl());
        values.put(TableContract.TableStudent._BACKGROUND, student.getBackground());
        values.put(TableContract.TableStudent._ROLE, student.getRole());
        values.put(TableContract.TableStudent._CREATED_AT, student.getCreatedAt());
        values.put(TableContract.TableStudent._UPDATED_AT, student.getUpdatedAt());
        resolver.insert(Uri.parse(SicauHelperProvider.URI_STUDENT_ALL), values);
    }

    /**
     * 根据学号取得用户
     * @param context
     * @param sid
     * @return
     */
    public static Student getLoginStudent(Context context, String sid) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Uri.parse(SicauHelperProvider.URI_STUDENT_ALL), null, TableContract.TableStudent._SID + "=?", new String[]{sid}, null);
        Student student = null;
        try {
            while (cursor.moveToNext()) {
                student = new Student();
                student.setName(cursor.getString(cursor.getColumnIndex(TableContract.TableStudent._NAME)));
                student.setRole(cursor.getInt(cursor.getColumnIndex(TableContract.TableStudent._ROLE)));
                student.setSchool(cursor.getInt(cursor.getColumnIndex(TableContract.TableStudent._SCHOOL)));
                student.setProfileUrl(cursor.getString(cursor.getColumnIndex(TableContract.TableStudent._PROFILE_URL)));
                student.setBackground(cursor.getString(cursor.getColumnIndex(TableContract.TableStudent._BACKGROUND)));
                student.setNickName(cursor.getString(cursor.getColumnIndex(TableContract.TableStudent._NICKNAME)));
                student.setPswd(cursor.getString(cursor.getColumnIndex(TableContract.TableStudent._PSWD)));
                student.setSid(cursor.getString(cursor.getColumnIndex(TableContract.TableStudent._SID)));
                student.setId(cursor.getLong(cursor.getColumnIndex(TableContract.TableStudent._ID)));
                student.setObjectId(cursor.getString(cursor.getColumnIndex(TableContract.TableStudent._OBJECTID)));
                student.setCreatedAt(cursor.getString(cursor.getColumnIndex(TableContract.TableStudent._CREATED_AT)));
                student.setUpdatedAt(cursor.getString(cursor.getColumnIndex(TableContract.TableStudent._UPDATED_AT)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        Log.d("winson", "得到的student:" + student);
        return student;
    }
}
