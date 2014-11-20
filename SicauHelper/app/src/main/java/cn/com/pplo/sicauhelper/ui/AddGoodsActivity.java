package cn.com.pplo.sicauhelper.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.avos.avoscloud.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.util.ImageUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;

public class AddGoodsActivity extends BaseActivity implements AMapLocationListener {

    private EditText titleEt;
    private EditText contentEt;
    private EditText priceEt;
    private Spinner schoolSpinner;
    private Spinner categorySpinner;
    private LinearLayout imageLayout;
    private LocationManagerProxy mLocationManagerProxy;
    private String resultPath;


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
    public static void startNewGoodsActivity(Context context) {
        Intent intent = new Intent(context, AddGoodsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_goods);

        initLocation();
        getSupportActionBar().setTitle("新增商品");
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
        schoolSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.school)));
        schoolSpinner.setSelection(SicauHelperApplication.getStudent().getInt(TableContract.TableStudent._SCHOOL));
        categorySpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.goods_category)));
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
                getImageSelectDialog(AddGoodsActivity.this).show();
            } else {
                UIUtil.showShortToast(AddGoodsActivity.this, "最多只能添加四张图片");
            }
            return true;
        } else if (id == R.id.action_send) {
            stopLocation();
            /**
             * 上传最终结果
             */
            upload(AddGoodsActivity.this, avImageList);
        }
//                for(int i = 0; i < 100; i++) {
//
//                    //类别
//                    AVObject avObject = new AVObject(TableContract.TableGoods.TABLE_NAME);
//                    //上传图片
//                    AVFile file = new AVFile(System.currentTimeMillis() + ".jpg", "http://jiaowu.sicau.edu.cn/photo/2011862" + new Random().nextInt(10) +".jpg", null);
//                    avObject.put("image" + 0, file);
//                    avObject.put("category", new Random().nextInt(9));
//                    //校区
//                    avObject.put("school", new Random().nextInt(3));
//                    //标题
//                    avObject.put("title", new Random().nextFloat() + "啊");
//                    //内容
//                    avObject.put("content", new Random().nextFloat() + "恩");
//                    //价格
//                    avObject.put("price", new Random().nextInt(1001));
//                    //发布人(学生)
//                    avObject.put("user", SicauHelperApplication.getStudent());
//                    //手机型号
//                    avObject.put("model", Build.MODEL);
//                    //手机品牌
//                    avObject.put("brand", Build.BRAND);
//                    //手机系统版本
//                    avObject.put("version", Build.VERSION.RELEASE);
//                    //经纬度
//                    avObject.put("location", new AVGeoPoint(latitude, longitude));
//                    //详细地址
//                    avObject.put("address", address);
//
//                    avObject.saveInBackground(new SaveCallback() {
//                        @Override
//                        public void done(AVException e) {
//                            if (e == null) {
//                                Log.d("winson", "上传对象成功");
//                            } else {
//                                Log.d("winson", "上传对象失败：" + e.getMessage());
//                            }
//                            try {
//                                Thread.sleep(100);
//                            } catch (InterruptedException e1) {
//                                e1.printStackTrace();
//                            }
//                        }
//                    });
//                }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 上传数据
     *
     * @param context
     */

    private void upload(Context context, final List<AVFile> avFiles) {
        AVObject avObject = new AVObject(TableContract.TableGoods.TABLE_NAME);

        //类别
        avObject.put("category", categorySpinner.getSelectedItemPosition());
        //校区
        avObject.put("school", schoolSpinner.getSelectedItemPosition());
        //标题
        avObject.put("title", titleEt.getText().toString().trim());
        //内容
        avObject.put("content", contentEt.getText().toString().trim());
        //价格
        avObject.put("price", Float.parseFloat(priceEt.getText().toString().trim()));
        //发布人(学生)
        avObject.put("user", SicauHelperApplication.getStudent());
        //手机型号
        avObject.put("model", Build.MODEL);
        //手机品牌
        avObject.put("brand", Build.BRAND);
        //手机系统版本
        avObject.put("version", Build.VERSION.RELEASE);
        //经纬度
        avObject.put("location", new AVGeoPoint(latitude, longitude));
        //详细地址
        avObject.put("address", address);

//                        添加图片到列表
//        avObject.addAll("pictures", avImageList);
//        上传图片
        for (int i = 0; i < avFiles.size(); i++) {
            avObject.put("image" + i, avFiles.get(i));
        }
        avObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Log.d("winson", "上传对象成功");
                } else {
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
                                    SicauHelperApplication.getStudent().getString(TableContract.TableStudent._SID)
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
        final AlertDialog uploadImgDialog = UIUtil.getProgressDialog(AddGoodsActivity.this, "上传图片中...");
        uploadImgDialog.show();
        avFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                UIUtil.dismissProgressDialog(uploadImgDialog);
                if (e == null) {
                    UIUtil.showShortToast(AddGoodsActivity.this, "上传图片成功");
                    avImageList.add(avFile);
                    showImage(AddGoodsActivity.this, resultPath);
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
     */
    public void showImage(final Context context, final String path) {
        final View view = View.inflate(context, R.layout.item_goods_add_image, null);
        ImageView goodsIv = (ImageView) view.findViewById(R.id.goods_iv);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        File file = new File(path);
        Log.d("winson", "大小：" + file.length());
        Log.d("winson", "大小：" + bitmap.getByteCount() + "   分辨率：" + bitmap.getWidth() + " *" + bitmap.getHeight());
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
                                avImageList.remove(path);
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
