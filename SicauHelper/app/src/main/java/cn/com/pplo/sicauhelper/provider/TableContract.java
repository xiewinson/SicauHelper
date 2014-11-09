package cn.com.pplo.sicauhelper.provider;

import android.provider.BaseColumns;

/**
 * Created by winson on 2014/9/15.
 */
public class TableContract {
    /**
     * 成绩表
     */
    public interface TableScore extends BaseColumns{

        String TABLE_NAME = "t_score";

        String _COURSE = "_course";
        String _MARK = "_mark";
        String _CREDIT = "_credit";
        String _CATEGORY = "_category";
        String _GRADE = "_grade";
    }

    /**
     * 课程表
     */
    public interface TableCourse extends BaseColumns{

        String TABLE_NAME = "t_course";

        String _NAME = "_name";
        String _CATEGORY = "_category";
        String _CREDIT = "_credit";
        String _TIME = "_time";
        String _CLASSROOM = "_classroom";
        String _WEEK = "_week";
        String _TEACHER = "_teacher";
        String _SCHEDULENUM = "_scheduleNum";
        String _SELECTNUM = "_selectedNum";
    }

    /**
     * 新闻表
      */
    public interface TableNews extends BaseColumns {

        String TABLE_NAME = "t_news";

        String _TITLE = "_title";
        String _URL = "_url";
        String _CATEGORY = "_category";
        String _CONTENT = "_content";
        String _SRC = "_src";
        String _DATE = "_date";
    }

    /**
     * 空闲教室表
     */
    public interface TableClassroom extends BaseColumns {

        String TABLE_NAME = "t_classroom";

        String _NAME = "_name";
        String _TIME = "_time";
        String _SCHOOL = "_school";
    }

    /**
     * 学生表
     */
    public interface TableStudent extends BaseColumns {

        public static final String TABLE_NAME = "Student";

        public static final String _SID = "sid";
        public static final String _NAME = "name";
        public static final String _NICKNAME = "nickName";
        public static final String _PSWD = "pswd";
        public static final String _SCHOOL = "school";
        public static final String _PROFILE_URL = "profileUrl";
        public static final String _BACKGROUND = "background";
        public static final String _ROLE = "role";
    }

}
