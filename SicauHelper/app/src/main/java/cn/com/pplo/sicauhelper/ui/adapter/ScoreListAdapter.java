package cn.com.pplo.sicauhelper.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.model.News;
import cn.com.pplo.sicauhelper.model.Score;
import cn.com.pplo.sicauhelper.ui.fragment.ScoreFragment;

/**
 * Created by winson on 2014/10/28.
 */
public class ScoreListAdapter extends BaseAdapter implements Filterable {

    private ScoreFragment.ScoreFilter scoreFilter;
    private Context context;
    private List<Score> data;

    public ScoreListAdapter(Context context, List<Score> list) {
        this.context = context;
        this.data = list;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Score getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //若是上半学期则在左边，反之则在右边
        if (String.valueOf(getItem(position).getGrade()).contains(".1")) {
            convertView = View.inflate(context, R.layout.item_fragment_score_list, null);
        } else {
            convertView = View.inflate(context, R.layout.item_fragment_score_list_right, null);
        }
        TextView categoryTv = (TextView) convertView.findViewById(R.id.category_tv);
        TextView gradeTv = (TextView) convertView.findViewById(R.id.grade_tv);
        final TextView scoreView = (TextView) convertView.findViewById(R.id.score_tv);
        TextView courseTv = (TextView) convertView.findViewById(R.id.course_tv);
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.rating_bar);
        TextView creditTv = (TextView) convertView.findViewById(R.id.credit_tv);

        String category = getItem(position).getCategory();
        int circleShape = 0;
        int color = 0;
        if (category.equals("必修")) {
            circleShape = R.drawable.circle_blue;
            color = context.getResources().getColor(R.color.blue_500);
        } else if (category.equals("公选")) {
            circleShape = R.drawable.circle_red;
            color = context.getResources().getColor(R.color.red_500);
        } else if (category.equals("任选")) {
            circleShape = R.drawable.circle_green;
            color = context.getResources().getColor(R.color.green_500);
        } else if (category.equals("推选")) {
            circleShape = R.drawable.circle_orange;
            color = context.getResources().getColor(R.color.orange_500);
        } else if (category.equals("实践")) {
            circleShape = R.drawable.circle_purple;
            color = context.getResources().getColor(R.color.purple_500);
        }
        scoreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RotateAnimation animation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);

                animation.setDuration(500);

                Log.d("winson", "点击了...");
                scoreView.startAnimation(animation);
            }
        });

        categoryTv.setTextColor(color);
        scoreView.setBackgroundResource(circleShape);
        scoreView.setTextColor(Color.WHITE);

        //显示学年
        String[] gradeArray = (getItem(position).getGrade() + "").split("\\.");
        String upOrDown = "";
        if (gradeArray[1].equals("1")) {
            upOrDown = "上学年";
        } else {
            upOrDown = "下学年";
        }
        gradeTv.setText(gradeArray[0] + upOrDown);
        scoreView.setText(getItem(position).getMark() + "");
        courseTv.setText(getItem(position).getCourse() + "");
        creditTv.setText(getItem(position).getCredit() + "学分");
        categoryTv.setText("#" + getItem(position).getCategory() + "#");

        //设置星星个数
        if ((getItem(position).getCredit() > 5)) {
            ratingBar.setNumStars(10);
        } else {
//                ratingBar.setMax(5);
            ratingBar.setNumStars(5);
        }
        //设置星星颜色
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(0).setColorFilter(Color.parseColor("#cccccc"), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.parseColor("#cccccc"), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(2).setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        ratingBar.setRating(getItem(position).getCredit());

        return convertView;
    }

    @Override
    public Filter getFilter() {

        return scoreFilter;
    }

    public void setFilter(ScoreFragment.ScoreFilter scoreFilter) {
        this.scoreFilter = scoreFilter;
    }


}

