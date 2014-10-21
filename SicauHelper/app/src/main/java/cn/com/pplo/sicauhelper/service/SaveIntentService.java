package cn.com.pplo.sicauhelper.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import cn.com.pplo.sicauhelper.model.News;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.provider.TableContract;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SaveIntentService extends IntentService {
    private static final String ACTION_NEWS_ALL = "cn.com.pplo.sicauhelper.service.action.news_all";

    private static final String EXTRA_NEWS_LIST = "cn.com.pplo.sicauhelper.service.extra.newses";
    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionNewsAll(Context context, List<News> newsList) {
        Intent intent = new Intent(context, SaveIntentService.class);
        intent.setAction(ACTION_NEWS_ALL);
        intent.putParcelableArrayListExtra(EXTRA_NEWS_LIST, (java.util.ArrayList<? extends android.os.Parcelable>) newsList);
        context.startService(intent);
    }



    public SaveIntentService() {
        super("SaveIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_NEWS_ALL.equals(action)) {
                ArrayList<News> newsList = intent.getParcelableArrayListExtra(EXTRA_NEWS_LIST);
                handleActionNewsList(newsList);
            }
        }
    }

    private void handleActionNewsList(ArrayList<News> newsList) {
        if(newsList != null){
            for (int i = 0; i < newsList.size(); i++) {
                ContentValues values = new ContentValues();
                values.put(TableContract.TableNews._ID, newsList.get(i).getId());
                values.put(TableContract.TableNews._TITLE, newsList.get(i).getTitle());
                values.put(TableContract.TableNews._DATE, newsList.get(i).getDate());
                values.put(TableContract.TableNews._URL, newsList.get(i).getUrl());
                values.put(TableContract.TableNews._CONTENT, newsList.get(i).getContent());
                values.put(TableContract.TableNews._SRC, newsList.get(i).getSrc());
                values.put(TableContract.TableNews._CATEGORY, newsList.get(i).getCategory());
                getApplicationContext().getContentResolver().insert(Uri.parse(SicauHelperProvider.URI_NEWS_SINGLE), values);
            }
        }
    }



}
