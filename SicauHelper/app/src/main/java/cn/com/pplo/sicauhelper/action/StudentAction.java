package cn.com.pplo.sicauhelper.action;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SignUpCallback;

import cn.com.pplo.sicauhelper.model.Student;
import cn.com.pplo.sicauhelper.provider.TableContract;

/**
 * Created by winson on 2014/11/9.
 */
public class StudentAction {

    public void signUp(Student student, SignUpCallback callback) {
        AVUser avUser = new AVUser();
        avUser.setUsername(student.getSid());
        avUser.setPassword(student.getSid());
        avUser.put(TableContract.TableStudent._SID, student.getSid());
        avUser.put(TableContract.TableStudent._BACKGROUND, student.getBackground());
        avUser.put(TableContract.TableStudent._NAME, student.getName());
        avUser.put(TableContract.TableStudent._NICKNAME, student.getNickName());
        //存储头像
        AVFile headImage = new AVFile(System.currentTimeMillis() + "_" +student.getSid() + ".jpg", student.getProfileUrl(), null);
        avUser.put(TableContract.TableStudent._PROFILE_URL, headImage);

        avUser.put(TableContract.TableStudent._PSWD, student.getPswd());
        avUser.put(TableContract.TableStudent._SCHOOL, student.getSchool());
        avUser.put(TableContract.TableStudent._ROLE, student.getRole());
        avUser.signUpInBackground(callback);
    }

    /**
     * 登录
     * @param sid
     * @param pswd
     * @param callback
     */
    public void logIn(String sid, String pswd, LogInCallback callback) {
        AVUser.logInInBackground(sid, pswd, callback);
    }

    public void update(Student student) {

    }

    public void find(String sid, FindCallback callback) {
        AVQuery<AVObject> query = new AVQuery<AVObject>(TableContract.TableStudent.TABLE_NAME);
        query.whereEqualTo(TableContract.TableStudent._SID, sid);
        query.findInBackground(callback);
    }

    public void delete(long id) {

    }

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
        student.setId(avStudent.getLong("sutdent_id"));
        student.setCreatedAt(avStudent.getCreatedAt().toString());
        student.setUpdatedAt(avStudent.getUpdatedAt().toString());
        return student;
    }

}
