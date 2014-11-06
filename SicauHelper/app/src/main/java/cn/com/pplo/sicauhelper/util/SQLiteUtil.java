package cn.com.pplo.sicauhelper.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import cn.com.pplo.sicauhelper.leancloud.AVStudent;
import cn.com.pplo.sicauhelper.model.Student;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;

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
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(Uri.parse(SicauHelperProvider.URI_STUDENT_ALL), AVStudent.SID + " =?", new String[]{student.getSid()});
        ContentValues values = new ContentValues();
        values.put(AVStudent.SID, student.getSid());
        values.put(AVStudent.NAME, student.getName());
        values.put(AVStudent.NICKNAME, student.getNickName());
        values.put(AVStudent.PSWD, student.getPswd());
        values.put(AVStudent.SCHOOL, student.getSchool());
        values.put(AVStudent.PROFILE_URL, student.getProfileUrl());
        values.put(AVStudent.BACKGROUND, student.getBackground());
        values.put(AVStudent.ROLE, student.getRole());
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
        Cursor cursor = resolver.query(Uri.parse(SicauHelperProvider.URI_STUDENT_ALL), null, AVStudent.SID + "=?", new String[]{sid}, null);
        Student student = null;
        try {
            while (cursor.moveToNext()) {
                student = new Student();
                student.setName(cursor.getString(cursor.getColumnIndex(AVStudent.NAME)));
                student.setRole(cursor.getInt(cursor.getColumnIndex(AVStudent.ROLE)));
                student.setSchool(cursor.getInt(cursor.getColumnIndex(AVStudent.SCHOOL)));
                student.setProfileUrl(cursor.getString(cursor.getColumnIndex(AVStudent.PROFILE_URL)));
                student.setBackground(cursor.getString(cursor.getColumnIndex(AVStudent.BACKGROUND)));
                student.setNickName(cursor.getString(cursor.getColumnIndex(AVStudent.NICKNAME)));
                student.setPswd(cursor.getString(cursor.getColumnIndex(AVStudent.PSWD)));
                student.setSid(cursor.getString(cursor.getColumnIndex(AVStudent.SID)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return student;
    }
}
