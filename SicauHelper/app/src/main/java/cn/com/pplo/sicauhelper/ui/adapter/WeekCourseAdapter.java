package cn.com.pplo.sicauhelper.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.model.Course;
import cn.com.pplo.sicauhelper.ui.fragment.CourseFragment;

public class WeekCourseAdapter extends BaseAdapter {
    private Context context;
    private List<Course> data;
    private int type;

    public WeekCourseAdapter(Context context, List<Course> data, int type) {
        this.context = context;
        this.data = data;
        this.type = type;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Course getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_fragment_week_course_date_list, null);
            holder.timeTv = (TextView) convertView.findViewById(R.id.time_tv);
            holder.nameTv = (TextView) convertView.findViewById(R.id.name_tv);
            holder.categoryTv = (TextView) convertView.findViewById(R.id.category_tv);
            holder.classroomTv = (TextView) convertView.findViewById(R.id.classroom_tv);
            holder.weekTv = (TextView) convertView.findViewById(R.id.week_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Course course = getItem(position);
        String time = course.getTime();
        int circleShape = 0;
        int color = 0;
        if (time.equals("1-2")) {
            circleShape = R.drawable.circle_cyan;
            color = context.getResources().getColor(R.color.cyan_500);
        } else if (time.equals("3-4")) {
            circleShape = R.drawable.circle_amber;
            color = context.getResources().getColor(R.color.amber_500);
        } else if (time.equals("5-6")) {
            circleShape = R.drawable.circle_deep_orange;
            color = context.getResources().getColor(R.color.deep_orange_500);
        } else if (time.equals("7-8")) {
            circleShape = R.drawable.circle_blue;
            color = context.getResources().getColor(R.color.blue_500);
        } else if (time.equals("9-10")) {
            circleShape = R.drawable.circle_indigo;
            color = context.getResources().getColor(R.color.indigo_500);
        } else if (time.contains("1-") || time.contains("2-") || time.contains("3-") || time.contains("4-")) {
            circleShape = R.drawable.circle_amber;
            color = context.getResources().getColor(R.color.amber_500);
        }else if (time.contains("5-") || time.contains("6-") || time.contains("7-") || time.contains("8-")) {
            circleShape = R.drawable.circle_deep_orange;
            color = context.getResources().getColor(R.color.deep_orange_500);
        }else {
            circleShape = R.drawable.circle_indigo;
            color = context.getResources().getColor(R.color.indigo_500);
        }

        //设置课程名
        holder.nameTv.setText(course.getName());
        //设置课程时间
        holder.timeTv.setText(course.getTime());
        holder.timeTv.setBackgroundResource(circleShape);

        //设置课程类型
        holder.categoryTv.setText(course.getCategory() + "");
        //设置教室
        holder.classroomTv.setText(course.getClassroom());
        if(type == CourseFragment.TYPE_COURSE_THEORY) {
            //设置周次
            holder.weekTv.setText(course.getWeek() + "周");
        }
        else {
            //设置周次
            holder.weekTv.setText(course.getWeek());
            holder.categoryTv.setVisibility(View.GONE);
        }

        return convertView;
    }
    private static class ViewHolder {
        TextView timeTv;
        TextView nameTv;
        TextView classroomTv;
        TextView weekTv;
        TextView categoryTv;
    }
}

