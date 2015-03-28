package cn.com.pplo.sicauhelper.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.model.Course;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.ui.fragment.CourseFragment;
import cn.com.pplo.sicauhelper.util.UIUtil;

public class CourseActivity extends BaseActivity {
    public static final String EXTRA_COURSE_LIST = "extra_course_list";
    public static final String EXTRA_COURSE_TYPE = "extra_course_type";
    private List<Course> data;
    private Course course;
    private int type;

    private TextView nameTv;
    private TextView categoryTv;
    private TextView teacherTv;
    private TextView weekTv;
    private TextView schedualCountTv;
    private TextView actualCountTv;
    private RatingBar creditRatingBar;
    private LinearLayout timeLayout;

    public static void startCourseActivity(Activity context, List<Course> data, int type) {
        Intent intent = new Intent(context, CourseActivity.class);
        intent.putExtra(EXTRA_COURSE_TYPE, type);
        intent.putParcelableArrayListExtra(EXTRA_COURSE_LIST, (java.util.ArrayList<? extends android.os.Parcelable>) data);
        context.startActivityForResult(intent, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        setUp();
    }

    private void setUp() {
        data = getIntent().getParcelableArrayListExtra(EXTRA_COURSE_LIST);
        type = getIntent().getIntExtra(EXTRA_COURSE_TYPE, 111);
        if (data != null && data.size() > 0) {
            course = data.get(0);
        }

        nameTv = (TextView) findViewById(R.id.course_name_tv);
        categoryTv = (TextView) findViewById(R.id.course_category_tv);
        teacherTv = (TextView) findViewById(R.id.course_teacher_tv);
        weekTv = (TextView) findViewById(R.id.course_week_tv);
        schedualCountTv = (TextView) findViewById(R.id.course_schedual_count_tv);
        actualCountTv = (TextView) findViewById(R.id.course_actual_count_tv);
        creditRatingBar = (RatingBar) findViewById(R.id.credit_ratingbar);
        timeLayout = (LinearLayout) findViewById(R.id.time_layout);

        //若是实验课
        if (type == CourseFragment.TYPE_COURSE_LAB) {
            getSupportActionBar().setTitle("实验课");
            findViewById(R.id.course_category_title_tv).setVisibility(View.GONE);
            findViewById(R.id.course_category_tv).setVisibility(View.GONE);
            findViewById(R.id.credit_ratingbar_title).setVisibility(View.GONE);
            findViewById(R.id.credit_ratingbar).setVisibility(View.GONE);
            findViewById(R.id.course_count_cardView).setVisibility(View.GONE);
            weekTv.setText(course.getWeek());
        } else {
            getSupportActionBar().setTitle("理论课");
            weekTv.setText(course.getWeek() + "周");
        }

        nameTv.setText(course.getName());
        categoryTv.setText(course.getCategory());
        teacherTv.setText(course.getTeacher());
        schedualCountTv.setText(course.getScheduleNum() + " 人");
        actualCountTv.setText(course.getSelectedNum() + " 人");

        //设置学分星星个数
        if (course.getCredit() <= 5) {
            creditRatingBar.setNumStars(5);
        } else {
            creditRatingBar.setNumStars(10);
        }
        //设置星星颜色
        LayerDrawable stars = (LayerDrawable) creditRatingBar.getProgressDrawable();
        stars.getDrawable(0).setColorFilter(Color.parseColor("#cccccc"), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.parseColor("#cccccc"), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.color_primary), PorterDuff.Mode.SRC_ATOP);
        Log.d("winson", "学分：" + course.getCredit());
        creditRatingBar.setRating(course.getCredit());

        //添加课堂时间
        for (Course cs : data) {
            View view = View.inflate(this, R.layout.room_time_layout, null);
            TextView roomTv = (TextView) view.findViewById(R.id.room_tv);
            TextView timeTv = (TextView) view.findViewById(R.id.time_tv);
            roomTv.setText(cs.getClassroom());
            timeTv.setText("(" + cs.getTime() + ")");
            timeLayout.addView(view);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            new AlertDialog.Builder(this).setMessage("是否删除这门课").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            if(type == CourseFragment.TYPE_COURSE_LAB) {
                                getContentResolver().delete(Uri.parse(SicauHelperProvider.URI_LAB_COURSE_ALL), TableContract.TableCourse._NAME + " =? ", new String[]{course.getName() + ""});

                            }
                            else {
                                getContentResolver().delete(Uri.parse(SicauHelperProvider.URI_COURSE_ALL), TableContract.TableCourse._NAME + " =? ", new String[]{course.getName() + ""});
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            setResult(RESULT_OK);
                            finish();
                        }
                    }.execute();
                }
            }).setNegativeButton("取消", null).create().show();

        }
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
}
