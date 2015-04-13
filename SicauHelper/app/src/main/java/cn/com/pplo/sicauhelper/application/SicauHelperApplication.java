package cn.com.pplo.sicauhelper.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.model.Student;
import cn.com.pplo.sicauhelper.util.SharedPreferencesUtil;

/**
 * Created by winson on 2014/9/13.
 */
public class SicauHelperApplication extends Application {
    private static RequestQueue requestQueue;
    private static AVUser student;
    private static SicauHelperApplication sicauHelperApplication;
    private static Object lockObj = new Object();
    private static int primaryColor = -1;
    private static int primaryDarkColor = -1;

    /**
     * 取得主色调
     * @return
     */
    public static int getPrimaryColor(Context ctx, boolean reGet) {
        if(primaryColor == -1 || reGet) {
            primaryColor = (int) SharedPreferencesUtil.get(ctx, SharedPreferencesUtil.PRIMARY_COLOR, R.color.red_500);
            if(primaryColor == 0) {
                primaryColor = R.color.red_500;
            }
            try {
                ctx.getResources().getColor(primaryColor);
            }catch (Exception e) {
                primaryColor = R.color.red_500;
            }
        }

        return primaryColor;
    }

    /**
     * 取得主色调
     * @return
     */
    public static int getPrimaryDarkColor(Context ctx, boolean reGet) {
        if(primaryDarkColor == -1 || reGet) {
            primaryDarkColor = (int) SharedPreferencesUtil.get(ctx, SharedPreferencesUtil.PRIMARY_DARK_COLOR, R.color.red_700);
            if(primaryDarkColor == 0) {
                primaryDarkColor = R.color.red_700;
            }
            try {
                ctx.getResources().getColor(primaryDarkColor);
            }catch (Exception e) {
                primaryDarkColor = R.color.red_700;
            }
        }

        return primaryDarkColor;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AVOSCloud.initialize(getApplicationContext(), "gu8z4el96hz1rkljtsiytkatoxtjesia6fl2aoliahdfczku", "1vbe06cd1u430vjzz0t8s6wfyk6t3kimesc85mtplln69p5z");
        sicauHelperApplication = getInstance();
        //初始化ImageLoader配置
        ImageLoaderConfiguration cofig = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
        ImageLoader.getInstance().init(cofig);
        student = AVUser.getCurrentUser();
    }

    public SicauHelperApplication() {

    }

    /**
     * 单例模式取得实例
     * @return
     */
    public static SicauHelperApplication getInstance(){
        if (sicauHelperApplication == null) {
            synchronized (lockObj) {
                if (sicauHelperApplication == null) {
                    sicauHelperApplication = new SicauHelperApplication();
                }
            }
        }
        return sicauHelperApplication;
    }

    //双重锁定单例模式获取请求队列
    public RequestQueue getRequestQueue(Context context) {
        if (requestQueue == null) {
            synchronized (lockObj) {
                if (requestQueue == null) {
                    requestQueue = Volley.newRequestQueue(context);
                }
            }
        }
        return requestQueue;
    }

    /**
     * 取得登录Student对象，若为null则重新取得
     * @return
     */
    public static AVUser getStudent() {
        if(student == null) {
            student = AVUser.getCurrentUser();
        }
        if(student != null) {
            student.fetchInBackground(null);
        }
        return student;
    }

    /**
     * 设置其为
     */
    public static void clearStudent() {
        student = null;
    }
}
