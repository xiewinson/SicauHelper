package cn.com.pplo.sicauhelper.service;

import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Binder;
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
    private final IBinder mBinder = new CourseServiceBinder();

    public CourseService() {
    }

    public class CourseServiceBinder extends Binder {
        public CourseService getCourseService(){
            return CourseService.this;
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

    public void requestCourseInfo(final OnRequestFinishListener onRequestFinishListener) {
            Log.d("winson", "请求课程表-------------------------------------------------");
            //此处需要修改
            Map<String, String> params = new HashMap<String, String>();
            Student student = SicauHelperApplication.getStudent();
            if (student != null) {
                params.put("user", student.getSid() + "");
                params.put("pwd", student.getPswd());
                params.put("lb", "S");
                NetUtil.getCourse2HtmlStr(getApplicationContext(), params, new NetUtil.NetCallback(getApplicationContext()) {
                    @Override
                    public void onSuccess(String result) {
                        final List<Course> tempList = StringUtil.parseCourseInfo(result);
                        if(tempList != null && tempList.size() > 0){
                            new Thread(){
                                @Override
                                public void run() {
                                    long j = System.currentTimeMillis();
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
                                    Log.d("winson", "存的时间为：" + (System.currentTimeMillis() - j));
                                    onRequestFinishListener.onRequestFinish(true);
                                }
                            }.start();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                        onRequestFinishListener.onRequestFinish(false);
                    }
                });
            }
    }
}
