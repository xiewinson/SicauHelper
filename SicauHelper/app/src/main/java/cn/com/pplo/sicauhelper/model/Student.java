package cn.com.pplo.sicauhelper.model;

/**
 * Created by winson on 2014/9/14.
 */
public class Student {
    private long id;
    private String sid;
    private String name;
    private String nickName;
    private String pswd;
    private int school;
    private int role;
    private String profileUrl;
    private String background;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public int getSchool() {
        return school;
    }

    public void setSchool(int school) {
        this.school = school;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", sid=" + sid +
                ", name='" + name + '\'' +
                ", nickName='" + nickName + '\'' +
                ", pswd='" + pswd + '\'' +
                ", school=" + school +
                ", role=" + role +
                ", profileUrl='" + profileUrl + '\'' +
                ", background='" + background + '\'' +
                '}';
    }
}
