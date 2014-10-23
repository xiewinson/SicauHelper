package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.listener.OnScrollListener;
import cn.com.pplo.sicauhelper.model.News;
import cn.com.pplo.sicauhelper.model.Score;
import cn.com.pplo.sicauhelper.model.Student;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.service.OnRequestFinishListener;
import cn.com.pplo.sicauhelper.service.ScoreService;
import cn.com.pplo.sicauhelper.util.CursorUtil;
import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;


public class ScoreDetailFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ListView listView;
    private ProgressDialog progressDialog;

    private ScoreListAdapter scoreListAdapter;
    private List<Score> scoreList = new ArrayList<Score>();
    private boolean isScrolling = false;
    private int startX;
    private int startY;

    public static ScoreDetailFragment newInstance() {
        ScoreDetailFragment fragment = new ScoreDetailFragment();
        return fragment;
    }

    public ScoreDetailFragment() {
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
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_score_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUp(view);
    }

    private void setUp(View view) {
        listView = (ListView) view.findViewById(R.id.score_listView);
        //设置空时view
        listView.setEmptyView(view.findViewById(R.id.empty_view));
        progressDialog = UIUtil.getProgressDialog(getActivity(), "找找找～正在教务系统上找你的成绩表");
        scoreListAdapter = new ScoreListAdapter(getActivity(), scoreList);
        listView.setAdapter(scoreListAdapter);
        //滑动监听
//        setScrollHideOrShowActionBar(listView);
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
        if (data != null && data.getCount() > 0) {
            notifyDataSetChanged(CursorUtil.parseScoreList(data));
        } else {
            Intent scoreIntent = new Intent(getActivity(), ScoreService.class);
            getActivity().bindService(scoreIntent, serviceConn, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * 通知ListView数据改变
     * @param list
     */
    private void notifyDataSetChanged(List<Score> list){
        if(list != null && list.size() > 0){
            scoreList.clear();
            scoreList.addAll(list);
            scoreListAdapter.notifyDataSetChanged();
        }
    }

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            progressDialog.show();
            ScoreService.ScoreServiceBinder scoreServiceBinder = (ScoreService.ScoreServiceBinder) service;
            ScoreService scoreService = scoreServiceBinder.getScoreService();
            scoreService.requestScoreInfo(new OnRequestFinishListener() {
                @Override
                public void onRequestFinish(boolean isSuccess) {
                    getActivity().unbindService(serviceConn);
                    UIUtil.dismissProgressDialog(progressDialog);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d("winson", ScoreDetailFragment.class.getSimpleName() + "Loader重置");
    }

    private class ScoreListAdapter extends BaseAdapter {

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
                color = getResources().getColor(R.color.blue_500);
            } else if (category.equals("公选")) {
                circleShape = R.drawable.circle_red;
                color = getResources().getColor(R.color.red_500);
            } else if (category.equals("任选")) {
                circleShape = R.drawable.circle_green;
                color = getResources().getColor(R.color.green_500);
            } else if (category.equals("推选")) {
                circleShape = R.drawable.circle_orange;
                color = getResources().getColor(R.color.orange_500);
            } else if (category.equals("实践")) {
                circleShape = R.drawable.circle_purple;
                color = getResources().getColor(R.color.purple_500);
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
    }

}
