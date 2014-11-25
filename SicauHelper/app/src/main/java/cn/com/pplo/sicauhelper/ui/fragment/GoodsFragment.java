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
import cn.com.pplo.sicauhelper.action.GoodsAction;
import cn.com.pplo.sicauhelper.ui.AddActivity;
import cn.com.pplo.sicauhelper.ui.adapter.GoodsAdapter;
import cn.com.pplo.sicauhelper.util.UIUtil;

public class GoodsFragment extends BaseFragment {

    private static final String SCHOOL_POSITION = "school_position";
    private int schoolPosition = 0;
    private String[] schoolArray;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private View footerView;
    private FloatingActionButton fab;
    private GoodsAdapter goodsAdapter;
    private List<AVObject> data = new ArrayList<AVObject>();

    public static GoodsFragment newInstance(int position) {
        GoodsFragment fragment = new GoodsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(SCHOOL_POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    public GoodsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        schoolPosition = getArguments().getInt(SCHOOL_POSITION);
        schoolArray = getResources().getStringArray(R.array.school);
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
        return inflater.inflate(R.layout.fragment_goods, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("winson", schoolPosition + "  " + "创建完成");
        setUp(getActivity(), view);
    }

    private void setUp(final Context context, View view) {
        //下拉刷新
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.school_market_swipe_container);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_500, R.color.orange_500, R.color.green_500);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                findNewData();
            }
        });

        listView = (ListView) view.findViewById(R.id.goods_listView);
        goodsAdapter = new GoodsAdapter(context, data);


        //加载更多进度条
        footerView = View.inflate(context, R.layout.footer_progress, null);
        footerView.setVisibility(View.GONE);
        listView.addFooterView(footerView);

        UIUtil.setListViewInitAnimation(UIUtil.LISTVIEW_ANIM_BOTTOM, listView, goodsAdapter);

        //添加fab
        fab = (FloatingActionButton) view.findViewById(R.id.goods_add_fab);
        int normalColor = 0;
        int pressColor = 0;
        int rippleColor = 0;
        switch (schoolPosition) {
            case 0:
                normalColor = R.color.blue_500;
                pressColor = R.color.blue_900;
                rippleColor = R.color.blue_400;
                break;
            case 1:
                normalColor = R.color.orange_500;
                pressColor = R.color.orange_900;
                rippleColor = R.color.orange_400;
                break;
            case 2:
                normalColor = R.color.green_500;
                pressColor = R.color.green_900;
                rippleColor = R.color.green_400;
                break;
        }

        UIUtil.initFab(getActivity(), fab, listView, normalColor, pressColor, rippleColor, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddActivity.startAddActivity(context, AddActivity.TYPE_GOODS);
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
                        findById(data.get(data.size() - 1).getLong("goods_id"));
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
        swipeRefreshLayout.setRefreshing(true);
        footerView.setVisibility(View.GONE);
        new GoodsAction().findInCacheThenNetwork(schoolPosition, new FindCallback<AVObject>() {
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
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
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
        new GoodsAction().findNewData(schoolPosition, new FindCallback<AVObject>() {
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
    private void findById(long goods_id) {
        new GoodsAction().findSinceId(schoolPosition, goods_id, new FindCallback<AVObject>() {
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
        goodsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.market, menu);
//        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            findNewData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
