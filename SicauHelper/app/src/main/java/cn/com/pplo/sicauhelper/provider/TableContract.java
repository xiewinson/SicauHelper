package cn.com.pplo.sicauhelper.provider;

import android.provider.BaseColumns;

/**
 * Created by winson on 2014/9/15.
 */
public class TableContract {
    public interface TableScore extends BaseColumns{

        String TABLE_NAME = "t_score";

        String _COURSE = "_course";
        String _MARK = "_mark";
        String _CREDIT = "_credit";
        String _CATEGORY = "_category";
        String _GRADE = "_grade";
    }

    public interface TableCourse extends BaseColumns{

        String TABLE_NAME = "t_course";

        String _NAME = "name";
        String _CATEGORY = "category";
        String _CREDIT = "credit";
        String _TIME = "time";
        String _CLASSROOM = "classroom";
        String _WEEK = "week";
        String _TEACHER = "teacher";
        String _SCHEDULENUM = "scheduleNum";
        String _SELECTNUM = "selectedNum";
    }

}
