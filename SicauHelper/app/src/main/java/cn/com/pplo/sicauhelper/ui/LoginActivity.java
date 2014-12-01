package cn.com.pplo.sicauhelper.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SignUpCallback;

import java.util.HashMap;
import java.util.Map;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.action.UserAction;
import cn.com.pplo.sicauhelper.model.Student;
import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.SharedPreferencesUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;


public class LoginActivity extends ActionBarActivity {

    private EditText sidEt;
    private EditText pswdEt;
    private Button loginBtn;
    private AlertDialog progressDialog;

    public static void startLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_login);

        setUp();
    }

    private void setUp() {
        loginBtn = (Button) findViewById(R.id.login_ok_btn);
        sidEt = (EditText) findViewById(R.id.login_sid_et);
        pswdEt = (EditText) findViewById(R.id.login_pswd_et);
        progressDialog = UIUtil.getProgressDialog(this, "吾已前往教务系统验证你的学号密码");

        //设置保存在xml的学号密码
        sidEt.setText(SharedPreferencesUtil.get(this, SharedPreferencesUtil.LOGIN_SID, "").toString());
        pswdEt.setText(SharedPreferencesUtil.get(this, SharedPreferencesUtil.LOGIN_PSWD, "").toString());
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }

            //进行登录
            private void login() {
                progressDialog.show();
                final String sid = sidEt.getText().toString().trim();
                final String pswd = pswdEt.getText().toString().trim();
                Map<String, String> map = new HashMap<String, String>();
                map.put("user", sid);
                map.put("pwd", pswd);
                map.put("lb", "S");
                NetUtil.login(getApplicationContext(), map, new NetUtil.NetCallback(LoginActivity.this) {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            Log.d("winson", "登录" + result);
                            Student student = StringUtil.parseStudentInfo(result);
                            logInOrSignUpStudent(student, sid, pswd);

                        } catch (Exception e) {
                            UIUtil.dismissProgressDialog(progressDialog);
                            UIUtil.showShortToast(LoginActivity.this, "你连学号和密码都忘了吗～那么，拜拜～");
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                        UIUtil.dismissProgressDialog(progressDialog);
                    }
                });
            }
        });

    }

    //存储学生信息到数据库和Application
    private void logInOrSignUpStudent(final Student student, final String sid, final String pswd) {
        //首先保存到xml
        saveSidPswdToXML(LoginActivity.this, sid, pswd);
        student.setSid(sid);
        student.setPswd(pswd);
        student.setProfileUrl("http://jiaowu.sicau.edu.cn/photo/" + sid + ".jpg");
        student.setRole(1);
        student.setBackground("pic_0");

        //登录
        new UserAction().logIn(sid, sid, new LogInCallback() {
            @Override
            public void done(AVUser avUser, AVException e) {
                //若已存在则跳转到主页面
                if (e == null) {
                    startMainActivity();
                }
                //若姓名为空
                else if(TextUtils.isEmpty(student.getName())) {
                    UIUtil.showShortToast(LoginActivity.this, "你先去教务系统进行评教后再来使用吧");
                    return;
                }
                //若不存在，则创建用户
                else {
                    //新建用户时将用户名设为用户昵称
                    student.setNickName(student.getName());
                    new UserAction().signUp(student, new SignUpCallback() {
                        @Override
                        public void done(AVException e) {

                            //创建成功
                            if (e == null) {
                                startMainActivity();
                            }
                            else {
                                UIUtil.showShortToast(LoginActivity.this, "出现了一个未知的错误");
                                Log.d("winson", e.getMessage());
                                UIUtil.dismissProgressDialog(progressDialog);
                            }
                        }
                    });
                }
            }
        });

    }

    /**
     * 存储数据并跳转到主页面
     */
    private void startMainActivity() {

        UIUtil.dismissProgressDialog(progressDialog);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
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
        return super.onOptionsItemSelected(item);
    }

    /**
     * 保存用户密码
     * @param context
     * @param sid
     * @param pswd
     */
    private void saveSidPswdToXML(Context context, String sid, String pswd) {
        SharedPreferencesUtil.put(context, SharedPreferencesUtil.LOGIN_SID, sid);
        SharedPreferencesUtil.put(context, SharedPreferencesUtil.LOGIN_PSWD, pswd);
    }
}
