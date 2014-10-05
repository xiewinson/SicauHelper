package cn.com.pplo.sicauhelper.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.model.Course;
import cn.com.pplo.sicauhelper.model.Student;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;

public class CourseService extends Service {
    private boolean isServing = false;
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
        isServing = true;
        Log.d("winson", "服务开始创建-------------------------------------------------");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(isServing = true){
            isServing = false;
            Log.d("winson", "服务开始使用-------------------------------------------------");
            //此处需要修改
            Map<String, String> params = new HashMap<String, String>();
            Student student = SicauHelperApplication.getStudent();
            if (student != null) {
                params.put("user", student.getSid() + "");
                params.put("pwd", student.getPswd());
                NetUtil.getCourse2HtmlStr(getApplicationContext(), params, new NetUtil.NetCallback(getApplicationContext()) {
                    @Override
                    public void onResponse(String result) {
                        super.onResponse(result);
                        final List<Course> tempList = StringUtil.parseCourseInfo(result);
                        if(tempList != null && tempList.size() > 0){
                            new Thread(){
                                @Override
                                public void run() {
                                    for(int i = 0; i < tempList.size(); i++){
                                        ContentValues values = new ContentValues();
                                        values.put(TableContract.TableCourse._NAME, tempList.get(i).getName());
                                        values.put(TableContract.TableCourse._CATEGORY, tempList.get(i).getCategory());
                                        values.put(TableContract.TableCourse._CREDIT, tempList.get(i).getCredit());
                                        values.put(TableContract.TableCourse._TIME, tempList.get(i).getTime());
                                        values.put(TableContract.TableCourse._CLASSROOM, tempList.get(i).getClassroom());
                                        values.put(TableContract.TableCourse._WEEK, tempList.get(i).getWeek());
                                        values.put(TableContract.TableCourse._TEACHER, tempList.get(i).getTeacher());
                                        values.put(TableContract.TableCourse._SCHEDULENUM, tempList.get(i).getScheduleNum());
                                        values.put(TableContract.TableCourse._SELECTNUM, tempList.get(i).getSelectedNum());
                                        getApplicationContext().getContentResolver().insert(Uri.parse(SicauHelperProvider.URI_COURSE_ALL), values);
                                    }
                                    getApplicationContext().getContentResolver().notifyChange(Uri.parse(SicauHelperProvider.URI_COURSE_ALL), null);
                                }
                            }.start();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                        stopSelf();
                    }
                });
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
