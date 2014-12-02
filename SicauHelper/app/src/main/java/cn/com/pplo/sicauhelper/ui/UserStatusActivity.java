package cn.com.pplo.sicauhelper.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import java.util.ArrayList;
import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.action.StatusAction;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.ui.adapter.StatusAdapter;
import cn.com.pplo.sicauhelper.util.UIUtil;

public class UserStatusActivity extends BaseActivity {

    private static final String EXTRA_SCHOOL = "school";
    private static final String EXTRA_NICKNAME = "nickname";
    private static final String EXTRA_OBJECT_ID = "object_id";

    private int schoolPosition = 0;
    private String objectId;
    private String nickname;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ListView listView;
    private View footerView;
    private StatusAdapter statusAdapter;
    private SearchView searchView;

    private List<AVObject> data = new ArrayList<AVObject>();
    private String queryStr = "";

    public static void startUserStatusActivity(Context context, int school, String objectId, String nickname) {
        Intent intent = new Intent(context, UserStatusActivity.class);
        intent.putExtra(EXTRA_SCHOOL, school);
        intent.putExtra(EXTRA_NICKNAME, nickname);
        intent.putExtra(EXTRA_OBJECT_ID, objectId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_status);
        setUp(this);
    }

    private void setUp(Context context) {
        objectId = getIntent().getStringExtra(EXTRA_OBJECT_ID);
        nickname = getIntent().getStringExtra(EXTRA_NICKNAME);
        schoolPosition = getIntent().getIntExtra(EXTRA_SCHOOL, 0);
        UIUtil.setActionBarColor(context,getSupportActionBar(), R.color.light_blue_500);
        getSupportActionBar().setTitle(nickname);
        getSupportActionBar().setSubtitle("圈子列表");

        //下拉刷新
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.user_status_swipe_container);
        swipeRefreshLayout.setColorSchemeResources(R.color.red_500, R.color.orange_500, R.color.light_green_500);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                findNewData(AVQuery.CachePolicy.NETWORK_ONLY);
            }
        });

        listView = (ListView) findViewById(R.id.user_status_listView);
        //加载更多进度条
        footerView = View.inflate(context, R.layout.footer_progress, null);
        footerView.setVisibility(View.GONE);
        listView.addFooterView(footerView);

        statusAdapter = new StatusAdapter(context, data);
        UIUtil.setListViewInitAnimation(UIUtil.LISTVIEW_ANIM_BOTTOM, listView, statusAdapter);

        //滑到最下面加载更多
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if ((firstVisibleItem + visibleItemCount) > (totalItemCount - 2)) {

                    if (footerView.getVisibility() == View.GONE && data.size() >= 10) {
                        Log.d("winson", "加载更多");
                        footerView.setVisibility(View.VISIBLE);
                        new StatusAction().findDataByUserSinceId(UserStatusActivity.this, objectId,
                                data.get(data.size() - 1).getLong(TableContract.TableStatus._STATUS_ID),
                                new FindCallback<AVObject>() {
                                    @Override
                                    public void done(List<AVObject> list, AVException e) {
                                        if (e == null) {
                                            Log.d("winson", list.size() + "个");
                                            if (list.size() == 0) {
                                                UIUtil.showShortToast(UserStatusActivity.this, "已经没有更多符合条件的帖子啦！");
                                                footerView.setVisibility(View.INVISIBLE);
                                            } else {
                                                notifyDataSetChanged(list, false);
                                                footerView.setVisibility(View.GONE);
                                            }
                                        } else {
                                            UIUtil.showShortToast(UserStatusActivity.this, "你的网络好像有点问题，重新试试吧");
                                            Log.d("winson", "出错：" + e.getMessage());
                                            footerView.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }
                }
            }
        });

        findNewData(AVQuery.CachePolicy.CACHE_THEN_NETWORK);
    }

    /**
     * 从网络取新的并清空缓存
     */
    private void findNewData(AVQuery.CachePolicy cachePolicy) {
        swipeRefreshLayout.setRefreshing(true);
        footerView.setVisibility(View.GONE);
        new StatusAction().findNewDataByUser(UserStatusActivity.this, cachePolicy, objectId, new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if(list.size() == 0) {
                        UIUtil.showShortToast(UserStatusActivity.this, "这人没有发布任何帖子");
                    }
                    Log.d("winson", list.size() + "个");
                    notifyDataSetChanged(list, true);
//                    listView.setSelection(0);
                } else {
                    if (!e.getMessage().contains("Cache")) {
                        UIUtil.showShortToast(UserStatusActivity.this, "你的网络好像有点问题，下拉刷新试试吧");
                    }
                }
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_refresh) {
            findNewData(AVQuery.CachePolicy.NETWORK_ONLY);
        }
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
}
