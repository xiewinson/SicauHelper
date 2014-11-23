package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.model.Score;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.service.SaveIntentService;
import cn.com.pplo.sicauhelper.ui.MainActivity;
import cn.com.pplo.sicauhelper.ui.ScoreStatsActivity;
import cn.com.pplo.sicauhelper.ui.adapter.ScoreListAdapter;
import cn.com.pplo.sicauhelper.util.CursorUtil;
import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.SharedPreferencesUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


public class ScoreFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private StickyListHeadersListView listView;
    private AlertDialog progressDialog;
    private SearchView searchView;

    private ScoreListAdapter scoreListAdapter;
    private List<Score> scoreList = new ArrayList<Score>();
    private List<Score> originalList = new ArrayList<Score>();

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
        UIUtil.setActionBarColorBySchool(getActivity(),
                SicauHelperApplication.getStudent().getInt(TableContract.TableUser._SCHOOL),
                getSupportActionBar(getActivity()));
        setUp(view);
    }

    private void setUp(View view) {
        listView = (StickyListHeadersListView) view.findViewById(R.id.score_listView);
//        setListViewTopBottomPadding(listView);
//        listView.setOnScrollListener(new OnScrollHideOrShowActionBarListener(getSupportActionBar(getActivity())));
//        listView.addHeaderView(ViewPadding.getActionBarPadding(getActivity(), R.color.eeeeee));
        progressDialog = UIUtil.getProgressDialog(getActivity(), "正在教务系统上找你的成绩表");
        initScoreDetailAdapter();
        getLoaderManager().initLoader(0, null, this);
    }

    //详情列表
    private void initScoreDetailAdapter() {
        scoreListAdapter = new ScoreListAdapter(getActivity(), scoreList, SicauHelperApplication.getStudent().getInt(TableContract.TableUser._SCHOOL));
//        UIUtil.setListViewInitAnimation("bottom", listView, scoreListAdapter);
        listView.setAdapter(scoreListAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initSearchView(Menu menu) {
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("搜索课程名称/性质/分数");
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String s) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String s) {
                            scoreListAdapter.setFilter(new ScoreFilter());
                            scoreListAdapter.getFilter().filter(s);
                            return false;
                        }
                    });
                    searchView.setOnQueryTextFocusChangeListener(null);
                }
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.score, menu);
        initSearchView(menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //点击统计
        if (id == R.id.action_score_static) {
            ScoreStatsActivity.startScoreStatsAcitivity(getActivity(), scoreList);
        } else if (id == R.id.action_refresh) {
            requestScoreList(getActivity());
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
            keepOriginalData(tempList);
            notifyDataSetChanged(tempList);
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
        if (list != null) {
            scoreList.clear();
            scoreList.addAll(list);
            scoreListAdapter.notifyDataSetChanged();
            //恢复到第一个
            listView.setSelection(0);
//            UIUtil.setListViewScrollHideOrShowActionBar(getActivity(), listView, getSupportActionBar(getActivity()));
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
        params.put("user", SharedPreferencesUtil.get(context, SharedPreferencesUtil.LOGIN_SID, "").toString());
        params.put("pwd", SharedPreferencesUtil.get(context, SharedPreferencesUtil.LOGIN_PSWD, "").toString());
            params.put("lb", "S");

            NetUtil.getScoreHtmlStr(getActivity(), params, new NetUtil.NetCallback(getActivity()) {
                @Override
                public void onSuccess(String result) {
                    StringUtil.parseScoreInfo(result, new StringUtil.Callback() {
                        @Override
                        public void handleParseResult(Object obj) {
                            List<Score> tempList = (List<Score>) obj;
                            keepOriginalData(tempList);
                            notifyDataSetChanged(tempList);


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

    /**
     * 保持最新的原始数据
     *
     * @param tempList
     */
    private void keepOriginalData(List<Score> tempList) {
        originalList.clear();
        originalList.addAll(tempList);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    //分数过滤
    public class ScoreFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String query = constraint.toString();
            List<Score> values = new ArrayList<Score>();
            FilterResults results = new FilterResults();
            if (TextUtils.isEmpty(query)) {
                values.addAll(originalList);
            } else {

                for (Score score : originalList) {
                    if (score.getCategory().contains(query)
                            || score.getCourse().contains(query)
                            || score.getMark().contains(query)) {
                        values.add(score);
                    }
                }
            }
            results.values = values;
            results.count = values.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //更新数据
            notifyDataSetChanged((List<Score>) results.values);
        }
    }

}
