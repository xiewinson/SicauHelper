package cn.com.pplo.sicauhelper.model;

/**
 * Created by winson on 2014/9/14.
 */
public class Student {
    private long id;
    private long sid;
    private String pswd;
    private String name;
    private String school;
    private String role;
    private String headImg;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", sid=" + sid +
                ", pswd='" + pswd + '\'' +
                ", name='" + name + '\'' +
                ", school='" + school + '\'' +
                ", role='" + role + '\'' +
                ", headImg='" + headImg + '\'' +
                '}';
    }
}
