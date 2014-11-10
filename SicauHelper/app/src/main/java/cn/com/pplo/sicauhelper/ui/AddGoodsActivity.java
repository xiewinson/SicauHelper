package cn.com.pplo.sicauhelper.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.util.ImageUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;

public class AddGoodsActivity extends BaseActivity {

    private EditText titleEt;
    private EditText contentEt;
    private EditText priceEt;
    private Spinner schoolSpinner;
    private Spinner categorySpinner;
    private LinearLayout imageLayout;

    public static final int REQUEST_CODE_PICK_IMAGE = 1991;
    public static final int REQUEST_CODE_CAPTURE_IMAGE = 1992;

    /**
     * 拍照后存储的位置
     */
    private String imagePath = "";
    /**
     * 用来存放图片地址
     */
    public List<String> imageList = new ArrayList<String>();

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
        getSupportActionBar().setTitle("新增商品");
        setUp();
    }

    private void setUp() {
        titleEt = (EditText) findViewById(R.id.title_et);
        contentEt = (EditText) findViewById(R.id.content_et);
        priceEt = (EditText) findViewById(R.id.price_et);
        schoolSpinner = (Spinner) findViewById(R.id.school_spinner);
        categorySpinner = (Spinner) findViewById(R.id.category_spinner);
        imageLayout = (LinearLayout) findViewById(R.id.image_layout);
        schoolSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.school)));
        schoolSpinner.setSelection(SicauHelperApplication.getStudent(this).getSchool());
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
            if(imageList.size() < 4) {
                getImageSelectDialog(AddGoodsActivity.this).show();
            }
            else {
                UIUtil.showShortToast(AddGoodsActivity.this, "最多只能添加四张图片");
            }
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
                    imagePath = ImageUtil.getImageFolderPath(context) + File.separator + System.currentTimeMillis() + ".jpg";
                    File file = new File(imagePath);
//                    if(!file.exists()) {
//                        try {
//                            file.createNewFile();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE);
                }
                //选择图片
                else if (which == 1) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
                }
            }
        });
        alertDialog = builder.create();
        return alertDialog;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String path = "";
            //若是从图库选择图
            if (requestCode == REQUEST_CODE_PICK_IMAGE) {
                path = ImageUtil.imageUriToPath(this, data.getData());
            }
            //调用相机
            else if (requestCode == REQUEST_CODE_CAPTURE_IMAGE) {
                path = imagePath;
            }

            //添加图片到list并且显示出来
            if (!TextUtils.isEmpty(path)) {
                imageList.add(path);
                showImage(AddGoodsActivity.this, path);
                Log.d("winson", "当前的图片列表个数为" + imageList.size() + "   " + imageList);
            } else {
                UIUtil.showShortToast(this, "选取图片时出现错误");
            }
        } else {
            UIUtil.showShortToast(this, "选取图片出现错误");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 将图片显示出来并暂存在list中
     * @param context
     * @param path
     */
    public void showImage(final Context context, final String path) {
        final View view = View.inflate(context, R.layout.item_goods_add_image, null);
        ImageView goodsIv = (ImageView) view.findViewById(R.id.goods_iv);
        goodsIv.setImageBitmap(BitmapFactory.decodeFile(path));

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
                                imageList.remove(path);
                                Log.d("winson", "当前的图片列表个数为" + imageList.size() + "   " + imageList);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();

            }
        });
        imageLayout.addView(view);
    }
}
