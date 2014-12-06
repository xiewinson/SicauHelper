package cn.com.pplo.sicauhelper.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.action.CommentAction;
import cn.com.pplo.sicauhelper.action.StatusAction;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.ui.adapter.CommentAdapter;
import cn.com.pplo.sicauhelper.util.DialogUtil;
import cn.com.pplo.sicauhelper.util.ImageUtil;
import cn.com.pplo.sicauhelper.util.TimeUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;
import cn.com.pplo.sicauhelper.util.UserUtil;
import cn.com.pplo.sicauhelper.widget.ViewPadding;
import de.hdodenhof.circleimageview.CircleImageView;

public class StatusActivity extends BaseActivity {

    private static final String EXTRA_OBJECT_ID = "object_id";
    private static final String EXTRA_SCHOOL = "school";
    private static final int ACTION_ITEM_DELETE_STATUS = -1000;
    private static final int ACTION_ITEM_EDIT_STATUS = -1001;

    private ListView listView;
    private EditText commentEt;
    private Button sendBtn;

    private AlertDialog progressDialog;

    private View headerView;
    private CircleImageView headIv;
    private TextView nameTv;
    private TextView dateTv;
    private TextView categoryTv;
    private TextView titleTv;
    private TextView contentTv;
    private LinearLayout imageLayout;
    private TextView commentBtn;
    private TextView deviceTv;
    private TextView locationTv;

    private SwipeRefreshLayout swipeRefreshLayout;
    private View footerView;

    private String objectId;
    private int school;
    private AVObject avStatus;

    //接收回复者
    private AVObject receiveStudent;

    private CommentAdapter commentAdapter;
    private List<AVObject> data = new ArrayList<AVObject>();


    public static void startStatusActivity(Context context, String objectId, int school) {
        Intent intent = new Intent(context, StatusActivity.class);
        intent.putExtra(EXTRA_OBJECT_ID, objectId);
        intent.putExtra(EXTRA_SCHOOL, school);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        getSupportActionBar().setTitle("帖子");
        setUp(this);
    }

