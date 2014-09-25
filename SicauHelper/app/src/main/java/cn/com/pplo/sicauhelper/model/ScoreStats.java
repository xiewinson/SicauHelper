package cn.com.pplo.sicauhelper.model;

/**
 * Created by winson on 2014/9/22.
 */
public class ScoreStats {
    private String year;
    private float mustAvgScore;
    private float choiceAvgScore;
    private float mustCredit;
    private float choiceCredit;
    private int mustNum;
    private int choiceNum;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public float getMustAvgScore() {
        return mustAvgScore;
    }

    public void setMustAvgScore(float mustAvgScore) {
        this.mustAvgScore = mustAvgScore;
    }

    public float getChoiceAvgScore() {
        return choiceAvgScore;
    }

    public void setChoiceAvgScore(float choiceAvgScore) {
        this.choiceAvgScore = choiceAvgScore;
    }

    public float getMustCredit() {
        return mustCredit;
    }

    public void setMustCredit(float mustCredit) {
        this.mustCredit = mustCredit;
    }

    public float getChoiceCredit() {
        return choiceCredit;
    }

    public void setChoiceCredit(float choiceCredit) {
        this.choiceCredit = choiceCredit;
    }

    public int getMustNum() {
        return mustNum;
    }

    public void setMustNum(int mustNum) {
        this.mustNum = mustNum;
    }

    public int getChoiceNum() {
        return choiceNum;
    }

    public void setChoiceNum(int choiceNum) {
        this.choiceNum = choiceNum;
    }

    @Override
    public String toString() {
        return "ScoreStats{" +
                "year='" + year + '\'' +
                ", mustAvgScore=" + mustAvgScore +
                ", choiceAvgScore=" + choiceAvgScore +
                ", mustCredit=" + mustCredit +
                ", choiceCredit=" + choiceCredit +
                ", mustNum=" + mustNum +
                ", choiceNum=" + choiceNum +
                '}';
    }
}
