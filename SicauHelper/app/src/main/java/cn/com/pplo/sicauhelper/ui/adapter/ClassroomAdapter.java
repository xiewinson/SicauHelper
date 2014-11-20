package cn.com.pplo.sicauhelper.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.model.Classroom;
import cn.com.pplo.sicauhelper.ui.fragment.ClassroomFragment;
import cn.com.pplo.sicauhelper.ui.fragment.ScoreFragment;

/**
 * Created by winson on 2014/11/2.
 */
public class ClassroomAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private List<Classroom> data;
    private ClassroomFragment.ClassroomFilter classroomFilter;

    public ClassroomAdapter(Context context, List<Classroom> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Classroom getItem(int position) {
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
            convertView = View.inflate(context, R.layout.item_fragment_classroom_grid, null);
            holder.backgroundLayout = convertView.findViewById(R.id.classroom_grid_background);
            holder.roomTv = (TextView) convertView.findViewById(R.id.classroom_grid_room_tv);
            holder.schoolTv = (TextView) convertView.findViewById(R.id.classroom_grid_school_tv);
            holder.timeIv = (ImageView) convertView.findViewById(R.id.classroom_grid_time_iv);
            holder.timeTv = (TextView) convertView.findViewById(R.id.classroom_grid_time_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Classroom classroom = getItem(position);
        String time = classroom.getTime();
        int color = 0;
        int imgSrc = 0;
        if(time.contains("上午")) {
            color = R.color.cyan_500;
            imgSrc = R.drawable.ic_brightness_5_white_48dp;
        }
        else if(time.contains("下午")) {
            color = R.color.amber_500;
            imgSrc = R.drawable.ic_wb_sunny_white_48dp;
        }
        else {
            color = R.color.deep_purple_500;
            imgSrc = R.drawable.ic_brightness_2_white_48dp;
        }
        holder.timeIv.setImageResource(imgSrc);
        holder.backgroundLayout.setBackgroundResource(color);

        holder.timeTv.setText( time.substring(0, 2) + "\n" + time.substring(2));
        holder.schoolTv.setText(classroom.getSchool());
        holder.roomTv.setText(classroom.getName());
        return convertView;
    }


    @Override
    public Filter getFilter() {

        return classroomFilter;
    }

    public void setFilter(ClassroomFragment.ClassroomFilter classroomFilter) {
        this.classroomFilter = classroomFilter;
    }

    private static class ViewHolder {
        View backgroundLayout;
        TextView timeTv;
        ImageView timeIv;
        TextView schoolTv;
        TextView roomTv;
    }
}
