package cn.com.pplo.sicauhelper.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by winson on 2014/9/27.
 */
public class Course implements Parcelable, Comparable, Cloneable {
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

    public Course(Parcel source) {
        id = source.readInt();
        name = source.readString();
        category = source.readString();
        credit = source.readFloat();
        time = source.readString();
        classroom = source.readString();
        week = source.readString();
        teacher = source.readString();
        scheduleNum = source.readInt();
        selectedNum = source.readInt();
    }

    public Course(){

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

    @Override
    public int compareTo(Object another) {
        String anotherTime = ((Course)another).getTime();
        String currentTime = getTime();
        return currentTime.compareTo(anotherTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(category);
        dest.writeFloat(credit);
        dest.writeString(time);
        dest.writeString(classroom);
        dest.writeString(week);
        dest.writeString(teacher);
        dest.writeInt(scheduleNum);
        dest.writeInt(selectedNum);
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel source) {
            return new Course(source);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    @Override
    public Course clone() throws CloneNotSupportedException {
        Course course = null;
        try {
            course = (Course) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return course;
    }
}
