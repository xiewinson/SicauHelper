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
     * 实验课表
     */
    public interface TableLabCourse extends BaseColumns{

        String TABLE_NAME = "t_lab_course";

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
    public interface TableUser extends BaseColumns {

        String TABLE_NAME = "_User";

        String _SID = "sid";
        String _OBJECTID = "objectId";
        String _NAME = "name";
        String _NICKNAME = "nickName";
        String _PSWD = "pswd";
        String _SCHOOL = "school";
        String _PROFILE_URL = "profileUrl";
        String _BACKGROUND = "background";
        String _ROLE = "role";
        String _CREATED_AT = "createdAt";
        String _UPDATED_AT = "updatedAt";
    }

    /**
     * 商品表
     */
    public interface TableGoods extends BaseColumns {

        String TABLE_NAME = "Goods";

        String _OBJECTID = "objectId";
        String _CATEGORY = "category";
        String _SCHOOL = "school";
        String _TITLE = "title";
        String _CONTENT = "content";
        String _PRICE = "price";
        String _USER = "user";
        String _MODEL = "model";
        String _BRAND = "brand";
        String _VERSION = "version";
        String _LONGITUDE = "longitude";
        String _LATITUDE = "latitude";
        String _ADDRESS = "address";
        String _CREATED_AT = "createdAt";
        String _UPDATED_AT = "updatedAt";
        String _COMMENT_COUNT = "commentCount";
        String _GOODS_ID = "goods_id";
    }

    /**
     * 商品评论表
     */
    public interface TableGoodsComment extends BaseColumns {

        String TABLE_NAME = "GoodsComment";

        String _SEND_USER = "sendUser";
        String _RECEIVE_USER = "receiveUser";
        String _CONTENT = "content";
        String _GOODS = "goods";
        String _GOODS_COMMENT_ID = "comment_id";
        String _IS_WATCHED = "isWatched";
        String _INVISIBLE = "invisible";
        String _MODEL = "model";
        String _BRAND = "brand";
        String _VERSION = "version";

        String _OBJECTID = "objectId";
        String _CREATED_AT = "createdAt";
        String _UPDATED_AT = "updatedAt";
    }

    /**
     * 论坛表
     */
    public interface TableStatus extends BaseColumns {

        String TABLE_NAME = "Status";

        String _OBJECTID = "objectId";
        String _TITLE = "title";
        String _CONTENT = "content";
        String _USER = "user";
        String _MODEL = "model";
        String _BRAND = "brand";
        String _VERSION = "version";
        String _LONGITUDE = "longitude";
        String _LATITUDE = "latitude";
        String _ADDRESS = "address";
        String _CREATED_AT = "createdAt";
        String _UPDATED_AT = "updatedAt";
        String _COMMENT_COUNT = "commentCount";
        String _STATUS_ID = "status_id";
    }

    /**
     * 论坛评论表
     */
    public interface TableStatusComment extends BaseColumns {

        String TABLE_NAME = "StatusComment";

        String _SEND_USER = "sendUser";
        String _RECEIVE_USER = "receiveUser";
        String _CONTENT = "content";
        String _STATUS = "status";
        String _STATUS_COMMENT_ID = "comment_id";
        String _IS_WATCHED = "isWatched";
        String _INVISIBLE = "invisible";
        String _MODEL = "model";
        String _BRAND = "brand";
        String _VERSION = "version";

        String _OBJECTID = "objectId";
        String _CREATED_AT = "createdAt";
        String _UPDATED_AT = "updatedAt";
    }
}
