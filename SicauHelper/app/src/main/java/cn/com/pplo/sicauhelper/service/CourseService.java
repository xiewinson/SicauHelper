package cn.com.pplo.sicauhelper.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.model.Student;
import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;

public class CourseService extends Service {
    public CourseService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //此处需要修改
        Map<String, String> params = new HashMap<String, String>();
        Student student = SicauHelperApplication.getStudent();
        if (student != null) {
            params.put("user", student.getSid() + "");
            params.put("pwd", student.getPswd());
            NetUtil.getCourse2HtmlStr(getApplicationContext(), params, new NetUtil.NetCallbcak(getApplicationContext()) {
                @Override
                public void onResponse(String result) {
                    super.onResponse(result);
                    StringUtil.parseCourseInfo(result);
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    super.onErrorResponse(volleyError);
                }
            });
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
