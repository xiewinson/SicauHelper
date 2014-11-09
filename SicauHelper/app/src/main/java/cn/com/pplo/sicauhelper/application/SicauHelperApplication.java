package cn.com.pplo.sicauhelper.application;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.avos.avoscloud.AVOSCloud;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import cn.com.pplo.sicauhelper.model.Student;
import cn.com.pplo.sicauhelper.util.SQLiteUtil;
import cn.com.pplo.sicauhelper.util.SharedPreferencesUtil;

/**
 * Created by winson on 2014/9/13.
 */
public class SicauHelperApplication extends Application {
    private static RequestQueue requestQueue;
    private static Student student;
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
     * 取得登录Student对象，若
     * @param context
     * @return
     */
    public static Student getStudent(Context context) {
        if(student == null) {
            setStudent(SQLiteUtil.getLoginStudent(context, SharedPreferencesUtil.get(context, SharedPreferencesUtil.LOGIN_SID, "").toString()));
        }
        return student;
    }

    public static void setStudent(Student student) {
        SicauHelperApplication.student = student;
    }
}
