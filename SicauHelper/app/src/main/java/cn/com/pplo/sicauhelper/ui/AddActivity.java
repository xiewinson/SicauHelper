package cn.com.pplo.sicauhelper.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.action.GoodsAction;
import cn.com.pplo.sicauhelper.action.StatusAction;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.util.ImageUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;

public class AddActivity extends BaseActivity implements AMapLocationListener {

    public static final String EXTRA_ADD_TYPE = "add_type";
    public static final String EXTRA_OBJECT_ID = "object_id";

    public static final int TYPE_GOODS = 888;
    public static final int TYPE_STATUS = 999;
    private int type;

    private AlertDialog progressDialog;
    private EditText titleEt;
    private EditText contentEt;
    private EditText priceEt;
    private CheckBox locationCb;
    private Spinner schoolSpinner;
    private Spinner categorySpinner;
    private LinearLayout imageLayout;
    private LocationManagerProxy mLocationManagerProxy;
    private String resultPath;
    private String objectId = "";

    private AVObject editObject;

    /**
     * 纬度
     */
    private double latitude;
    /**
     * 经度
     */
    private double longitude;
    /**
     * 地址
     */
    private String address;

    /**
     * 拍照后存储的位置
     */
    private String captureImagePath = "";
    /**
     * 用来存放图片地址
     */
    public List<AVFile> avImageList = new ArrayList<AVFile>();

    /**
     * 进入NewGoodsActivity页面
     *
     * @param context
     */
    public static void startAddActivity(Context context, int type) {
        Intent intent = new Intent(context, AddActivity.class);
        intent.putExtra(EXTRA_ADD_TYPE, type);
        context.startActivity(intent);
    }

    public static void startAddActivity(Context context, int type, String objectId) {
        Intent intent = new Intent(context, AddActivity.class);
        intent.putExtra(EXTRA_ADD_TYPE, type);
        intent.putExtra(EXTRA_OBJECT_ID, objectId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        type = getIntent().getIntExtra(EXTRA_ADD_TYPE, 0);
        objectId = getIntent().getStringExtra(EXTRA_OBJECT_ID);
        initLocation();

        setUp();
    }

    //初始化定位
    private void initLocation() {
        mLocationManagerProxy = LocationManagerProxy.getInstance(this);
        mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 60 * 1000, 100, this);
        mLocationManagerProxy.setGpsEnable(false);
    }

    //销毁定位
    private void stopLocation() {
        if (mLocationManagerProxy != null) {
            mLocationManagerProxy.removeUpdates(this);
            mLocationManagerProxy.destory();
        }
        mLocationManagerProxy = null;
    }

