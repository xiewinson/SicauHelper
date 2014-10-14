package cn.com.pplo.sicauhelper.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.text.Html;
import android.util.Log;

import com.android.volley.VolleyError;

import java.util.List;

import cn.com.pplo.sicauhelper.model.Course;
import cn.com.pplo.sicauhelper.model.News;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;

public class NewsService extends Service {
    private IBinder mBinder = new NewsServiceBinder();
    public NewsService() {
    }

    public class NewsServiceBinder extends Binder {
        public NewsService getNewsService() {
            return NewsService.this;
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

    public void requestNewsList(final NewsCallback callback) {
        NetUtil.getNewsListHtmlStr(getApplicationContext(), null, new NetUtil.NetCallback(getApplicationContext()) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                callback.onFailure();
            }

            @Override
            public void onResponse(String result) {
                super.onResponse(result);
                final List<News> tempList =  StringUtil.parseNewsListInfo(result);
                callback.onSuccess(tempList);
                if(tempList != null && tempList.size() > 0) {
                    new Thread() {
                        @Override
                        public void run() {
                            for (int i = 0; i < tempList.size(); i++) {
                                ContentValues values = new ContentValues();
                                Log.d("winson", "_id: ----------------------" + tempList.get(i).getId());
                                values.put(TableContract.TableNews._ID, tempList.get(i).getId());
                                values.put(TableContract.TableNews._TITLE, tempList.get(i).getTitle());
                                values.put(TableContract.TableNews._DATE, tempList.get(i).getDate());
                                values.put(TableContract.TableNews._URL, tempList.get(i).getUrl());
                                values.put(TableContract.TableNews._CONTENT, tempList.get(i).getContent());
                                values.put(TableContract.TableNews._SRC, tempList.get(i).getSrc());
                                values.put(TableContract.TableNews._CATEGORY, tempList.get(i).getCategory());
                                getApplicationContext().getContentResolver().insert(Uri.parse(SicauHelperProvider.URI_NEWS_SINGLE), values);
                            }
                            callback.onSaveFinish(true);
//                            getApplicationContext().getContentResolver().notifyChange(Uri.parse(SicauHelperProvider.URI_NEWS_SINGLE), null);
                        }
                    }.start();
                }
            }
        });
    }

    public interface NewsCallback {
        public void onSuccess(List<News> data);
        public void onFailure();
        public void onSaveFinish(boolean isSaveSuccess);
    }
}
