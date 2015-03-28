package cn.com.pplo.sicauhelper.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.util.DialogUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;
import cn.com.pplo.sicauhelper.util.UserUtil;

public class SettingActivity extends BaseActivity {

    public static void startSettingActivity(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    private Button goodsStatusCountBtn;
    private Button commentCountBtn;
    private Button updateBtn;
    private Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setUp(this);
    }

    private void setUp(Context context) {
        getSupportActionBar().setTitle("设置");

        goodsStatusCountBtn = (Button) findViewById(R.id.goods_status_count_btn);
        commentCountBtn = (Button) findViewById(R.id.comment_count_btn);
        updateBtn = (Button) findViewById(R.id.update_btn);
        logoutBtn = (Button) findViewById(R.id.logout_btn);

        //商品帖子每次加载数量
        goodsStatusCountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showCountEditDialog(SettingActivity.this, DialogUtil.TYPE_COUNT_EDIT.GOODS_STATUS);
            }
        });

        //评论每次加载数量
        commentCountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showCountEditDialog(SettingActivity.this, DialogUtil.TYPE_COUNT_EDIT.COMMENT);
            }
        });

        //检查更新
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
                    @Override
                    public void onUpdateReturned(int i, UpdateResponse updateResponse) {
                        if(i == UpdateStatus.No) {
                            UIUtil.showShortToast(SettingActivity.this, "没有新的更新啊");
                        }
                    }
                });
                UmengUpdateAgent.forceUpdate(SettingActivity.this);
            }
        });

        //注销
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserUtil.clearUserInfo(SettingActivity.this);
                LoginActivity.startLoginActivity(SettingActivity.this);
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
