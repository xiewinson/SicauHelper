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
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.action.GoodsAction;
import cn.com.pplo.sicauhelper.action.GoodsCommentAction;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.ui.adapter.CommentAdapter;
import cn.com.pplo.sicauhelper.util.ImageUtil;
import cn.com.pplo.sicauhelper.util.TimeUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;
import cn.com.pplo.sicauhelper.widget.ViewPadding;
import de.hdodenhof.circleimageview.CircleImageView;

public class GoodsActivity extends BaseActivity {

    private static final String EXTRA_OBJECT_ID = "object_id";
    private static final String EXTRA_SCHOOL = "school";

    private ListView listView;
    private EditText commentEt;
    private Button sendBtn;

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
    private AVObject avGoods;

    //接收回复者
    private AVObject receiveStudent;

    private CommentAdapter commentAdapter;
    private List<AVObject> data = new ArrayList<AVObject>();


    public static void startGoodsActivity(Context context, String objectId, int school) {
        Intent intent = new Intent(context, GoodsActivity.class);
        intent.putExtra(EXTRA_OBJECT_ID, objectId);
        intent.putExtra(EXTRA_SCHOOL, school);
        context.startActivity(intent);
    }

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods);
        getSupportActionBar().setTitle("");
        setUp(this);
    }

    private void setUp(final Context context) {
        objectId = getIntent().getStringExtra(EXTRA_OBJECT_ID);
        school = getIntent().getIntExtra(EXTRA_SCHOOL, 0);

        listView = (ListView) findViewById(R.id.comment_listView);
        commentEt = (EditText) findViewById(R.id.comment_et);
        sendBtn = (Button) findViewById(R.id.comment_send_btn);

        UIUtil.setActionBarColorBySchool(context, school, getSupportActionBar());

        //下拉刷新
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.goods_swipe_container);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_500, R.color.orange_500, R.color.green_500);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                initGoodsData(context);
                findNewData(objectId);
                footerView.setVisibility(View.GONE);
            }
        });

        //header
        headerView = View.inflate(context, R.layout.header_goods, null);
        headIv = (CircleImageView) headerView.findViewById(R.id.goods_head_iv);
        nameTv = (TextView) headerView.findViewById(R.id.goods_name_tv);
        dateTv = (TextView) headerView.findViewById(R.id.goods_time_tv);
        categoryTv = (TextView) headerView.findViewById(R.id.goods_category_tv);
        titleTv = (TextView) headerView.findViewById(R.id.goods_title_tv);
        contentTv = (TextView) headerView.findViewById(R.id.goods_content_tv);
        imageLayout = (LinearLayout) headerView.findViewById(R.id.goods_image_layout);
        commentBtn = (TextView) headerView.findViewById(R.id.goods_comment_btn);
        deviceTv = (TextView) headerView.findViewById(R.id.goods_device_tv);
        locationTv = (TextView) headerView.findViewById(R.id.goods_location_tv);
        headerView.setVisibility(View.GONE);
        listView.addHeaderView(headerView, null, false);

        //加载更多进度条
        footerView = View.inflate(context, R.layout.footer_progress, null);
        footerView.setVisibility(View.GONE);
        listView.addFooterView(footerView);
        listView.setFooterDividersEnabled(false);
        listView.addFooterView(ViewPadding.getActionBarPadding(this, android.R.color.white));

        commentAdapter = new CommentAdapter(context, data);
        listView.setAdapter(commentAdapter);
        //点击出现选项菜单
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showOptionDialog(context, data.get((int) id));
            }
        });

        //初始化商品数据
        initGoodsData(context);
        //初始化评论数据
        initCommentData();
    }

    /**
     * 显示选项dialog
     */
    private void showOptionDialog(Context context, final AVObject avComment) {
        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String[] optionArray = null;

        AVUser avUser = SicauHelperApplication.getStudent();
        Log.d("winson", "学号：" + avComment.getAVObject(TableContract.TableGoodsComment._SEND_USER).getString(TableContract.TableStudent._SID));
        int roleId = avUser.getInt(TableContract.TableStudent._ROLE);
        //若为管理员或者是本人发表
        if (roleId == 0 ||
                avUser.getString(TableContract.TableStudent._SID).equals(avComment.getAVObject(TableContract.TableGoodsComment._SEND_USER).getString(TableContract.TableStudent._SID))) {
            optionArray = getResources().getStringArray(R.array.comment_option_0);
        }
        //若为普通用户
        else if(roleId == 1) {
            optionArray = getResources().getStringArray(R.array.comment_option_1);
        }


        builder.setItems(optionArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //回复
                if (which == 0) {
                    receiveStudent = avComment.getAVObject(TableContract.TableGoodsComment._SEND_USER);
                    commentEt.setHint("回复 " + receiveStudent.getString(TableContract.TableStudent._NICKNAME) + " 的评论");
                }
                //投诉
                else if (which == 1) {

                }
                //删除
                else {

                }
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }


    /**
     * 初始化goods数据
     *
     * @param context
     */
    private void initGoodsData(final Context context) {
        new GoodsAction().findByObjectId(AVQuery.CachePolicy.CACHE_THEN_NETWORK, objectId, new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException e) {
                if (e == null) {
                    Log.d("winson", "更新商品数据");
                    avGoods = avObjects.get(0);
                    AVObject avStudent = avGoods.getAVObject(TableContract.TableGoods._USER);
                    //头像
                    ImageLoader.getInstance().displayImage(avStudent.getAVFile(TableContract.TableStudent._PROFILE_URL).getUrl(), headIv, ImageUtil.getDisplayImageOption(context));
                    //名字
                    nameTv.setText(avStudent.getString(TableContract.TableStudent._NAME));
                    //时间
                    dateTv.setText(TimeUtil.timeToFriendlTime(avGoods.getCreatedAt().toString()));
                    //类别(暂时用来写价格)
                    categoryTv.setText("￥" + avGoods.getInt(TableContract.TableGoods._PRICE) + "");
                    //标题
                    titleTv.setText(avGoods.getString(TableContract.TableGoods._TITLE));
                    //内容
                    contentTv.setText(avGoods.getString(TableContract.TableGoods._CONTENT));
                    //评论
                    commentBtn.setText(avGoods.getLong(TableContract.TableGoods._COMMENT_COUNT) + "");
                    //手机
                    deviceTv.setText("来自 " + avGoods.getString(TableContract.TableGoods._BRAND) + " "
                            + avGoods.getString(TableContract.TableGoods._MODEL) + " ("
                            + avGoods.getString(TableContract.TableGoods._VERSION) + ") ");
                    //地址
                    locationTv.setText(avGoods.getString(TableContract.TableGoods._ADDRESS));
                    //显示图片
                    List<AVFile> imageList = ImageUtil.getAVFileListByAVGoods(avGoods);
                    imageLayout.removeAllViews();
                    for (AVFile avFile : imageList) {
                        ImageView imageView = new ImageView(context);
                        int width = (int) UIUtil.parseDpToPx(context, 60);
                        int height = (int) UIUtil.parseDpToPx(context, 40);
                        imageView.setPadding(0, 0, (int) UIUtil.parseDpToPx(context, 8), 0);
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(width, height));
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        ImageLoader.getInstance().displayImage(avFile.getThumbnailUrl(false, width, height), imageView, ImageUtil.getDisplayImageOption(context));
                        imageLayout.addView(imageView);
                    }
                    //显示headerView
                    headerView.setVisibility(View.VISIBLE);
                } else {
//                    UIUtil.showShortToast(context, "你的网络好像不太好");
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
                if (TextUtils.isEmpty(commentStr)) {
                    UIUtil.showShortToast(GoodsActivity.this, "评论不可以是空的哦");
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
                        findById(objectId, data.get(data.size() - 1).getLong(TableContract.TableGoodsComment._GOODS_COMMENT_ID));
                    }
                }
            }
        });
        findInCacheThenNetwork(objectId);
    }

    /**
     * 发布评论
     *
     * @param commentStr
     */
    private void sendComment(String commentStr) {
        AVObject avComment = new AVObject(TableContract.TableGoodsComment.TABLE_NAME);

        //存商品
        avComment.put(TableContract.TableGoodsComment._GOODS, AVObject.createWithoutData(TableContract.TableGoods.TABLE_NAME,
                objectId));
        //发送者
        avComment.put(TableContract.TableGoodsComment._SEND_USER, SicauHelperApplication.getStudent());
        //接收者
        if (receiveStudent != null) {
            avComment.put(TableContract.TableGoodsComment._RECEIVE_USER, receiveStudent);
        }
        //内容
        avComment.put(TableContract.TableGoodsComment._CONTENT, commentStr);
        //手机型号
        avComment.put(TableContract.TableGoodsComment._MODEL, Build.MODEL);
        //手机品牌
        avComment.put(TableContract.TableGoodsComment._BRAND, Build.BRAND);
        //手机系统版本
        avComment.put(TableContract.TableGoodsComment._VERSION, Build.VERSION.RELEASE);

        //是否匿名
        avComment.put(TableContract.TableGoodsComment._INVISIBLE, false);
        //是否已经阅读过
        avComment.put(TableContract.TableGoodsComment._IS_WATCHED, true);

        avComment.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e != null) {
                    Log.d("winson", e.getMessage());
                    UIUtil.showShortToast(GoodsActivity.this, "发布失败，请重试");
                } else {
                    UIUtil.showShortToast(GoodsActivity.this, "发布成功");
                    //清空editText
                    commentEt.setHint("");
                    commentEt.setText("");
                    receiveStudent = null;
                    addCommentCount();
                }
            }
        });
    }

    /**
     * 更新评论数量
     */
    private void addCommentCount() {
        new GoodsCommentAction().count(avGoods, new CountCallback() {
            @Override
            public void done(int count, AVException e) {
                if (e != null) {
                    Log.d("winson", e.getMessage());
                } else {
                    //得到评论数
                    avGoods.setFetchWhenSave(true);
                    avGoods.put(TableContract.TableGoods._COMMENT_COUNT, count);
                    avGoods.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e != null) {
                                Log.d("winson", e.getMessage());
                            } else {
                                Log.d("winson", "更新评论数量成功");
                            }
                            //更新商品数据和评论
                            initGoodsData(GoodsActivity.this);
                            findNewData(objectId);
                        }
                    });
                }
            }
        });
    }

    /**
     * 从缓存中取
     */
    private void findInCacheThenNetwork(final String objectId) {
        new GoodsCommentAction().findInCacheThenNetwork(objectId, new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    Log.d("winson", list.size() + "个");
                    //若为0.则进行网络请求
                    if (list.size() > 0) {
                        notifyDataSetChanged(list, true);
                    }
                } else {
                    Log.d("winson", "出错：" + e.getMessage());
                }
            }
        });
    }

    /**
     * 从网络取新的并清空缓存
     */
    private void findNewData(String objectId) {
        footerView.setVisibility(View.GONE);
        new GoodsCommentAction().findNewData(objectId, new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    Log.d("winson", list.size() + "个");
                    notifyDataSetChanged(list, true);
                    listView.setSelection(0);
                } else {
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
    private void findById(String objectId, long comment_id) {
        new GoodsCommentAction().findSinceId(objectId, comment_id, new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    Log.d("winson", list.size() + "个");
                    if (list.size() == 0) {
                        UIUtil.showShortToast(GoodsActivity.this, "已经没有更多评论啦！");
                        footerView.setVisibility(View.INVISIBLE);
                    } else {
                        notifyDataSetChanged(list, false);
                        footerView.setVisibility(View.GONE);
                    }
                } else {
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
//        getMenuInflater().inflate(R.menu.menu_goods, menu);
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
            swipeRefreshLayout.setRefreshing(true);
            initGoodsData(this);
            findNewData(objectId);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
