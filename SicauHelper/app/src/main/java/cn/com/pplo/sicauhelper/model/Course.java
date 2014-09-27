package cn.com.pplo.sicauhelper.model;

import java.io.Serializable;

/**
 * Created by winson on 2014/9/27.
 */
public class Course implements Serializable {
    private int id;
    private String name;
    private String category;
    private float credit;
    private String time;
    private String classroom;
    private String week;
    private String teacher;
    private int scheduleNum;
    private int selectedNum;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getCredit() {
        return credit;
    }

    public void setCredit(float credit) {
        this.credit = credit;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public int getScheduleNum() {
        return scheduleNum;
    }

    public void setScheduleNum(int scheduleNum) {
        this.scheduleNum = scheduleNum;
    }

    public int getSelectedNum() {
        return selectedNum;
    }

    public void setSelectedNum(int selectedNum) {
        this.selectedNum = selectedNum;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", credit=" + credit +
                ", time='" + time + '\'' +
                ", classroom='" + classroom + '\'' +
                ", week='" + week + '\'' +
                ", teacher='" + teacher + '\'' +
                ", scheduleNum=" + scheduleNum +
                ", selectedNum=" + selectedNum +
                '}';
    }
}
