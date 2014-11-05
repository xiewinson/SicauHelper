package cn.com.pplo.sicauhelper.ui;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.model.Student;
import cn.com.pplo.sicauhelper.ui.fragment.ClassroomFragment;
import cn.com.pplo.sicauhelper.ui.fragment.CourseFragment;
import cn.com.pplo.sicauhelper.ui.fragment.MarketFragment;
import cn.com.pplo.sicauhelper.ui.fragment.NavigationDrawerFragment;
import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.ui.fragment.NewsFragment;
import cn.com.pplo.sicauhelper.ui.fragment.ScoreFragment;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //不使用up
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        Student s = new Student();
//        s.setName("谢豪");
//        s.setSid(20118622);
//        s.setPswd("winson");
//        s.setSchool(2);
//        SicauHelperApplication.setStudent(s);
        if (SicauHelperApplication.getStudent() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        } else {
            initView();
        }
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = new NavigationDrawerFragment();
        mTitle = getTitle();
        // Set up the drawer.
        getSupportFragmentManager().beginTransaction().add(R.id.navigation_drawer, mNavigationDrawerFragment).commit();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setUp(mDrawerLayout, this);
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
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(!getSupportActionBar().isShowing()){
                    getSupportActionBar().show();
                }
                getSupportActionBar().setTitle("川农生活助手");
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
    }

    public void onSectionAttached(String title) {
        mTitle = title;
    }

    //选择不同的导航抽屉item
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {

            //课程
            case 0:
                fragment = CourseFragment.newInstance();
                break;
            //成绩
            case 1:
                fragment = ScoreFragment.newInstance();
                break;
            //新闻
            case 2:
                fragment = NewsFragment.newInstance();
                break;
            //空教室
            case 3:
                fragment = ClassroomFragment.newInstance();
                break;
                //首页
            case 4:
                fragment = MarketFragment.newInstance();
                break;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        if(mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
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
            if(!mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            else {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SicauHelperApplication.setStudent(null);
        finish();
    }
}
