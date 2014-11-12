package cn.com.pplo.sicauhelper.dao;

import android.util.Log;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;

import cn.com.pplo.sicauhelper.model.Student;
import cn.com.pplo.sicauhelper.provider.TableContract;

/**
 * Created by winson on 2014/11/9.
 */
public class StudentDAO implements BaseDAO<Student> {

    @Override
    public void save(Student student, SaveCallback callback) {
        AVObject avObject = new AVObject(TableContract.TableStudent.TABLE_NAME);
        avObject.put(TableContract.TableStudent._SID, student.getSid());
        avObject.put(TableContract.TableStudent._BACKGROUND, student.getBackground());
        avObject.put(TableContract.TableStudent._NAME, student.getName());
        avObject.put(TableContract.TableStudent._NICKNAME, student.getNickName());
        avObject.put(TableContract.TableStudent._PROFILE_URL, student.getProfileUrl());
        avObject.put(TableContract.TableStudent._PSWD, student.getPswd());
        avObject.put(TableContract.TableStudent._SCHOOL, student.getSchool());
        avObject.put(TableContract.TableStudent._ROLE, student.getRole());
        avObject.saveInBackground(callback);
    }

    @Override
    public void update(Student student) {

    }

    public void find(FindCallback callback, String sid) {
        AVQuery<AVObject> query = new AVQuery<AVObject>(TableContract.TableStudent.TABLE_NAME);
        query.whereEqualTo(TableContract.TableStudent._SID, sid);
        query.findInBackground(callback);
    }

    @Override
    public void delete(long id) {

    }

    @Override
    public Student toModel(AVObject avStudent) {
        Student student = new Student();
        student.setNickName(avStudent.getString("nickName"));
        student.setName(avStudent.getString("name"));
        student.setRole(avStudent.getInt("role"));
        student.setBackground(avStudent.getString("background"));
        student.setProfileUrl(avStudent.getString("profileUrl"));
        student.setSchool(avStudent.getInt("school"));
        student.setPswd(avStudent.getString("pswd"));
        student.setSid(avStudent.getString("sid"));
        student.setObjectId(avStudent.getObjectId());
        student.setCreatedAt(avStudent.getCreatedAt().toString());
        student.setUpdatedAt(avStudent.getUpdatedAt().toString());
        return student;
    }

}
