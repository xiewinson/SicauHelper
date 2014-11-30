package cn.com.pplo.sicauhelper.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import cn.com.pplo.sicauhelper.action.StatusAction;
import cn.com.pplo.sicauhelper.ui.adapter.GoodsAdapter;
import cn.com.pplo.sicauhelper.ui.adapter.StatusAdapter;
import cn.com.pplo.sicauhelper.util.UIUtil;

public class SearchStatusActivity extends BaseActivity {

    private static final String EXTRA_SCHOOL = "school";

    private ListView listView;
    private View footerView;
    private StatusAdapter statusAdapter;
    private SearchView searchView;

    private List<AVObject> data = new ArrayList<AVObject>();
    private String queryStr = "";

    public static void startSearchStatusActivity(Context context) {
        Intent intent = new Intent(context, SearchStatusActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_status);
        setUp(this);
    }

    private void setUp(Context context) {
        UIUtil.setActionBarColor(context, getSupportActionBar(), R.color.red_500);

        listView = (ListView) findViewById(R.id.search_status_listView);
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
                        new StatusAction().findDataByTitleSinceId(
                                SearchStatusActivity.this,
                                queryStr,
                                data.get(data.size() - 1).getLong("goods_id"),
                                new FindCallback<AVObject>() {
                                    @Override
                                    public void done(List<AVObject> list, AVException e) {
                                        if (e == null) {
                                            Log.d("winson", list.size() + "个");
                                            if (list.size() == 0) {
                                                UIUtil.showShortToast(SearchStatusActivity.this, "臣未找到更多帖子！");
                                                footerView.setVisibility(View.INVISIBLE);
                                            } else {
                                                notifyDataSetChanged(list);
                                                footerView.setVisibility(View.GONE);
                                            }
                                        } else {
                                            UIUtil.showShortToast(SearchStatusActivity.this, "大王是否网络状况不佳");
                                            Log.d("winson", "出错：" + e.getMessage());
                                            footerView.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    /**
     * 数据更新
     *
     * @param list
     */
    private void notifyDataSetChanged(List<AVObject> list) {
        data.addAll(list);
        statusAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_goods, menu);
        initSearchView(menu);
        return true;
    }

    //初始化搜索框
    private void initSearchView(Menu menu) {
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("请输入商品标题");
        searchView.onActionViewExpanded();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                data.clear();
                statusAdapter.notifyDataSetChanged();
                footerView.setVisibility(View.GONE);

                queryStr = query;
                new StatusAction().findDataByTitle(
                        SearchStatusActivity.this,
                        queryStr, new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (e == null) {
                            Log.d("winson", list.size() + "个");
                            if (list.size() == 0) {
                                UIUtil.showShortToast(SearchStatusActivity.this, "臣未找到与大王所搜之词相匹配之帖");
                            }
                            notifyDataSetChanged(list);
                            listView.setSelection(0);
                        } else {
                            UIUtil.showShortToast(SearchStatusActivity.this, "大王是否网络状况不佳");
                            Log.d("winson", "出错：" + e.getMessage());
                        }
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                data.clear();
                statusAdapter.notifyDataSetChanged();
                footerView.setVisibility(View.GONE);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
}