    private void setUp(final Context context) {
        objectId = getIntent().getStringExtra(EXTRA_OBJECT_ID);
        school = getIntent().getIntExtra(EXTRA_SCHOOL, 0);

        progressDialog = UIUtil.getProgressDialog(context, "正在发表评论...");

        listView = (ListView) findViewById(R.id.comment_listView);
        commentEt = (EditText) findViewById(R.id.comment_et);
        sendBtn = (Button) findViewById(R.id.comment_send_btn);

        UIUtil.setActionBarColor(context,getSupportActionBar(), R.color.light_blue_500);

        //刷新
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.status_swipe_container);
        swipeRefreshLayout.setColorSchemeResources(R.color.red_500, R.color.orange_500, R.color.green_500);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                initStatusData(context);
                findNewData(AVQuery.CachePolicy.NETWORK_ONLY, objectId);
                footerView.setVisibility(View.GONE);
            }
        });

        //header
        headerView = View.inflate(context, R.layout.header_status, null);
        headIv = (CircleImageView) headerView.findViewById(R.id.status_head_iv);
        nameTv = (TextView) headerView.findViewById(R.id.status_name_tv);
        dateTv = (TextView) headerView.findViewById(R.id.status_time_tv);
        categoryTv = (TextView) headerView.findViewById(R.id.status_category_tv);
        titleTv = (TextView) headerView.findViewById(R.id.status_title_tv);
        contentTv = (TextView) headerView.findViewById(R.id.status_content_tv);
        imageLayout = (LinearLayout) headerView.findViewById(R.id.status_image_layout);
        commentBtn = (TextView) headerView.findViewById(R.id.status_comment_btn);
        deviceTv = (TextView) headerView.findViewById(R.id.status_device_tv);
        locationTv = (TextView) headerView.findViewById(R.id.status_location_tv);
        headerView.setVisibility(View.GONE);
        listView.addHeaderView(headerView, null, false);

        //加载更多进度条
        footerView = View.inflate(context, R.layout.footer_progress, null);
        footerView.setVisibility(View.GONE);
        listView.addFooterView(footerView);
        listView.setFooterDividersEnabled(false);
        listView.setHeaderDividersEnabled(false);
        listView.addFooterView(ViewPadding.getActionBarPadding(this, android.R.color.white));

        commentAdapter = new CommentAdapter(context, data);
        listView.setAdapter(commentAdapter);
        //点击出现选项菜单
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (data.size() > 0 && data.size() > id) {
                    showOptionDialog(context, data.get((int) id));
                }
            }
        });

        //初始化帖子数据
        initStatusData(context);
        //初始化评论数据
        initCommentData();
    }

    /**
     * 显示选项dialog
     */
    private void showOptionDialog(final Context context, final AVObject avComment) {
        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String[] optionArray = null;

        AVUser avUser = SicauHelperApplication.getStudent();
        //若为管理员或者是本人发表
        Log.d("winson", "是否管理员：" + UserUtil.isAdmin(avUser));
        if (UserUtil.isAdmin(avUser)){
            optionArray = getResources().getStringArray(R.array.comment_option_0);
        }
        else if (avUser.getString(TableContract.TableUser._SID).equals(avComment.getAVObject(TableContract.TableStatusComment._SEND_USER).getString(TableContract.TableUser._SID))) {
            optionArray = getResources().getStringArray(R.array.comment_option_0);
        }
        //若为普通用户
        else {
            optionArray = getResources().getStringArray(R.array.comment_option_1);
        }

        builder.setItems(optionArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //回复
                if (which == 0) {
                    receiveStudent = avComment.getAVObject(TableContract.TableStatusComment._SEND_USER);
                    commentEt.setHint("回复 " + receiveStudent.getString(TableContract.TableUser._NICKNAME) + " 的评论");
                }
                //投诉
                else if (which == 1) {
                    DialogUtil.showComplainDialog(context, DialogUtil.STATUS_COMMENT, avComment);
                }
                //删除
                else {
                    DialogUtil.showDeleteDialog(context, DialogUtil.STATUS_COMMENT, avComment, new DeleteCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                footerView.setVisibility(View.GONE);
                                UIUtil.showShortToast(context, "删除成功");
                                addCommentCount(false);
                                //从列表中删除刚才从网络删除的comment
                                data.remove(avComment);
                                commentAdapter.notifyDataSetChanged();
                            } else {
                                UIUtil.showShortToast(context, "删除失败");
                                Log.d("winson", "删除失败：" + e.getMessage());
                            }
                        }
                    });
                }
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }


    /**
     * 初始化status数据
     *
     * @param context
     */
    private void initStatusData(final Context context) {
        new StatusAction().findByObjectId(AVQuery.CachePolicy.CACHE_THEN_NETWORK, objectId, new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException e) {
                if (e == null) {
//                    更新菜单
                    invalidateOptionsMenu();
                    Log.d("winson", "更新帖子数据");
                    avStatus = avObjects.get(0);
                    final AVObject avStudent = avStatus.getAVObject(TableContract.TableStatus._USER);
                    //头像
                    ImageLoader.getInstance().displayImage(avStudent.getAVFile(TableContract.TableUser._PROFILE_URL).getUrl(), headIv, ImageUtil.getDisplayProfileOption(context));
                    //点击头像打开UserActivity
                    headIv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UserActivity.startUserActivity(context, avStudent.getObjectId());
                        }
                    });
                    //名字
                    nameTv.setText(avStudent.getString(TableContract.TableUser._NICKNAME));
                    //时间
                    dateTv.setText(TimeUtil.timeToFriendlTime(avStatus.getCreatedAt().toString()));
                    //类别(暂时不用)
