package cn.com.pplo.sicauhelper.model;

/**
 * Created by Administrator on 2014/9/16.
 */
public class Score {
    private int id;
    private String course;
    private String mark;
    private float credit;
    private String category;
    private float grade;

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
}