    private void setUp() {

        //裁剪后的图片地址
        resultPath = ImageUtil.getImageFolderPath(this) + File.separator + System.currentTimeMillis() + ".jpg";

        titleEt = (EditText) findViewById(R.id.title_et);
        contentEt = (EditText) findViewById(R.id.content_et);
        priceEt = (EditText) findViewById(R.id.price_et);
        schoolSpinner = (Spinner) findViewById(R.id.school_spinner);
        categorySpinner = (Spinner) findViewById(R.id.category_spinner);
        imageLayout = (LinearLayout) findViewById(R.id.image_layout);
        locationCb = (CheckBox) findViewById(R.id.location_cb);

        schoolSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.school)));
        schoolSpinner.setSelection(SicauHelperApplication.getStudent().getInt(TableContract.TableUser._SCHOOL));
        categorySpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.goods_category)));

        //商品
        if (type == TYPE_GOODS) {
            getSupportActionBar().setTitle("新商品");
            int school = schoolSpinner.getSelectedItemPosition();
            setActionBarColor(school);
            schoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (type == TYPE_GOODS) {
                        setActionBarColor(position);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            /**
             * 若有objectId, 则是更新goods
             */
            if (!TextUtils.isEmpty(objectId)) {
                getSupportActionBar().setTitle("修改商品");
                progressDialog = UIUtil.getProgressDialog(this, "加载中...", false);
                progressDialog.show();
                new GoodsAction().findByObjectId(AVQuery.CachePolicy.CACHE_ELSE_NETWORK, objectId, new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        UIUtil.dismissProgressDialog(progressDialog);
                        if (e == null && list.size() > 0) {
                            editObject = list.get(0);
                            //类别
                            categorySpinner.setSelection(editObject.getInt(TableContract.TableGoods._CATEGORY));
                            //校区
                            schoolSpinner.setSelection(editObject.getInt(TableContract.TableGoods._SCHOOL));
                            //标题
                            titleEt.setText(editObject.getString(TableContract.TableGoods._TITLE));
                            //内容
                            contentEt.setText(editObject.getString(TableContract.TableGoods._CONTENT));
                            //价格
                            priceEt.setText((int)editObject.getDouble(TableContract.TableGoods._PRICE) + "");

                            avImageList = ImageUtil.getAVFileListByAVObject(editObject);
                            for (AVFile avFile : avImageList) {
                                showImageByAVFile(AddActivity.this, avFile);
                            }

                            if(TextUtils.isEmpty(editObject.getString(TableContract.TableGoods._ADDRESS))) {
                                locationCb.setChecked(false);
                            }
                            else {
                                locationCb.setChecked(true);
                            }

                        } else {
                            UIUtil.showShortToast(AddActivity.this, "获取商品信息失败");
                            AddActivity.this.finish();
                            if (e != null){
                                Log.d("winson", "出错：" + e.getMessage());
                            }
                        }
                    }
                });
            }

        }
        //若为新帖子则隐藏校区选项
        else if (type == TYPE_STATUS) {
            getSupportActionBar().setTitle("新帖子");
            UIUtil.setActionBarColor(AddActivity.this, getSupportActionBar(), R.color.color_primary);
            schoolSpinner.setVisibility(View.GONE);
            findViewById(R.id.add_school_layout).setVisibility(View.GONE);
            findViewById(R.id.add_price_layout).setVisibility(View.GONE);
            priceEt.setVisibility(View.GONE);

            /**
             * 若有objectId, 则是更新status
             */
            if (!TextUtils.isEmpty(objectId)) {
                getSupportActionBar().setTitle("修改帖子");
                progressDialog = UIUtil.getProgressDialog(this, "加载中...", false);
                progressDialog.show();
                new StatusAction().findByObjectId(AVQuery.CachePolicy.CACHE_ELSE_NETWORK, objectId, new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        UIUtil.dismissProgressDialog(progressDialog);
                        if (e == null && list.size() > 0) {
                            editObject = list.get(0);

                            //标题
                            titleEt.setText(editObject.getString(TableContract.TableStatus._TITLE));
                            //内容
                            contentEt.setText(editObject.getString(TableContract.TableStatus._CONTENT));

                            avImageList = ImageUtil.getAVFileListByAVObject(editObject);
                            for (AVFile avFile : avImageList) {
                                showImageByAVFile(AddActivity.this, avFile);
                            }

                            if(TextUtils.isEmpty(editObject.getString(TableContract.TableGoods._ADDRESS))) {
                                locationCb.setChecked(false);
                            }
                            else {
                                locationCb.setChecked(true);
                            }
                        } else {
                            UIUtil.showShortToast(AddActivity.this, "获取商品信息失败");
                            AddActivity.this.finish();
                            if (e != null){
                                Log.d("winson", "出错：" + e.getMessage());
                            }
                        }
                    }
                });
            }
        }

    }

    /**
     * 设置颜色
     *
     * @param school
     */
    private void setActionBarColor(int school) {
        int color = R.color.color_primary;
//        if (school == 0) {
//            color = R.color.red_500;
//        } else if (school == 1) {
//            color = R.color.orange_500;
//        } else {
//            color = R.color.green_500;
//        }
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(color));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar   if it is present.
        getMenuInflater().inflate(R.menu.new_goods, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_image) {
            if (avImageList.size() < 4) {
                getImageSelectDialog(AddActivity.this).show();
            } else {
                UIUtil.showShortToast(AddActivity.this, "最多只能添加四张图片");
            }
            return true;
        } else if (id == R.id.action_send) {
            stopLocation();
            /**
             * 上传最终结果
             */
            //隐藏软键盘
            UIUtil.hideSoftKeyboard(this, titleEt);
            if (type == TYPE_GOODS) {
                uploadGoods(AddActivity.this, avImageList);
            } else if (type == TYPE_STATUS) {
                uploadStatus(AddActivity.this, avImageList);
            }

        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 上传数据
     *
     * @param context
     */

    private void uploadGoods(Context context, final List<AVFile> avFiles) {
        final AlertDialog uploadDialog = UIUtil.getProgressDialog(context, "上传中...", false);
        uploadDialog.show();
        if (TextUtils.isEmpty(priceEt.getText().toString().trim())) {
            UIUtil.showShortToast(context, "价格不可为空");
            return;
        } else if (TextUtils.isEmpty(titleEt.getText().toString().trim())) {
            UIUtil.showShortToast(context, "标题不可为空");
            return;
        } else if (TextUtils.isEmpty(contentEt.getText().toString().trim())) {
            UIUtil.showShortToast(context, "内容不可为空");
            return;
        }
        AVObject avObject = editObject;
        if(avObject == null) {
            avObject = new AVObject(TableContract.TableGoods.TABLE_NAME);
        }

        //类别
        avObject.put(TableContract.TableGoods._CATEGORY, categorySpinner.getSelectedItemPosition());
        //校区
        avObject.put(TableContract.TableGoods._SCHOOL, schoolSpinner.getSelectedItemPosition());
        //标题
        avObject.put(TableContract.TableGoods._TITLE, titleEt.getText().toString().trim());
        //内容
        avObject.put(TableContract.TableGoods._CONTENT, contentEt.getText().toString().trim());
        //价格
        avObject.put(TableContract.TableGoods._PRICE, Float.parseFloat(priceEt.getText().toString().trim()));
        //发布人(学生)
        avObject.put(TableContract.TableGoods._USER, SicauHelperApplication.getStudent());
        //手机型号
        avObject.put(TableContract.TableGoods._MODEL, Build.MODEL);
        //手机品牌
        avObject.put(TableContract.TableGoods._BRAND, Build.BRAND);
        //手机系统版本
        avObject.put(TableContract.TableGoods._VERSION, Build.VERSION.RELEASE);
        //经纬度
        avObject.put("location", new AVGeoPoint(latitude, longitude));
        if(locationCb.isChecked() == false) {
            Log.d("winson", "不显示地址");
            //详细地址
            avObject.put(TableContract.TableGoods._ADDRESS, "");
        }
        else {
            Log.d("winson", "显示地址");
            avObject.put(TableContract.TableGoods._ADDRESS, address);
        }



//        上传图片
        for(int i = 0; i < 4; i++) {
            avObject.put("image" + i, null);
        }
        for (int i = 0; i < avFiles.size(); i++) {
            avObject.put("image" + i, avFiles.get(i));
        }

        avObject.setFetchWhenSave(true);
        avObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                UIUtil.dismissProgressDialog(uploadDialog);
                if (e == null) {
                    UIUtil.showShortToast(AddActivity.this, "哇，成功了");
                    AddActivity.this.finish();
                    Log.d("winson", "上传对象成功");
                } else {
                    UIUtil.showShortToast(AddActivity.this, "唉，失败了，再试试");
                    Log.d("winson", "上传对象失败：" + e.getMessage());
                }
            }
        });
    }

    /**
     * 上传帖子
     *
     * @param context
     * @param avFiles
     */
    private void uploadStatus(Context context, List<AVFile> avFiles) {
        final AlertDialog uploadDialog = UIUtil.getProgressDialog(context, "上传中...", false);
        uploadDialog.show();
        if (TextUtils.isEmpty(titleEt.getText().toString().trim())) {
            UIUtil.showShortToast(context, "标题不可为空");
            uploadDialog.show();
            return;
        } else if (TextUtils.isEmpty(contentEt.getText().toString().trim())) {
            UIUtil.showShortToast(context, "内容不可为空");
            uploadDialog.show();
            return;
        }

        AVObject avObject = editObject;
        if(avObject == null) {
            avObject = new AVObject(TableContract.TableStatus.TABLE_NAME);
        }
        //标题
        avObject.put(TableContract.TableStatus._TITLE, titleEt.getText().toString().trim());
        //内容
        avObject.put(TableContract.TableStatus._CONTENT, contentEt.getText().toString().trim());
        //发布人(学生)
        avObject.put(TableContract.TableStatus._USER, SicauHelperApplication.getStudent());
        //手机型号
        avObject.put(TableContract.TableStatus._MODEL, Build.MODEL);
        //手机品牌
        avObject.put(TableContract.TableStatus._BRAND, Build.BRAND);
        //手机系统版本
        avObject.put(TableContract.TableStatus._VERSION, Build.VERSION.RELEASE);
        //经纬度
        avObject.put("location", new AVGeoPoint(latitude, longitude));
        if(locationCb.isChecked() == false) {
            Log.d("winson", "不显示地址");
            //详细地址
            avObject.put(TableContract.TableGoods._ADDRESS, "");
        }
        else {
            Log.d("winson", "显示地址");
            avObject.put(TableContract.TableGoods._ADDRESS, address);
        }


//        上传图片
        for(int i = 0; i < 4; i++) {
            avObject.put("image" + i, null);
        }
        for (int i = 0; i < avFiles.size(); i++) {
            avObject.put("image" + i, avFiles.get(i));
        }
        avObject.setFetchWhenSave(true);
        avObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                UIUtil.dismissProgressDialog(uploadDialog);
                if (e == null) {
                    UIUtil.showShortToast(AddActivity.this, "哇，成功了");
                    AddActivity.this.finish();
                    Log.d("winson", "上传对象成功");
                } else {
                    UIUtil.showShortToast(AddActivity.this, "唉，失败了，再试试");
                    Log.d("winson", "上传对象失败：" + e.getMessage());
                }
            }
        });
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
                                            + "_goods_" + System.currentTimeMillis() + ".jpg", resultPath);

                            if (avFile != null) {
                                uploadImage(this, avFile);
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
    private void uploadImage(final Context context, final AVFile avFile) {
        final AlertDialog uploadImgDialog = UIUtil.getProgressDialog(AddActivity.this, "上传图片中...", false);
        uploadImgDialog.show();
        avFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                UIUtil.dismissProgressDialog(uploadImgDialog);
                if (e == null) {
                    UIUtil.showShortToast(AddActivity.this, "上传图片成功");
                    avImageList.add(avFile);
                    showImage(AddActivity.this, resultPath, avFile);
                    Log.d("winson", "当前的图片列表个数为" + avImageList.size() + "   " + avImageList);
                } else {
                    new AlertDialog.Builder(context)
                            .setMessage("上传图片失败，要重试吗?")
                            .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    uploadImage(context, avFile);
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

    /**
     * 将图片显示出来并暂存在list中
     *
     * @param context
     * @param path
     * @param avFile
     */
    public void showImage(final Context context, final String path, final AVFile avFile) {
        final View view = View.inflate(context, R.layout.item_goods_add_image, null);
        ImageView goodsIv = (ImageView) view.findViewById(R.id.goods_iv);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        goodsIv.setImageBitmap(bitmap);

        Button deleteBtn = (Button) view.findViewById(R.id.goods_delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setMessage("确定删除这张图片？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                imageLayout.removeView(view);
                                avImageList.remove(avFile);
                                Log.d("winson", "当前的图片列表个数为" + avImageList.size() + "   " + avImageList);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();

            }
        });
        imageLayout.addView(view);
    }

    /**
     * 显示从avFile中所取得的图片
     *
     * @param context
     * @param avFile
     */
    public void showImageByAVFile(final Context context, final AVFile avFile) {
        final View view = View.inflate(context, R.layout.item_goods_add_image, null);
        ImageView goodsIv = (ImageView) view.findViewById(R.id.goods_iv);
        int width = (int) UIUtil.parseDpToPx(context, 200);
        ImageLoader.getInstance().displayImage(avFile.getThumbnailUrl(true, width, width), goodsIv, ImageUtil.getDisplayProfileOption(context));

        Button deleteBtn = (Button) view.findViewById(R.id.goods_delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setMessage("确定删除这张图片？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                imageLayout.removeView(view);
                                avImageList.remove(avFile);
                                Log.d("winson", "当前的图片列表个数为" + avImageList.size() + "   " + avImageList);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();

            }
        });
        imageLayout.addView(view);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        //获取位置信息
        if (aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
            longitude = aMapLocation.getLongitude();
            latitude = aMapLocation.getLatitude();
            address = aMapLocation.getAddress();
            Log.d("winson", longitude + "  " + latitude + "  " + address);
        } else {
            Log.d("winson", aMapLocation.getAMapException().getMessage());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
//        longitude = location.getLongitude();
//        latitude = location.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
