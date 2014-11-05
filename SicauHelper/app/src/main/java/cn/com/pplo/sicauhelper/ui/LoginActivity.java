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
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.leancloud.AVStudent;
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
                map.put("lb", "S");
                NetUtil.login(getApplicationContext(), map, new NetUtil.NetCallback(LoginActivity.this) {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            Log.d("winson", "登录" + result);
                            Student student = StringUtil.parseStudentInfo(result);
                            saveStudent(student, sid, pswd);
                            goToMainActivity();
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
    private void saveStudent(Student student, final String sid, final String pswd) {
        student.setSid(sid);
        student.setPswd(pswd);
        student.setProfileUrl("http://jiaowu.sicau.edu.cn/photo/" + sid + ".jpg");
        student.setRole(1);
        student.setBackground("http://jiaowu.sicau.edu.cn/photo/" + sid + ".jpg");

        //TODO 存储到本地数据库
        SicauHelperApplication.getInstance().setStudent(student);
        Log.d("winson", student + "");
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
