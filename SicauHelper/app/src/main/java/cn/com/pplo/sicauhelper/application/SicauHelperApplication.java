package cn.com.pplo.sicauhelper.application;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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

    @Override
    public void onCreate() {
        super.onCreate();

        AVOSCloud.initialize(getApplicationContext(), "gu8z4el96hz1rkljtsiytkatoxtjesia6fl2aoliahdfczku", "1vbe06cd1u430vjzz0t8s6wfyk6t3kimesc85mtplln69p5z");
        sicauHelperApplication = getInstance();
        //初始化ImageLoader配置
        ImageLoaderConfiguration cofig = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
        ImageLoader.getInstance().init(cofig);

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
        return student;
    }
}
