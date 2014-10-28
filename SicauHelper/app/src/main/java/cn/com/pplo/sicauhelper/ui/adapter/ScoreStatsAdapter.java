package cn.com.pplo.sicauhelper.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.model.ScoreStats;

/**
 * Created by winson on 2014/10/28.
 */
public class ScoreStatsAdapter extends BaseAdapter {
    private Context context;
    private List<ScoreStats> data;

    public ScoreStatsAdapter(Context context, List<ScoreStats> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public ScoreStats getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_fragment_score_stats_list, null);
            holder.gradeTv = (TextView) convertView.findViewById(R.id.grade);
            holder.numMustTv = (TextView) convertView.findViewById(R.id.num_must);
            holder.numChoiceTv = (TextView) convertView.findViewById(R.id.num_choice);
            holder.creditMustTv = (TextView) convertView.findViewById(R.id.credit_must);
            holder.creditChoiceTv = (TextView) convertView.findViewById(R.id.credit_choice);
            holder.scoreMustTv = (TextView) convertView.findViewById(R.id.score_must);
            holder.scoreChoiceTv = (TextView) convertView.findViewById(R.id.score_choice);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        ScoreStats scoreStats = getItem(position);
        holder.gradeTv.setText(scoreStats.getYear());
        holder.numMustTv.setText(scoreStats.getMustNum() + "");
        holder.numChoiceTv.setText(scoreStats.getChoiceNum() + "");
        holder.creditMustTv.setText(scoreStats.getMustCredit() + "");
        holder.creditChoiceTv.setText(scoreStats.getChoiceCredit() + "");
        holder.scoreMustTv.setText(scoreStats.getMustAvgScore() + "");
        holder.scoreChoiceTv.setText(scoreStats.getChoiceAvgScore() + "");
        return convertView;
    }

    private static class ViewHolder {
        TextView gradeTv;
        TextView numMustTv;
        TextView numChoiceTv;
        TextView creditMustTv;
        TextView creditChoiceTv;
        TextView scoreMustTv;
        TextView scoreChoiceTv;

    }
}
