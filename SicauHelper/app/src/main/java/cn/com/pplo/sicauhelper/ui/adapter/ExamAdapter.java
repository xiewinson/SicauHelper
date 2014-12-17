package cn.com.pplo.sicauhelper.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.model.Exam;
import cn.com.pplo.sicauhelper.util.UIUtil;

/**
 * 考试安排adapter
 * Created by winson on 2014/12/13.
 */
public class ExamAdapter extends BaseAdapter {

    private Context context;
    private List<Exam> data;

    public ExamAdapter(Context context, List<Exam> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Exam getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_fragment_exam_list, null);
            holder.courseTv = (TextView) convertView.findViewById(R.id.exam_course_tv);
            holder.timeTv = (TextView) convertView.findViewById(R.id.exam_time_tv);
            holder.classroomTv = (TextView) convertView.findViewById(R.id.exam_classroom_tv);
            holder.numTv = (TextView) convertView.findViewById(R.id.exam_num_tv);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Exam exam = getItem(position);
        holder.courseTv.setText((position + 1) + ". " + exam.getCourse());
        holder.timeTv.setText(exam.getTime());
        holder.numTv.setText(exam.getNum());
        holder.classroomTv.setText(exam.getClassroom());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtil.startShareIntent(context,
                        "分享",
                        "考试科目：" + exam.getCourse() + "\n" +
                        "时间：" + exam.getTime() + "\n" +
                        "地点：" + exam.getClassroom() + "\n" +
                        "座位号：" + exam.getNum()
                         );
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        TextView courseTv;
        TextView timeTv;
        TextView classroomTv;
        TextView numTv;
    }
}
