package cn.com.pplo.sicauhelper.ui;

import android.app.Activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.feedback.Comment;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.avos.avoscloud.feedback.FeedbackThread;
import com.umeng.update.UmengUpdateAgent;

import java.util.List;

import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.ui.fragment.ClassroomFragment;
import cn.com.pplo.sicauhelper.ui.fragment.CourseFragment;
import cn.com.pplo.sicauhelper.ui.fragment.MarketFragment;
import cn.com.pplo.sicauhelper.ui.fragment.MessageDrawerFragment;
import cn.com.pplo.sicauhelper.ui.fragment.NavigationDrawerFragment;
import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.ui.fragment.NewsFragment;
import cn.com.pplo.sicauhelper.ui.fragment.ScoreFragment;
import cn.com.pplo.sicauhelper.ui.fragment.StatusFragment;
import cn.com.pplo.sicauhelper.util.SharedPreferencesUtil;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private MessageDrawerFragment messageDrawerFragment;
    private DrawerLayout mDrawerLayout;
    /**
     * 选择哪个fragment
     */
    private int drawerPosition = 0;
    private int drawerPositionCopy = 0;

    /**
     * fragment应在哪个位置
     */
    public static final String SAVE_DRAWER_POSITION = "save_drawer_position";


    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * 启动主页面
     * @param context
     */
    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //不使用up
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setElevation(0);

        if (SicauHelperApplication.getStudent() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        } else {
            //更新user
            AVUser.getCurrentUser().fetchInBackground(null);
            if(savedInstanceState != null) {
                int savedPosition = savedInstanceState.getInt(SAVE_DRAWER_POSITION, 0);
                drawerPosition = savedPosition;
                drawerPositionCopy = savedPosition;
            }
            //启动更新
            UmengUpdateAgent.setUpdateOnlyWifi(false);
            UmengUpdateAgent.update(this);
            initView();
        }
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = NavigationDrawerFragment.newInstance(drawerPosition);

        messageDrawerFragment = new MessageDrawerFragment();
        mTitle = getTitle();
        // Set up the drawer.
        getSupportFragmentManager().beginTransaction().add(R.id.navigation_drawer, mNavigationDrawerFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.message_drawer, messageDrawerFragment).commit();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setUp(mDrawerLayout, this);
        receiveFeedback();
    }

    /**
     * 接收反馈
     */
    private void receiveFeedback() {
        FeedbackAgent agent = new FeedbackAgent(this);
        final FeedbackThread feedbackThread = agent.getDefaultThread();
        feedbackThread.sync(new FeedbackThread.SyncCallback() {
            @Override
            public void onCommentsSend(List<Comment> comments, AVException e) {
                Log.d("winson", "当前发送消息数量： " + comments.size());
            }

            @Override
            public void onCommentsFetch(List<Comment> comments, AVException e) {
                Log.d("winson", "当前消息数量： " + comments.size());
                int currentSize = (Integer) SharedPreferencesUtil.get(MainActivity.this, SharedPreferencesUtil.CURRENT_FEEDBACK_SIZE, 0);
                Log.d("winson", "存储的数量：" + currentSize);
                if (feedbackThread.getCommentsList().size() > currentSize) {
                    Log.d("winson", "你有未查收的新消息");
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this);
                    Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentTitle("川农校园通回复了您的反馈")
                            .setContentText("点击查看")
                            .setTicker("川农校园通回复了您的反馈")
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentIntent(pi)
                            .setAutoCancel(true);
                    notificationManager.notify(0, builder.build());
                } else {
                    Log.d("winson", "没有新消息");
                }

            }
        });
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     */
    public void setUp(DrawerLayout mDrawerLayout, final Activity activity) {
//        mFragmentContainerView = getActivity().findViewById(fragmentId);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        final ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                activity,                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(mTitle);
                selectFragment();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(!getSupportActionBar().isShowing()){
                    getSupportActionBar().show();
                }
                getSupportActionBar().setTitle("川农校园通");
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
//        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
//            mDrawerLayout.openDrawer(mFragmentContainerView);
//        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        selectFragment();
    }

    /**
     * 选择drawerPosition所在的fragment
     */
    private void selectFragment() {
        Fragment fragment = null;
        switch (drawerPosition) {

            //课程
            case 0:
                fragment = CourseFragment.newInstance(CourseFragment.TYPE_COURSE_THEORY);
                break;
            //实验课程
            case 1:
                fragment = CourseFragment.newInstance(CourseFragment.TYPE_COURSE_LAB);
                break;
            //成绩
            case 2:
                fragment = ScoreFragment.newInstance();
                break;
            //新闻
            case 3:
                fragment = NewsFragment.newInstance();
                break;
            //空教室
            case 4:
                fragment = ClassroomFragment.newInstance();
                break;
            //市场模块
            case 5:
                fragment = MarketFragment.newInstance();
                break;
            //圈子模块
            case 6:
                fragment = StatusFragment.newInstance();
                break;
            case -1:
                return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

        drawerPosition = -1;
    }

    public void onSectionAttached(String title) {
        mTitle = title;
    }

    //选择不同的导航抽屉item
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        drawerPosition = position;
        drawerPositionCopy = position;

        if(mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVE_DRAWER_POSITION, drawerPositionCopy);
        super.onSaveInstanceState(outState);
    }

    //在每次重新创建菜单时重新调用一次，并在此时更新
    public void restoreActionBar() {
        Log.d("winson", "重新设置名字：" + mTitle);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        restoreActionBar();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //点击导航键开启/关闭抽屉
        if(id == android.R.id.home){
            if(mDrawerLayout.isDrawerOpen(GravityCompat.END)){
                mDrawerLayout.closeDrawer(GravityCompat.END);
            }

            if(!mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            else {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }

        }
        else if(id == R.id.action_message) {
            if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
            if(!mDrawerLayout.isDrawerOpen(GravityCompat.END)){
                mDrawerLayout.openDrawer(GravityCompat.END);
            }
            else {
                mDrawerLayout.closeDrawer(GravityCompat.END);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