//                    categoryTv.setText("￥" + avStatus.getInt(TableContract.TableStatus._PRICE) + "");
                    //标题
                    titleTv.setText(avStatus.getString(TableContract.TableStatus._TITLE));
                    //内容
                    contentTv.setText(avStatus.getString(TableContract.TableStatus._CONTENT));
                    //评论
                    commentBtn.setText(avStatus.getLong(TableContract.TableStatus._COMMENT_COUNT) + "");
                    commentBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            receiveStudent = null;
                            commentEt.setHint("");
                        }
                    });
                    //手机
                    deviceTv.setText("来自 " + avStatus.getString(TableContract.TableStatus._BRAND) + " "
                            + avStatus.getString(TableContract.TableStatus._MODEL) + " ("
                            + avStatus.getString(TableContract.TableStatus._VERSION) + ") ");
                    //地址
                    String address = avStatus.getString(TableContract.TableStatus._ADDRESS);
                    locationTv.setText(address);
                    if(TextUtils.isEmpty(address)) {
                        locationTv.setVisibility(View.GONE);
                    }
                    //显示图片
                    final List<AVFile> imageList = ImageUtil.getAVFileListByAVObject(avStatus);
                    //图片url列表
                    final String[] imageUrl = ImageUtil.getImageUrlsByAVFileList(imageList);

                    imageLayout.removeAllViews();
                    int childPosition = 0;
                    for (AVFile avFile : imageList) {
                        ImageView imageView = ImageUtil.getThumImageView(imageUrl, childPosition, avFile, context);
                        imageLayout.addView(imageView);
                        childPosition ++;
                    }
                    //显示headerView
                    headerView.setVisibility(View.VISIBLE);
                } else {
//                    UIUtil.showShortToast(context, "你的网络好像不太好");
                    //帖子已被删除
                    if(e.getMessage().contains("java.lang.IndexOutOfBoundsException")){
                        UIUtil.showShortToast(context, "帖子应该已经被删除了，找不到它了");
                    }
                    Log.d("winson", "加载出错：" + e.getMessage());
                }
            }
        });
    }



    /**
     * 获得评论列表
     */
    private void initCommentData() {

        //发送评论
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentStr = commentEt.getText().toString().trim();
                UIUtil.hideSoftKeyboard(StatusActivity.this, sendBtn);
                if (TextUtils.isEmpty(commentStr)) {
                    UIUtil.showShortToast(StatusActivity.this, "评论不可以是空的哦");
                } else {
                    sendComment(commentStr);
                }
            }
        });

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
                        findCommentById(objectId, data.get(data.size() - 1).getLong(TableContract.TableStatusComment._STATUS_COMMENT_ID));
                    }
                }
            }
        });

        findNewData(AVQuery.CachePolicy.CACHE_THEN_NETWORK, objectId);
    }

    /**
     * 发布评论
     *
     * @param commentStr
     */
    private void sendComment(String commentStr) {
        if(progressDialog != null) {
            progressDialog.show();
        }
        AVObject avComment = new AVObject(TableContract.TableStatusComment.TABLE_NAME);

        //存帖子
        avComment.put(TableContract.TableStatusComment._STATUS, AVObject.createWithoutData(TableContract.TableStatus.TABLE_NAME,
                objectId));
        //发送者
        avComment.put(TableContract.TableStatusComment._SEND_USER, SicauHelperApplication.getStudent());
        //接收者
        if (receiveStudent != null) {
            avComment.put(TableContract.TableStatusComment._RECEIVE_USER, receiveStudent);
        }
        //内容
        avComment.put(TableContract.TableStatusComment._CONTENT, commentStr);
        //手机型号
        avComment.put(TableContract.TableStatusComment._MODEL, Build.MODEL);
        //手机品牌
        avComment.put(TableContract.TableStatusComment._BRAND, Build.BRAND);
        //手机系统版本
        avComment.put(TableContract.TableStatusComment._VERSION, Build.VERSION.RELEASE);

        //是否匿名
        avComment.put(TableContract.TableStatusComment._INVISIBLE, false);
        //是否已经阅读过
        avComment.put(TableContract.TableStatusComment._IS_WATCHED, true);

        avComment.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                UIUtil.dismissProgressDialog(progressDialog);
                if (e != null) {
                    Log.d("winson", e.getMessage());
                    UIUtil.showShortToast(StatusActivity.this, "发布失败，请重试");
                } else {
                    UIUtil.showShortToast(StatusActivity.this, "发布成功");
                    //清空editText
                    commentEt.setHint("");
                    commentEt.setText("");
                    receiveStudent = null;
                    addCommentCount(true);
                }
            }
        });
    }

    /**
     * 更新评论数量
     */
    private void addCommentCount(final boolean isRefreshComment) {
        new CommentAction().count(CommentAction.COMMENT_STATUS, avStatus, new CountCallback() {
            @Override
            public void done(int count, AVException e) {
                if (e != null) {
                    Log.d("winson", e.getMessage());
                } else {
                    //得到评论数
                    avStatus.setFetchWhenSave(true);
                    avStatus.put(TableContract.TableStatus._COMMENT_COUNT, count);
                    avStatus.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e != null) {
                                Log.d("winson", e.getMessage());
                            } else {
                                Log.d("winson", "更新评论数量成功");
                            }
                            //更新帖子数据和评论
                            initStatusData(StatusActivity.this);
                            if (isRefreshComment) {
                                findNewData(AVQuery.CachePolicy.NETWORK_ONLY, objectId);
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 从缓存中取
     */
    private void findNewData(AVQuery.CachePolicy cachePolicy, final String objectId) {
        swipeRefreshLayout.setRefreshing(true);
        new CommentAction().findNewDataByObjectId(StatusActivity.this, cachePolicy, CommentAction.COMMENT_STATUS, objectId, new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    Log.d("winson", list.size() + "个");
                    notifyDataSetChanged(list, true);
                } else {
                    if (!e.getMessage().contains("Cache")) {
                        UIUtil.showShortToast(StatusActivity.this, "你的网络好像有点问题，刷新试试吧");
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
    private void findCommentById(String objectId, long comment_id) {
        new CommentAction().findSinceId(StatusActivity.this, CommentAction.COMMENT_STATUS, objectId, comment_id, new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    Log.d("winson", list.size() + "个");
                    if (list.size() == 0) {
                        UIUtil.showShortToast(StatusActivity.this, "已经没有更多评论啦！");
                        footerView.setVisibility(View.INVISIBLE);
                    } else {
                        notifyDataSetChanged(list, false);
                        footerView.setVisibility(View.GONE);
                    }
                } else {
                    UIUtil.showShortToast(StatusActivity.this, "你的网络好像有点问题，重新试试吧");
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
    protected void onRestart() {
        super.onRestart();
        initStatusData(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_status, menu);
        //若为管理员或者是本人发表则添加删除按钮
        AVUser avUser = SicauHelperApplication.getStudent();
        if (avStatus != null && (UserUtil.isAdmin(avUser) ||
                avUser.getString(TableContract.TableUser._SID).equals(avStatus.getAVObject(TableContract.TableStatus._USER).getString(TableContract.TableUser._SID)))){
            menu.add(0, ACTION_ITEM_DELETE_STATUS, 0, "删除")
                    .setIcon(R.drawable.ic_delete_white_24dp)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        //若为本人发表则添加修改按钮
        if (avStatus != null && avUser.getString(TableContract.TableUser._SID).equals(avStatus.getAVObject(TableContract.TableStatus._USER).getString(TableContract.TableUser._SID))){
            menu.add(1, ACTION_ITEM_EDIT_STATUS, 1, "修改")
                    .setIcon(R.drawable.ic_mode_edit_white_24dp)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //
        if (id == R.id.action_refresh) {

            initStatusData(this);
            findNewData(AVQuery.CachePolicy.NETWORK_ONLY, objectId);
            return true;
        }
        //删除帖子信息
        else if(id == ACTION_ITEM_DELETE_STATUS) {
            DialogUtil.showDeleteDialog(this, DialogUtil.STATUS, avStatus, new DeleteCallback() {
                @Override
                public void done(AVException e) {
                    if(e == null) {
                        UIUtil.showShortToast(StatusActivity.this, "帖子应该已经被删除了，找不到它了");
                        StatusActivity.this.finish();
                        Log.d("winson", "删除帖子成功");
                    }
                    else {
                        UIUtil.showShortToast(StatusActivity.this, "帖子删除失败了");
                        Log.d("winson", "删除帖子失败：" + e.getMessage());
                    }
                }
            });
        }
        //修改帖子信息
        else if(id == ACTION_ITEM_EDIT_STATUS) {
            AddActivity.startAddActivity(this, AddActivity.TYPE_STATUS, objectId);
        }

        //投诉帖子
        else if(id == R.id.action_complain) {
            DialogUtil.showComplainDialog(this, DialogUtil.STATUS, avStatus);
        }


        return super.onOptionsItemSelected(item);
    }
}
