package cn.com.pplo.sicauhelper.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
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
    public ScoreService() {
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
            
            NetUtil.getScoreHtmlStr(getApplicationContext(), params, new NetUtil.NetCallbcak(getApplicationContext()) {
                @Override
                public void onResponse(String result) {
                    super.onResponse(result);
                    StringUtil.parseScoreInfo(result, new StringUtil.Callback() {
                        @Override
                        public void handleParseResult(final List<Score> tempList) {
                            if (tempList != null && tempList.size() > 0) {
                                new Thread(){
                                    @Override
                                    public void run() {
                                        super.run();
                                        //此处仍将修改
                                        for(int i = 0; i < tempList.size(); i++){
                                            ContentValues values = new ContentValues();
                                            values.put(TableContract.TableScore._CATEGORY, tempList.get(i).getCategory());
                                            values.put(TableContract.TableScore._COURSE, tempList.get(i).getCourse());
                                            values.put(TableContract.TableScore._CREDIT, tempList.get(i).getCredit());
                                            values.put(TableContract.TableScore._GRADE, tempList.get(i).getGrade());
                                            values.put(TableContract.TableScore._MARK, tempList.get(i).getMark());
                                            getApplicationContext().getContentResolver().insert(Uri.parse(SicauHelperProvider.URI_SCORE_ALL), values);
                                        }
                                        getApplicationContext().getContentResolver().notifyChange(Uri.parse(SicauHelperProvider.URI_SCORE_ALL), null);
                                    }
                                }.start();
                            }

                        }
                    });
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
