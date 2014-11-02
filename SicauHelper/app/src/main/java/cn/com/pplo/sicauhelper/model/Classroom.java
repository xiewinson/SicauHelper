package cn.com.pplo.sicauhelper.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by winson on 2014/11/1.
 */
public class Classroom implements Parcelable {
    private int id;
    private String name;
    private String time;
    private String school;

    public Classroom(int id, String name, String time, String school) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.school = school;
    }

    public Classroom() {
    }

    public Classroom(Parcel source) {
        id = source.readInt();
        name = source.readString();
        time = source.readString();
        school = source.readString();
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
                ", time='" + time + '\'' +
                ", school='" + school + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(time);
        dest.writeString(school);
    }

    public static final Creator<Classroom> CREATOR = new Creator<Classroom>() {
        @Override
        public Classroom createFromParcel(Parcel source) {
            return new Classroom(source);
        }

        @Override
        public Classroom[] newArray(int size) {
            return new Classroom[size];
        }
    };
}
