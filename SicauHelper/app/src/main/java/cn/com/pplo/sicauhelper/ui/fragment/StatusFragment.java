package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.FindCallback;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.action.StatusAction;
import cn.com.pplo.sicauhelper.ui.AddActivity;
import cn.com.pplo.sicauhelper.ui.MainActivity;
import cn.com.pplo.sicauhelper.ui.adapter.StatusAdapter;
import cn.com.pplo.sicauhelper.util.UIUtil;

public class StatusFragment extends BaseFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private View footerView;
    private FloatingActionButton fab;
    private StatusAdapter statusAdapter;
    private List<AVObject> data = new ArrayList<AVObject>();

    public static StatusFragment newInstance() {
        StatusFragment fragment = new StatusFragment();

        return fragment;
    }

    public StatusFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getResources().getString(R.string.title_status));
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
        setHasOptionsMenu(true);
        UIUtil.setActionBarColor(getActivity(), getSupportActionBar(getActivity()), R.color.red_500);
        return inflater.inflate(R.layout.fragment_status, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUp(getActivity(), view);
    }

    private void setUp(final Context context, View view) {
        //下拉刷新
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.status_swipe_container);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_500, R.color.orange_500, R.color.green_500);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                findNewData();
            }
        });

        listView = (ListView) view.findViewById(R.id.status_listView);
        statusAdapter = new StatusAdapter(context, data);

        //加载更多进度条
        footerView = View.inflate(context, R.layout.footer_progress, null);
        footerView.setVisibility(View.GONE);
        listView.addFooterView(footerView);

        UIUtil.setListViewInitAnimation(UIUtil.LISTVIEW_ANIM_BOTTOM, listView, statusAdapter);

        //添加fab
        fab = (FloatingActionButton) view.findViewById(R.id.status_add_fab);
        UIUtil.initFab(getActivity(), fab, listView, R.color.red_500, R.color.red_900, R.color.red_400, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddActivity.startAddActivity(context, AddActivity.TYPE_STATUS);

            }
        }, new FloatingActionButton.FabOnScrollListener(){
            @Override
            public void onScrollDown() {
                super.onScrollDown();
            }

            @Override
            public void onScrollUp() {
                super.onScrollUp();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                if ((firstVisibleItem + visibleItemCount) > (totalItemCount - 2)) {

                    if (footerView.getVisibility() == View.GONE && data.size() >= 10) {
                        Log.d("winson", "加载更多");
                        footerView.setVisibility(View.VISIBLE);
                        findById(data.get(data.size() - 1).getLong("status_id"));
                    }
                }
            }
        });

        findInCacheThenNetwork();
    }



    /**
     * 从缓存中取
     */
    private void findInCacheThenNetwork() {
        new StatusAction().findInCacheThenNetwork(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    Log.d("winson", list.size() + "个");
                    //若为0.则进行网络请求
                    notifyDataSetChanged(list, true);
                } else {
                    if (!e.getMessage().contains("Cache")) {
                        UIUtil.showShortToast(getActivity(), "你的网络好像有点问题，下拉刷新试试吧");
                    }
                    findNewData();
                }
            }
        });
    }

    /**
     * 从网络取新的并清空缓存
     */
    private void findNewData() {
        swipeRefreshLayout.setRefreshing(true);
        footerView.setVisibility(View.GONE);
        new StatusAction().findNewData(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    Log.d("winson", list.size() + "个");
                    notifyDataSetChanged(list, true);
                    listView.setSelection(0);
                } else {
                    UIUtil.showShortToast(getActivity(), "你的网络好像有点问题，重新试试吧");
                    Log.d("winson", "出错：" + e.getMessage());
                }
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    /**
     * 加载更多
     */
    private void findById(long status_id) {
        new StatusAction().findSinceId(status_id, new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    Log.d("winson", list.size() + "个");
                    if (list.size() == 0) {
                        UIUtil.showShortToast(getActivity(), "已经没有更多商品啦");
                        footerView.setVisibility(View.INVISIBLE);
                    } else {
                        notifyDataSetChanged(list, false);
                        footerView.setVisibility(View.GONE);
                    }
                } else {
                    UIUtil.showShortToast(getActivity(), "你的网络好像有点问题，重新试试吧");
                    Log.d("winson", "出错：" + e.getMessage());
                    footerView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void notifyDataSetChanged(List<AVObject> list, boolean isRefresh) {
        if (isRefresh) {
            data.clear();
        }
        data.addAll(list);
        statusAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.market, menu);
//        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //noinspection SimplifiableIfStatement
        int id = item.getItemId();
        if(id == R.id.action_search) {
//            SearchGoodsActivity.startSearchGoodsActivity(getActivity(), viewPager.getCurrentItem());
            return true;
        }
        else if (id == R.id.action_refresh) {
            findNewData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
