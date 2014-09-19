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
}
