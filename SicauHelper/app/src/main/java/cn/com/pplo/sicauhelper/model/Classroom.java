package cn.com.pplo.sicauhelper.model;

/**
 * Created by winson on 2014/11/1.
 */
public class Classroom {
    private int id;
    private String name;
    private String date;
    private String time;
    private String school;

    public Classroom(int id, String name, String date, String time, String school) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.school = school;
    }

    public Classroom() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    @Override
    public String toString() {
        return "Classroom{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", school='" + school + '\'' +
                '}';
    }
}
