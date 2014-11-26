package cn.com.pplo.sicauhelper.ui;

import android.app.Activity;
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
import com.avos.avoscloud.FindCallback;

import java.util.ArrayList;
import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.action.GoodsAction;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.ui.adapter.GoodsAdapter;
import cn.com.pplo.sicauhelper.util.UIUtil;

public class UserGoodsActivity extends BaseActivity {

    private static final String EXTRA_SCHOOL = "school";
    private static final String EXTRA_NICKNAME = "nickname";
    private static final String EXTRA_OBJECT_ID = "object_id";

    private int schoolPosition = 0;
    private String objectId;
    private String nickname;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ListView listView;
    private View footerView;
    private GoodsAdapter goodsAdapter;
    private SearchView searchView;

    private List<AVObject> data = new ArrayList<AVObject>();
    private String queryStr = "";

    public static void startUserGoodsActivity(Context context, int school, String objectId, String nickname) {
        Intent intent = new Intent(context, UserGoodsActivity.class);
        intent.putExtra(EXTRA_SCHOOL, school);
        intent.putExtra(EXTRA_NICKNAME, nickname);
        intent.putExtra(EXTRA_OBJECT_ID, objectId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_goods);
        setUp(this);
    }

    private void setUp(Context context) {
        objectId = getIntent().getStringExtra(EXTRA_OBJECT_ID);
        nickname = getIntent().getStringExtra(EXTRA_NICKNAME);
        schoolPosition = getIntent().getIntExtra(EXTRA_SCHOOL, 0);
        UIUtil.setActionBarColorBySchool(context, schoolPosition, getSupportActionBar());
        getSupportActionBar().setTitle(nickname);
        getSupportActionBar().setSubtitle("商品列表");

        //下拉刷新
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.user_goods_swipe_container);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_500, R.color.orange_500, R.color.green_500);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                findNewData();
            }
        });

        listView = (ListView) findViewById(R.id.user_goods_listView);
        //加载更多进度条
        footerView = View.inflate(context, R.layout.footer_progress, null);
        footerView.setVisibility(View.GONE);
        listView.addFooterView(footerView);

        goodsAdapter = new GoodsAdapter(context, data);
        UIUtil.setListViewInitAnimation(UIUtil.LISTVIEW_ANIM_BOTTOM, listView, goodsAdapter);

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
                        new GoodsAction().findDataByUserSinceId(objectId,
                                data.get(data.size() - 1).getLong(TableContract.TableGoods._GOODS_ID),
                                new FindCallback<AVObject>() {
                                    @Override
                                    public void done(List<AVObject> list, AVException e) {
                                        if (e == null) {
                                            Log.d("winson", list.size() + "个");
                                            if (list.size() == 0) {
                                                UIUtil.showShortToast(UserGoodsActivity.this, "已经没有更多符合条件的商品啦！");
                                                footerView.setVisibility(View.INVISIBLE);
                                            } else {
                                                notifyDataSetChanged(list, false);
                                                footerView.setVisibility(View.GONE);
                                            }
                                        } else {
                                            UIUtil.showShortToast(UserGoodsActivity.this, "你的网络好像有点问题，重新试试吧");
                                            Log.d("winson", "出错：" + e.getMessage());
                                            footerView.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }
                }
            }
        });

        findNewData();
    }

    /**
     * 从网络取新的并清空缓存
     */
    private void findNewData() {
        swipeRefreshLayout.setRefreshing(true);
        footerView.setVisibility(View.GONE);
        new GoodsAction().findNewDataByUser(objectId, new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        UIUtil.showShortToast(UserGoodsActivity.this, "这人没有发布任何商品");
                    }
                    Log.d("winson", list.size() + "个");
                    notifyDataSetChanged(list, true);
                    listView.setSelection(0);
                } else {
                    UIUtil.showShortToast(UserGoodsActivity.this, "大王的网络好像有点问题");
                    Log.d("winson", "出错：" + e.getMessage());
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
        goodsAdapter.notifyDataSetChanged();
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
            findNewData();
        }
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
}
