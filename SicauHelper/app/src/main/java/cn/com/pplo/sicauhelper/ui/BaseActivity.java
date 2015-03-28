package cn.com.pplo.sicauhelper.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.impl.client.DefaultHttpClient;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.util.UIUtil;

/**
 * Created by Administrator on 2014/9/25.
 */
public class BaseActivity extends ActionBarActivity {
    protected RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        UIUtil.setActionBarColor(this, getSupportActionBar(), R.color.color_primary);

//        getActionBar().setDisplayShowHomeEnabled(false);
        requestQueue = Volley.newRequestQueue(this, new HttpClientStack(new DefaultHttpClient()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
