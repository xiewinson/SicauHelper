package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.model.Score;
import cn.com.pplo.sicauhelper.model.ScoreStats;
import cn.com.pplo.sicauhelper.model.Student;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.ui.MainActivity;
import cn.com.pplo.sicauhelper.util.CursorUtil;
import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;
import cn.com.pplo.sicauhelper.widget.ViewPadding;

public class ScoreStatsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView listView;
    private List<ScoreStats> scoreStatsList = new ArrayList<ScoreStats>();
    private StatsAdapter statsAdapter;

    public static ScoreStatsFragment newInstance() {
        ScoreStatsFragment fragment = new ScoreStatsFragment();
        return fragment;
    }

    public ScoreStatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        ((MainActivity) activity).onSectionAttached("成绩");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater,container, savedInstanceState);
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_score_stats, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUp(view);
    }

    private void setUp(View view) {
        listView = (ListView) view.findViewById(R.id.stats_listView);
        //设置空时view
        listView.setEmptyView(view.findViewById(R.id.empty_view));
        //listView上下补点间距
        TextView paddingTv = ViewPadding.getListViewPadding(getActivity());
        listView.addHeaderView(paddingTv);
        listView.addFooterView(paddingTv);

        statsAdapter = new StatsAdapter(getActivity(), scoreStatsList);
        listView.setAdapter(statsAdapter);

        //滑动监听


        //启动Loader
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), Uri.parse(SicauHelperProvider.URI_SCORE_ALL), null, null, null, null) {
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        listView.setEmptyView(null);
        if(data != null){
            notifyDataSetChanged(StringUtil.parseScoreStatsList(CursorUtil.parseScoreList(data)));
        }
    }

    /**
     * 通知ListView数据改变
     * @param list
     */
    private void notifyDataSetChanged(List<ScoreStats> list){
        if(list != null && list.size() > 0){
            scoreStatsList.clear();
            scoreStatsList.addAll(list);
            statsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private class StatsAdapter extends BaseAdapter {
        private Context context;
        private List<ScoreStats> data;

        private StatsAdapter(Context context, List<ScoreStats> data) {
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
    }

    private class ViewHolder {
        TextView gradeTv;
        TextView numMustTv;
        TextView numChoiceTv;
        TextView creditMustTv;
        TextView creditChoiceTv;
        TextView scoreMustTv;
        TextView scoreChoiceTv;

    }

}
