package cn.com.pplo.sicauhelper.util;

import com.avos.avoscloud.AVUser;

import cn.com.pplo.sicauhelper.provider.TableContract;

/**
 * Created by winson on 2014/11/18.
 */
public class UserUtil {
    public static boolean isAdmin(AVUser avUser) {
        boolean result = false;
        int roleId = avUser.getInt(TableContract.TableUser._ROLE);
        if(roleId == 0) {
            result = true;
        }
        return result;
    }
}
