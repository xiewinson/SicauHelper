package cn.com.pplo.sicauhelper.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.Html;

import com.android.volley.VolleyError;

import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;

public class NewsService extends Service {
    public NewsService() {
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
        NetUtil.getNewsListHtmlStr(getApplicationContext(), null, new NetUtil.NetCallback(getApplicationContext()) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
            }

            @Override
            public void onResponse(String result) {
                super.onResponse(result);
                StringUtil.parseNewsListInfo(result);
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
}
