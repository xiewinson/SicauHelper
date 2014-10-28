package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.listener.OnScrollHideOrShowActionBarListener;
import cn.com.pplo.sicauhelper.model.Score;
import cn.com.pplo.sicauhelper.model.ScoreStats;
import cn.com.pplo.sicauhelper.model.Student;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.service.SaveIntentService;
import cn.com.pplo.sicauhelper.ui.MainActivity;
import cn.com.pplo.sicauhelper.ui.adapter.ScoreListAdapter;
import cn.com.pplo.sicauhelper.ui.adapter.ScoreStatsAdapter;
import cn.com.pplo.sicauhelper.util.CursorUtil;
import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;
import cn.com.pplo.sicauhelper.widget.ViewPadding;


public class ScoreFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ListView listView;
    private ProgressDialog progressDialog;

    private ScoreListAdapter scoreListAdapter;
    private List<Score> scoreList = new ArrayList<Score>();

    private ScoreStatsAdapter statsAdapter;
    private List<ScoreStats> scoreStatsList = new ArrayList<ScoreStats>();

    private boolean isShowStats = false;
    private static final int MENU_ITEM_ID_STATS = 111;
    private static final int MENU_ITEM_ID_DETAIL = 222;


    public static ScoreFragment newInstance() {
        ScoreFragment fragment = new ScoreFragment();
        return fragment;
    }

    public ScoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached("成绩");
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
        super.onCreateView(inflater, container, savedInstanceState);
        UIUtil.setActionBarColor(getActivity(), getSupportActionBar(getActivity()), R.color.indigo_500);
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_score, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUp(view);
    }

    private void setUp(View view) {
        listView = (ListView) view.findViewById(R.id.score_listView);
//        setListViewTopBottomPadding(listView);
        listView.setOnScrollListener(new OnScrollHideOrShowActionBarListener(getSupportActionBar(getActivity())));
        listView.addHeaderView(ViewPadding.getActionBarPadding(getActivity()));
        progressDialog = UIUtil.getProgressDialog(getActivity(), "找找找～正在教务系统上找你的成绩表");

        if(isShowStats == false) {
            initScoreDetailAdapter();
        }
        else {
            initScoreStatsAdapter();
        }
        //滑动监听
//        setScrollHideOrShowActionBar(listView);
        //启动Loader
        getLoaderManager().initLoader(0, null, this);
    }

    //详情列表
    private void initScoreDetailAdapter() {
        scoreListAdapter = new ScoreListAdapter(getActivity(), scoreList);
        listView.setAdapter(scoreListAdapter);
    }

    //统计列表
    private void initScoreStatsAdapter() {
        statsAdapter = new ScoreStatsAdapter(getActivity(), scoreStatsList);
        listView.setAdapter(statsAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(isShowStats == false) {
            menu.add(1, MENU_ITEM_ID_STATS, 1, "统计")
                    .setIcon(R.drawable.ic_trending_up_white_24dp)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        else {
            menu.add(2, MENU_ITEM_ID_DETAIL, 2 , "详情")
                    .setIcon(R.drawable.ic_list_white_24dp)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //点击统计
        Log.d("winson", "点击了：" + id);
        if(id == MENU_ITEM_ID_STATS) {
            isShowStats = true;
            UIUtil.setActionBarColor(getActivity(), getSupportActionBar(getActivity()), R.color.teal_500);
            getActivity().invalidateOptionsMenu();
            initScoreStatsAdapter();
            getLoaderManager().restartLoader(0, null, this);
        }

        //点击详情
        else if(id == MENU_ITEM_ID_DETAIL) {
            isShowStats = false;
            UIUtil.setActionBarColor(getActivity(), getSupportActionBar(getActivity()), R.color.indigo_500);
            getActivity().invalidateOptionsMenu();
            initScoreDetailAdapter();
            getLoaderManager().restartLoader(0, null, this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), Uri.parse(SicauHelperProvider.URI_SCORE_ALL), null, null, null, null) {
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            List<Score> tempList = CursorUtil.parseScoreList(data);
            if(isShowStats == false) {
                notifyDataSetChanged(tempList);
            }
            else {
                notifyStatsDataSetChanged(StringUtil.parseScoreStatsList(tempList));
            }

        } else {
            requestScoreList(getActivity());
        }
    }

    /**
     * 通知ListView数据改变
     *
     * @param list
     */
    private void notifyDataSetChanged(List<Score> list) {
        if (list != null && list.size() > 0) {
            scoreList.clear();
            scoreList.addAll(list);
            scoreListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 通知ListView数据改变
     * @param list
     */
    private void notifyStatsDataSetChanged(List<ScoreStats> list){
        if(list != null && list.size() > 0){
            scoreStatsList.clear();
            scoreStatsList.addAll(list);
            statsAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 从网络请求数据
     *
     * @param context
     */
    public void requestScoreList(final Context context) {
        progressDialog.show();
        //此处需要修改
        Map<String, String> params = new HashMap<String, String>();
        Student student = SicauHelperApplication.getStudent();
        if (student != null) {
            params.put("user", student.getSid() + "");
            params.put("pwd", student.getPswd());
            params.put("lb", "S");

            NetUtil.getScoreHtmlStr(getActivity(), params, new NetUtil.NetCallback(getActivity()) {
                @Override
                public void onSuccess(String result) {
                    StringUtil.parseScoreInfo(result, new StringUtil.Callback() {
                        @Override
                        public void handleParseResult(Object obj) {
                            List<Score> tempList = (List<Score>) obj;
                            if(isShowStats == false) {
                                notifyDataSetChanged(tempList);
                            }
                            else {
                                notifyStatsDataSetChanged(StringUtil.parseScoreStatsList(tempList));
                            }

                            UIUtil.dismissProgressDialog(progressDialog);
                            SaveIntentService.startActionScoreAll(context, tempList);
                        }
                    });
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    UIUtil.dismissProgressDialog(progressDialog);
                    super.onErrorResponse(volleyError);
                }
            });
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d("winson", ScoreFragment.class.getSimpleName() + "Loader重置");
    }


}
