package cn.com.pplo.sicauhelper.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.action.GoodsAction;
import cn.com.pplo.sicauhelper.action.StatusAction;
import cn.com.pplo.sicauhelper.action.UserAction;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.util.DialogUtil;
import cn.com.pplo.sicauhelper.util.ImageUtil;
import cn.com.pplo.sicauhelper.util.SharedPreferencesUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends BaseActivity {

    public static final String EXTRA_OBJECT_ID = "object_id";

    /**
     * 拍照后存储的位置
     */
    private String captureImagePath = "";
    private String resultPath;

    private int primaryColor;
    private String objectId;
    private AVUser avUser;

    private CircleImageView headIv;
    private View backgroundView;
    private TextView nameTv;
    private TextView schoolTv;
    private TextView gradeTv;

    private TextView goodsCountTv;
    private TextView statusCountTv;

    private Button editHeadBtn;
    private Button editNicknameBtn;


    public static void startUserActivity(Context context, String objectId) {
        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra(EXTRA_OBJECT_ID, objectId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setUp(this);
    }

    private void setUp(Context context) {
        //裁剪后的图片地址
        resultPath = ImageUtil.getImageFolderPath(this) + File.separator + System.currentTimeMillis() + ".jpg";

        //设置actionbar透明
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        getSupportActionBar().setTitle("");
        objectId = getIntent().getStringExtra(EXTRA_OBJECT_ID);

        headIv = (CircleImageView) findViewById(R.id.user_head_iv);
        nameTv = (TextView) findViewById(R.id.user_name_tv);
        schoolTv = (TextView) findViewById(R.id.user_school_tv);
        gradeTv = (TextView) findViewById(R.id.user_grade_tv);
        backgroundView = findViewById(R.id.user_background);

        goodsCountTv = (TextView) findViewById(R.id.user_goods_tv);
        statusCountTv = (TextView) findViewById(R.id.user_status_tv);

        editHeadBtn = (Button) findViewById(R.id.edit_head_btn);
        editNicknameBtn = (Button) findViewById(R.id.edit_nickname_btn);

        findUser(objectId, AVQuery.CachePolicy.CACHE_THEN_NETWORK);
    }

    /**
     * 获取user
     *
     * @param objectId
     * @param cachePolicy
     */
    private void findUser(final String objectId, AVQuery.CachePolicy cachePolicy) {
        new UserAction().findByObjectId(cachePolicy, objectId, new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if (e == null && list.size() > 0) {
                    initView(list);
                } else {
                    if (e != null && !e.getMessage().contains("Cache")) {
                        UIUtil.showShortToast(UserActivity.this, "你的网络好像有点问题，刷新试试吧");
                    }
                }
            }
        });
    }

    private void initView(List<AVUser> list) {
        avUser = list.get(0);

        primaryColor = SicauHelperApplication.getPrimaryColor(this, false);

        OnCountBtnClickListener onCountBtnClickListener = new OnCountBtnClickListener(this);

        goodsCountTv.setOnClickListener(onCountBtnClickListener);
        statusCountTv.setOnClickListener(onCountBtnClickListener);

        nameTv.setText(avUser.getString(TableContract.TableUser._NICKNAME));
        int school = avUser.getInt(TableContract.TableUser._SCHOOL);
        backgroundView.setBackgroundResource(primaryColor);
        schoolTv.setText(getResources().getStringArray(R.array.school)[school] + "校区");
        gradeTv.setText(avUser.getString(TableContract.TableUser._SID).substring(0, 4) + " 级");
        ImageLoader.getInstance().displayImage(avUser.getAVFile(TableContract.TableUser._PROFILE_URL).getUrl(), headIv, ImageUtil.getDisplayProfileOption(this));

        //点击头像显示大图
        headIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryActivity.startGalleryActivity(UserActivity.this, new String[]{avUser.getAVFile(TableContract.TableUser._PROFILE_URL).getUrl()}, 0);
            }
        });

        //修改昵称和修改头像
        if(SicauHelperApplication.getStudent().getObjectId().equals(objectId)) {
            editHeadBtn.setVisibility(View.VISIBLE);
            editNicknameBtn.setVisibility(View.VISIBLE);

            editHeadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (avUser == null) {
                        UIUtil.showShortToast(UserActivity.this, "人都还没获取到，先别改头像");
                    } else {
                        getImageSelectDialog(UserActivity.this).show();
                    }
                }
            });

            editNicknameBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (avUser == null) {
                        UIUtil.showShortToast(UserActivity.this, "人都还没获取到，先别改名字");
                    } else {
                        DialogUtil.showEditNicknameDialog(UserActivity.this, avUser, new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if(e == null) {
                                    UIUtil.showShortToast(UserActivity.this, "修改昵称成功");
                                    findUser(objectId, AVQuery.CachePolicy.NETWORK_ONLY);
                                    SicauHelperApplication.clearStudent();
                                }
                                else {
                                    Log.d("winson", "修改昵称失败：" + e.getMessage());
                                    UIUtil.showShortToast(UserActivity.this, "修改昵称失败了");
                                }
                            }
                        });
                    }
                }
            });
        }

        getGoodsCount();
        getStatusCount();
    }

    private void getStatusCount() {
        new StatusAction().countStatusByUser(objectId, new CountCallback() {
            @Override
            public void done(int count, AVException e) {
                if (e != null && !e.getMessage().contains("Cache")) {
//                    UIUtil.showShortToast(UserActivity.this, "臣未算出此人所发帖子之数");
                } else {
                    statusCountTv.setText("圈子数量 " + count);
                    statusCountTv.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void getGoodsCount() {
        new GoodsAction().countGoodsByUser(objectId, new CountCallback() {
            @Override
            public void done(int count, AVException e) {
                if (e != null && !e.getMessage().contains("Cache")) {
//                    UIUtil.showShortToast(UserActivity.this, "臣未算出此人所发商品之数");
                } else {
                    goodsCountTv.setText("商品数量 " + count);
                    goodsCountTv.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

            getMenuInflater().inflate(R.menu.global, menu);


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
            findUser(objectId, AVQuery.CachePolicy.NETWORK_ONLY);
            return true;
        }
        //修改昵称
        else if (id == R.id.action_edit_nickname) {

            return true;
        }
        //修改头像
        else if(id == R.id.action_edit_profile) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 取得图片选择Dialog
     *
     * @param context
     * @return
     */
    private AlertDialog getImageSelectDialog(final Context context) {
        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.image_select, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //立刻拍照
                if (which == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    captureImagePath = ImageUtil.getImageFolderPath(context) + File.separator + System.currentTimeMillis() + ".jpg";
                    File file = new File(captureImagePath);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    startActivityForResult(intent, ImageUtil.REQUEST_CODE_CAPTURE_IMAGE);
                }
                //选择图片
                else if (which == 1) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                    intent.setType("image/*");
                    startActivityForResult(intent, ImageUtil.REQUEST_CODE_PICK_IMAGE);
                }
            }
        });
        alertDialog = builder.create();
        return alertDialog;
    }

    //点击数量
    private class OnCountBtnClickListener implements View.OnClickListener {
        private Context context;

        private OnCountBtnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            //点击商品数量
            if (id == R.id.user_goods_tv) {
                UserGoodsActivity.startUserGoodsActivity(context, avUser.getInt(TableContract.TableUser._SCHOOL), objectId, avUser.getString(TableContract.TableUser._NICKNAME));
            }
            //点击评论数量
            else if (id == R.id.user_status_tv) {
                UserStatusActivity.startUserStatusActivity(context, avUser.getInt(TableContract.TableUser._SCHOOL), objectId, avUser.getString(TableContract.TableUser._NICKNAME));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                //若是从图库选择图
                case ImageUtil.REQUEST_CODE_PICK_IMAGE:
                    ImageUtil.cropImage(this, data.getData(), resultPath);
                    break;
                //调用相机
                case ImageUtil.REQUEST_CODE_CAPTURE_IMAGE:
                    ImageUtil.cropImage(this, Uri.fromFile(new File(captureImagePath)), resultPath);
                    break;

                //裁剪图片
                case ImageUtil.REQUEST_CODE_CROP_IMAGE:
                    //添加图片到list并且显示出来

                    //上传图片
                    if (!TextUtils.isEmpty(resultPath)) {

                        try {
                            final AVFile avFile = AVFile.withAbsoluteLocalPath(
                                    SicauHelperApplication.getStudent().getString(TableContract.TableUser._SID)
                                            + "_profile_" + System.currentTimeMillis() + ".jpg", resultPath);

                            if (avFile != null) {
                                uploadImage(this, avFile, avUser);
                            } else {
                                UIUtil.showShortToast(this, "图片上传出错");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    } else {
                        UIUtil.showShortToast(this, "选取图片时出现错误");
                    }
                    break;
            }

        } else {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 上传图片
     *
     * @param avFile
     */
    private void uploadImage(final Context context, final AVFile avFile, final AVUser avUser) {
        final AlertDialog uploadImgDialog = UIUtil.getProgressDialog(context, "上传头像中...", false);
        uploadImgDialog.show();
        avFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                UIUtil.dismissProgressDialog(uploadImgDialog);
                if (e == null) {
                    UIUtil.showShortToast(context, "上传图片成功");
                    String sid = SharedPreferencesUtil.get(UserActivity.this, SharedPreferencesUtil.LOGIN_SID, "").toString();
                    new UserAction().logIn(sid, sid,
                            new LogInCallback() {
                                @Override
                                public void done(AVUser avUser, AVException e) {
                                    if(e != null) {
                                        Log.d("winson", "保存失败：" + e.getMessage());
                                    }
                                    else {
                                        avUser.put(TableContract.TableUser._PROFILE_URL, avFile);
                                        avUser.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                if(e != null) {
                                                    Log.d("winson", "保存失败：" + e.getMessage());
                                                }
                                                findUser(objectId, AVQuery.CachePolicy.NETWORK_ONLY);
                                                SicauHelperApplication.clearStudent();
                                            }
                                        });
                                    }
                                }
                            });
                } else {
                    new AlertDialog.Builder(context)
                            .setMessage("上传头像失败，要重试吗?")
                            .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    uploadImage(context, avFile, avUser);
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create().show();
                    Log.d("winson", "上传出错：" + e.getMessage());
                }
            }
        });
    }
}
