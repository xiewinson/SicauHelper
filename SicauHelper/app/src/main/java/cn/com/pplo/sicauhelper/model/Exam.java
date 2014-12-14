package cn.com.pplo.sicauhelper.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by winson on 2014/12/13.
 */
public class Exam implements Parcelable {
    private int id;
    private String course;
    private String time;
    private String classroom;
    private String num;

    public Exam() {

    }

    public Exam(Parcel source) {
        this.id = source.readInt();
        this.course = source.readString();
        this.time = source.readString();
        this.classroom = source.readString();
        this.num = source.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
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

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "Exam{" +
                "id=" + id +
                ", course='" + course + '\'' +
                ", time='" + time + '\'' +
                ", classroom='" + classroom + '\'' +
                ", num='" + num + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.course);
        dest.writeString(this.time);
        dest.writeString(this.classroom);
        dest.writeString(this.num);
    }

    public static final Creator<Exam> CREATOR = new Creator<Exam>() {
        @Override
        public Exam createFromParcel(Parcel source) {
            return new Exam(source);
        }

        @Override
        public Exam[] newArray(int size) {
            return new Exam[size];
        }
    };
}
