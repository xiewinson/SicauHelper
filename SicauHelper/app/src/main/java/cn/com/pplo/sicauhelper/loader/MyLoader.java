package cn.com.pplo.sicauhelper.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;


import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;

/**
 * Created by winson on 2014/9/19.
 */
public class MyLoader implements LoaderManager.LoaderCallbacks<Cursor> {
    private Context context;
    public MyLoader(Context context){
        this.context = context;
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Log.d("winson", "Loader创建");
        return new CursorLoader(context, Uri.parse(SicauHelperProvider.URI_SCORE_ALL), null, null, null, null){
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("winson", "Loader加载完成");
        if(data != null) {
            Log.d("winson", "这次加载了" + data.getCount() + "条数据");
        }
        else {
            Log.d("winson", "没有取到数据");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d("winson", "Loader重置");
    }


}
