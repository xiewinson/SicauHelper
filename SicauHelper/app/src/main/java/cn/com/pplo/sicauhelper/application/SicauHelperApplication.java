package cn.com.pplo.sicauhelper.application;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import cn.com.pplo.sicauhelper.model.Student;

/**
 * Created by winson on 2014/9/13.
 */
public class SicauHelperApplication extends Application {
    private static RequestQueue requestQueue;
    private static Student student;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    //双重锁定单例模式获取请求队列
    public static RequestQueue getRequestQueue(Context context){
        if(requestQueue == null){
            synchronized (SicauHelperApplication.class){
                if(requestQueue == null){
                    requestQueue = Volley.newRequestQueue(context);
                }
            }
        }
        return requestQueue;
    }

    public static Student getStudent() {
        return student;
    }

    public static void setStudent(Student student) {
        SicauHelperApplication.student = student;
    }
}
