package cn.com.pplo.sicauhelper.model;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2014/9/16.
 */
public class Score implements Parcelable {
    private int id;
    private String course;
    private String mark;
    private float credit;
    private String category;
    private float grade;

    public Score(Parcel in) {
        id = in.readInt();
        course = in.readString();
        mark = in.readString();
        credit = in.readFloat();
        category = in.readString();
        grade = in.readFloat();
    }

    public Score() {
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

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public float getCredit() {
        return credit;
    }

    public void setCredit(float credit) {
        this.credit = credit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "Score{" +
                "id=" + id +
                ", course='" + course + '\'' +
                ", mark='" + mark + '\'' +
                ", credit=" + credit +
                ", category='" + category + '\'' +
                ", grade=" + grade +
                '}';
    }

    public static final Creator<Score> CREATOR = new Creator<Score>() {
        @Override
        public Score createFromParcel(Parcel source) {
            return new Score(source);
        }

        @Override
        public Score[] newArray(int size) {
            return new Score[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(course);
        dest.writeString(mark);
        dest.writeFloat(credit);
        dest.writeString(category);
        dest.writeFloat(grade);
    }
}
