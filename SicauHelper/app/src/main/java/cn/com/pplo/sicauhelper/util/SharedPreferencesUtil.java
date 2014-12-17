package cn.com.pplo.sicauhelper.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by winson on 2014/11/6.
 */
public class SharedPreferencesUtil {
    public static final String NAME = "SICAU_HELPER_SHARED";

    /**
     * 登录学号
     */
    public static final String LOGIN_SID = "LOGIN_SID";
    /**
     * 登录密码
     */
    public static final String LOGIN_PSWD = "LOGIN_PSWD";

    /**
     * 当前反馈数量
     */
    public static final String CURRENT_FEEDBACK_SIZE = "current_feedback_size";

    /**
     * 当前每次商品/帖子加载数量
     */
    public static final String PER_GOODS_STATUS_COUNT = "per_goods_status_count";

    /**
     * 当前每次评论加载数量
     */
    public static final String PER_COMMENT_COUNT = "per_comment_count";

    /**
     * 最后一次同步教室时间
     */
    public static final String LAST_SYNC_CLASSROMM = "last_sync_classroom";

    /**
     * 当前市场消息数量
     */
    public static final String CURRENT_GOODS_COMMENT_COUNT = "current_goods_comment_count";

    /**
     * 当前论坛消息数量
     */
    public static final String CURRENT_STATUS_COMMENT_COUNT = "current_status_comment_count";

    /**
     * 以xml保存键值对
     * @param context
     * @param key
     * @param value
     */
    public static void put(Context context, String key, Object value) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if(value instanceof String) {
            editor.putString(key, (String) value);
        }
        else if(value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        }
        else if(value instanceof Long) {
            editor.putLong(key, (Long) value);
        }
        editor.apply();
        editor.commit();
    }

    public static Object get(Context context, String key, Object defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        if(defaultValue instanceof String) {
            return sp.getString(key, (String) defaultValue);
        }
        else if(defaultValue instanceof Integer) {
            return sp.getInt(key, (Integer) defaultValue);
        }
        else if(defaultValue instanceof Long) {
            return sp.getLong(key, (Long) defaultValue);
        }
        else {
            return null;
        }
    }

}
