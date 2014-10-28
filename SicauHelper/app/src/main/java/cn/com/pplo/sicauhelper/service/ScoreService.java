package cn.com.pplo.sicauhelper.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.model.Score;
import cn.com.pplo.sicauhelper.model.Student;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;

public class ScoreService extends Service {
    private IBinder mBinder = new ScoreServiceBinder();

    public ScoreService() {
    }

    public class ScoreServiceBinder extends Binder {
       public ScoreService getScoreService(){
            return ScoreService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void requestScoreInfo(final OnRequestFinishListener listener) {
        //此处需要修改
        Map<String, String> params = new HashMap<String, String>();
        Student student = SicauHelperApplication.getStudent();
        if (student != null) {
            params.put("user", student.getSid() + "");
            params.put("pwd", student.getPswd());
            params.put("lb", "S");

            NetUtil.getScoreHtmlStr(getApplicationContext(), params, new NetUtil.NetCallback(getApplicationContext()) {
                @Override
                public void onSuccess(String result) {
                    StringUtil.parseScoreInfo(result, new StringUtil.Callback() {
                        @Override
                        public void handleParseResult(Object obj) {


                        }
                    });
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    listener.onRequestFinish(false);
                    super.onErrorResponse(volleyError);
                }
            });
        }
    }
}
