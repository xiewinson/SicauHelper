package cn.com.pplo.sicauhelper.leancloud;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

import cn.com.pplo.sicauhelper.model.Student;

/**
 * Created by winson on 2014/11/5.
 */
@AVClassName("Student")
public class AVStudent extends AVObject {
    public static final String TABLE_NAME = "Student";

    public static final String SID = "sid";
    public static final String NAME = "name";
    public static final String NICKNAME = "nickName";
    public static final String PSWD = "pswd";
    public static final String SCHOOL = "school";
    public static final String PROFILE_URL = "profileUrl";
    public static final String BACKGROUND = "background";
    public static final String ROLE = "role";

    public String getSid(){
        return getString(SID);
    }
    public void setSid(String sid){
        put(SID, sid);
    }

    public String getName(){
        return getString(NAME);
    }
    public void setName(String name){
        put(NAME, name);
    }

    public String getNickname(){
        return getString(NICKNAME);
    }
    public void setNickname(String nickname) {
        put(NICKNAME, nickname);
    }

    public String getPswd() {
        return getString(PSWD);
    }
    public void setPswd(String pswd) {
        put(PSWD, pswd);
    }

    public int getSchool() {
        return getInt(SCHOOL);
    }
    public void setSchool(int school) {
        put(SCHOOL, school);
    }
    public String getProfileUrl() {
        return getString(PROFILE_URL);
    }
    public void setProfileUrl(String profileUrl) {
        put(PROFILE_URL, profileUrl);
    }

    public String getBackground() {
        return getString(BACKGROUND);
    }
    public void setBackground(String background) {
        put(BACKGROUND, background);
    }

    public int getRole() {
        return getInt(ROLE);
    }
    public void setRole(int role) {
        put(ROLE, role);
    }
}
