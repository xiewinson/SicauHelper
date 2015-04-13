package cn.com.pplo.sicauhelper.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;

import java.util.ArrayList;
import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.action.CommentAction;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.service.MessageService;
import cn.com.pplo.sicauhelper.ui.adapter.CommentAdapter;
import cn.com.pplo.sicauhelper.util.SharedPreferencesUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;
import cn.com.pplo.sicauhelper.widget.ViewPadding;

public class CommentActivity extends BaseActivity {

    public static final String EXTRA_OBJECT_ID = "object_id";
    public static final String EXTRA_COMMENT_TYPE = "comment_type";
    public static final String EXTRA_TITLE = "comment_title";
    private String objectId;
    private String title;
    private int commentType;
    /**
     * 评论类型
     */
    public static enum COMMENT_TYPE {
        GOODS_SEND_COMMENT, GOODS_RECEIVE_COMMENT, STATUS_SEND_COMMENT, STATUS_RECEIVE_COMMENT
    }

    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View footerView;
    private CommentAdapter commentAdapter;
    private List<AVObject> data = new ArrayList<AVObject>();

    public static void startCommentActivity(Context context, String objectId, int commentType, String title) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra(EXTRA_OBJECT_ID, objectId);
        intent.putExtra(EXTRA_COMMENT_TYPE, commentType);
        intent.putExtra(EXTRA_TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        setUp(this);
    }

    private void setUp(Context context) {
        objectId = getIntent().getStringExtra(EXTRA_OBJECT_ID);
        commentType = getIntent().getIntExtra(EXTRA_COMMENT_TYPE, 0);
        title = getIntent().getStringExtra(EXTRA_TITLE);

        getSupportActionBar().setTitle(title);

        listView = (ListView) findViewById(R.id.comment_listView);

        //刷新
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.status_swipe_container);
        swipeRefreshLayout.setColorSchemeResources(SicauHelperApplication.getPrimaryColor(context, false));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                findNewCommentData(AVQuery.CachePolicy.NETWORK_ONLY, objectId, commentType);
                footerView.setVisibility(View.GONE);
            }
        });

        //加载更多进度条
        footerView = View.inflate(context, R.layout.footer_progress, null);
        footerView.setVisibility(View.GONE);
        listView.addFooterView(footerView);
        listView.setFooterDividersEnabled(false);
        listView.addFooterView(ViewPadding.getActionBarPadding(this, android.R.color.white), null, false);

        commentAdapter = new CommentAdapter(context, data);
        listView.setAdapter(commentAdapter);

        //获取新数据
        findNewCommentData(AVQuery.CachePolicy.CACHE_THEN_NETWORK, objectId, commentType);

        //滑到最下面后加载更多
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
                        findCommentById(objectId, data.get(data.size() - 1).getLong("comment_id"), commentType);
                    }
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(commentType == CommentAction.GOODS_SEND_COMMENT || commentType == CommentAction.GOODS_RECEIVE_COMMENT) {
                    AVObject avGoods = data.get((int) id).getAVObject(TableContract.TableGoodsComment._GOODS);
                    if(avGoods == null) {
                        UIUtil.showShortToast(CommentActivity.this, "臣以为这条评论所在的商品已被丢弃");
                    }
                    else {
                        GoodsActivity.startGoodsActivity(CommentActivity.this,
                                avGoods.getObjectId(),
                                SicauHelperApplication.getStudent().getInt(TableContract.TableUser._SCHOOL));
                    }

                }
                else {
                    AVObject avStatus = data.get((int) id).getAVObject(TableContract.TableStatusComment._STATUS);
                    if(avStatus == null) {
                        UIUtil.showShortToast(CommentActivity.this, "臣以为这条评论所在的帖子已被丢弃");
                    }
                    else {
                        StatusActivity.startStatusActivity(CommentActivity.this,
                                avStatus.getObjectId(),
                                SicauHelperApplication.getStudent().getInt(TableContract.TableUser._SCHOOL));
                    }

                }
            }
        });

        //记录消息数量、清除通知
        new CommentAction().countCommentByType(this, commentType, objectId, new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if(e != null) {
                    Log.d("wnson", "获取消息条数出错：" + e.getMessage());
                }
                else {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if(commentType == CommentAction.GOODS_RECEIVE_COMMENT) {
                        SharedPreferencesUtil.put(CommentActivity.this, SharedPreferencesUtil.CURRENT_GOODS_COMMENT_COUNT, i);
                        notificationManager.cancel(MessageService.TYPE_GOODS);
                    }
                    else if(commentType == CommentAction.STATUS_RECEIVE_COMMENT) {
                        SharedPreferencesUtil.put(CommentActivity.this, SharedPreferencesUtil.CURRENT_STATUS_COMMENT_COUNT, i);
                        notificationManager.cancel(MessageService.TYPE_STATUS);
                    }
                }

            }
        });
    }


    /**
     * 从网络取新的并清空缓存
     */
    private void findNewCommentData(AVQuery.CachePolicy cachePolicy, String objectId, int commentType) {
        Log.d("winson", "请求类型：" + commentType);
        swipeRefreshLayout.setRefreshing(true);
        footerView.setVisibility(View.GONE);
        new CommentAction().findNewDataByType(CommentActivity.this, cachePolicy, commentType, objectId, new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if(list.size() == 0) {
                        UIUtil.showShortToast(CommentActivity.this, "臣以为没有评论和大王有关");
                    }
                    notifyDataSetChanged(list, true);
//                    listView.setSelection(0);
                } else {
                    if (!e.getMessage().contains("Cache")) {
                        UIUtil.showShortToast(CommentActivity.this, "你的网络好像有点问题，刷新试试吧");
                    }
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
    private void findCommentById(String objectId, long comment_id, int commentType) {
        new CommentAction().findSinceIdByType(CommentActivity.this, commentType, objectId, comment_id, new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    Log.d("winson", list.size() + "个");
                    if (list.size() == 0) {
                        footerView.setVisibility(View.INVISIBLE);
                    } else {
                        notifyDataSetChanged(list, false);
                        footerView.setVisibility(View.GONE);
                    }
                } else {
                    UIUtil.showShortToast(CommentActivity.this, "你的网络好像有点问题，重新试试吧");
                    Log.d("winson", "出错：" + e.getMessage());
                    footerView.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 数据更新
     *
     * @param list
     * @param isRefresh
     */
    private void notifyDataSetChanged(List<AVObject> list, boolean isRefresh) {
        if (isRefresh) {
            data.clear();
        }
        data.addAll(list);
        commentAdapter.notifyDataSetChanged();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            findNewCommentData(AVQuery.CachePolicy.NETWORK_ONLY, objectId, commentType);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
