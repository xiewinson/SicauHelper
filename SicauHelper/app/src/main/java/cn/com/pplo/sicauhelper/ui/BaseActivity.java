package cn.com.pplo.sicauhelper.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Administrator on 2014/9/25.
 */
public class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActionBar().setDisplayShowHomeEnabled(false);
    }
}
