package cn.com.pplo.sicauhelper.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.model.Student;
import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;


public class LoginActivity extends Activity {

    private EditText sidEt;
    private EditText pswdEt;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setUp();
    }

    private void setUp() {
        loginBtn = (Button) findViewById(R.id.login_ok_btn);
        sidEt = (EditText) findViewById(R.id.login_sid_et);
        pswdEt = (EditText) findViewById(R.id.login_pswd_et);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }

            //进行登录
            private void login() {
                final String sid = sidEt.getText().toString().trim();
                final String pswd = pswdEt.getText().toString().trim();
                Map<String, String> map = new HashMap<String, String>();
                map.put("user", sid);
                map.put("pwd", pswd);
                NetUtil.login(getApplicationContext(), map, new NetUtil.NetCallback(LoginActivity.this) {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            Log.d("winson", "登录" + result);
                            Student student = StringUtil.parseStudentInfo(result);
                            if (!student.getRole().equals("学生")) {
                                UIUtil.showShortToast(LoginActivity.this, "你又不是学生，就别凑热闹了，不送～");
                            } else {
                                saveStudent(student, sid, pswd);
                                goToMainActivity();
                            }
                        } catch (Exception e) {
                            UIUtil.showShortToast(LoginActivity.this, "你连学号和密码都忘了吗～那么，拜拜～");
                        }

                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                    }
                });
            }
        });

    }

    //存储学生信息到数据库和Application
    private void saveStudent(Student student, String sid, String pswd) {
        student.setSid(Long.parseLong(sid));
        student.setPswd(pswd);
        student.setHeadImg("http://jiaowu.sicau.edu.cn/photo/" + sid + ".jpg");
        SicauHelperApplication.setStudent(student);
    }

    //跳转到MainActivity
    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
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
}
