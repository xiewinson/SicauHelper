package cn.com.pplo.sicauhelper.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.model.Course;

public class CourseAdapter extends BaseAdapter {
    private Context context;
    private List<Course> data;

    public CourseAdapter(Context context, List<Course> data) {
        this.context = context;
        this.data = data;
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
            convertView = View.inflate(context, R.layout.item_fragment_course_date_list, null);
            holder.timeTv = (TextView) convertView.findViewById(R.id.time_tv);
            holder.nameTv = (TextView) convertView.findViewById(R.id.name_tv);
            holder.categoryTv = (TextView) convertView.findViewById(R.id.category_tv);
            holder.classroomTv = (TextView) convertView.findViewById(R.id.classroom_tv);
            holder.creditTv = (RatingBar) convertView.findViewById(R.id.credit_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Course course = getItem(position);
        String time = course.getTime();
        int circleShape = 0;
        int color = 0;
        if (time.equals("1-2")) {
            circleShape = R.drawable.circle_blue;
            color = context.getResources().getColor(R.color.blue_500);
        } else if (time.equals("3-4")) {
            circleShape = R.drawable.circle_red;
            color = context.getResources().getColor(R.color.red_500);
        } else if (time.equals("5-6")) {
            circleShape = R.drawable.circle_green;
            color = context.getResources().getColor(R.color.green_500);
        } else if (time.equals("7-8")) {
            circleShape = R.drawable.circle_orange;
            color = context.getResources().getColor(R.color.orange_500);
        } else if (time.equals("9-10")) {
            circleShape = R.drawable.circle_purple;
            color = context.getResources().getColor(R.color.purple_500);
        }

        //设置课程名
        holder.nameTv.setText(course.getName());
        //设置课程时间
        holder.timeTv.setText(course.getTime());
        holder.timeTv.setBackgroundResource(circleShape);

        //设置课程类型
        holder.categoryTv.setText(course.getCategory() + "");
        //设置教室
        holder.classroomTv.setText(course.getClassroom() + "(" + course.getWeek() + "周)");

        //设置星星个
        if ((getItem(position).getCredit() > 5)) {
            holder.creditTv.setNumStars(10);
        } else {
            holder.creditTv.setNumStars(5);
        }
        //设置星星颜色
        LayerDrawable stars = (LayerDrawable) holder.creditTv.getProgressDrawable();
        stars.getDrawable(0).setColorFilter(Color.parseColor("#cccccc"), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.parseColor("#cccccc"), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(2).setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        holder.creditTv.setRating(getItem(position).getCredit());
        return convertView;
    }
    private static class ViewHolder {
        TextView timeTv;
        TextView nameTv;
        TextView classroomTv;
        RatingBar creditTv;
        TextView categoryTv;
    }
}

