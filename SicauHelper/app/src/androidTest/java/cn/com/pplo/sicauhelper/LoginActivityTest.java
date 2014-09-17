package cn.com.pplo.sicauhelper;

import android.app.Activity;
import android.test.ActivityTestCase;

import cn.com.pplo.sicauhelper.ui.LoginActivity;
import cn.com.pplo.sicauhelper.util.NetUtil;

/**
 * Created by winson on 2014/9/13.
 */
public class LoginActivityTest extends ActivityTestCase {
    public void testLogin() {
        Activity activity = new LoginActivity();
        NetUtil.login(activity, "20118622", "winson");
    }
}
