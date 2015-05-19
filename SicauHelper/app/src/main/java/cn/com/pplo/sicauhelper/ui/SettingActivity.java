package cn.com.pplo.sicauhelper.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.util.DialogUtil;
import cn.com.pplo.sicauhelper.util.SharedPreferencesUtil;
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
    private Button changeColorBtn;

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
        changeColorBtn = (Button) findViewById(R.id.change_theme_btn);

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

        //更改颜色
        changeColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = null;
                int pos = 0;
                switch (SicauHelperApplication.getPrimaryColor(SettingActivity.this, false)){
                    case R.color.red_500:
                        pos = 0;
                    break;

                    case R.color.pink_500:
                        pos = 1;
                        break;
                    case R.color.purple_500:
                        pos = 2;
                        break;
                    case R.color.deep_purple_500:
                        pos = 3;
                        break;
                    case R.color.indigo_500:
                        pos = 4;
                        break;
                    case R.color.blue_500:
                        pos = 5;
                        break;
                    case R.color.light_blue_500:
                        pos = 6;
                        break;
                    case R.color.cyan_500:
                        pos = 7;
                        break;
                    case R.color.teal_500:
                        pos = 8;
                        break;
                    case R.color.green_500:
                        pos = 9;
                        break;
                    case R.color.light_green_500:
                        pos = 10;
                        break;
                    case R.color.deep_orange_500:
                        pos = 11;
                        break;
                    case R.color.brown_500:
                        pos = 12;
                        break;
                    case R.color.blue_grey_500:
                        pos = 13;
                        break;
                }
                dialog = new AlertDialog.Builder(SettingActivity.this).setSingleChoiceItems(R.array.theme_color, pos, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectColor = 0;
                        int darkColor = 0;
                        switch (which) {
                            //red
                            case 0:
                                selectColor = R.color.red_500;
                                darkColor = R.color.red_700;
                                break;
                            //pink
                            case 1:
                                selectColor = R.color.pink_500;
                                darkColor = R.color.pink_700;
                                break;
                                //purple
                            case 2:
                                selectColor = R.color.purple_500;
                                darkColor = R.color.purple_700;
                                break;
                            case 3:
                                selectColor = R.color.deep_purple_500;
                                darkColor = R.color.deep_purple_700;

                                break;
                            case 4:
                                selectColor = R.color.indigo_500;
                                darkColor = R.color.indigo_700;
                                break;
                            case 5:
                                selectColor = R.color.blue_500;
                                darkColor = R.color.blue_700;
                                break;
                            case 6:
                                selectColor = R.color.light_blue_500;
                                darkColor =  R.color.light_blue_700;
                                break;
                            case 7:
                                selectColor = R.color.cyan_500;
                                darkColor = R.color.cyan_700;
                                break;
                            case 8:
                                selectColor = R.color.teal_500;
                                darkColor = R.color.teal_700;
                                break;
                            case 9:
                                selectColor = R.color.green_500;
                                darkColor = R.color.green_700;
                                break;

                            case 10:
                                selectColor = R.color.light_green_500;
                                darkColor = R.color.light_green_700;
                                break;
                            case 11:
                                selectColor = R.color.deep_orange_500;
                                darkColor = R.color.deep_orange_700;
                                break;
                            case 12:
                                selectColor = R.color.brown_500;
                                darkColor = R.color.brown_700;
                                break;
                            case 13:
                                selectColor = R.color.blue_grey_500;
                                darkColor = R.color.blue_grey_700;
                                break;
                            case 14:
                                selectColor = -2412424;
                                darkColor =  -2412644;
                                break;
                        }
                        SharedPreferencesUtil.put(SettingActivity.this, SharedPreferencesUtil.PRIMARY_COLOR, selectColor);
                        SharedPreferencesUtil.put(SettingActivity.this, SharedPreferencesUtil.PRIMARY_DARK_COLOR, darkColor);

                        int color = SicauHelperApplication.getPrimaryColor(SettingActivity.this, true);
                        int color1 = SicauHelperApplication.getPrimaryDarkColor(SettingActivity.this, true);

                        if(Build.VERSION.SDK_INT >= 21) {
                            getWindow().setStatusBarColor(getResources().getColor(color1));
                            getWindow().setNavigationBarColor(getResources().getColor(color1));
                        }
                        UIUtil.setActionBarColor(SettingActivity.this, getSupportActionBar(), color);

                        dialog.dismiss();
                    }
                }).create();
                dialog.show();
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
