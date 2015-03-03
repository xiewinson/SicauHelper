package cn.com.pplo.sicauhelper.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.SharedPreferencesUtil;

public class SelectCourseActivity extends BaseActivity {

    public static void startSelectCourseActivity(Context context) {
        Intent intent = new Intent(context, SelectCourseActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_course);

        requestSelectCourseResult("300114474");
        requestSelectCourseResult("300114470");
        requestSelectCourseResult("300114698");
    }

    private void requestSelectCourseResult(String bianhao) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user", SharedPreferencesUtil.get(this, SharedPreferencesUtil.LOGIN_SID, "").toString());
        params.put("pwd", SharedPreferencesUtil.get(this, SharedPreferencesUtil.LOGIN_PSWD, "").toString());
        params.put("lb", "S");
        new NetUtil().getSelectCourseResutlHtmlStr(this, bianhao, params, new NetUtil.NetCallback(this) {
            @Override
            protected void onSuccess(String result) {
                Log.d("winson", "选课结果出来了");
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                Log.d("winson", "选课出问题：" + volleyError.getMessage());

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
