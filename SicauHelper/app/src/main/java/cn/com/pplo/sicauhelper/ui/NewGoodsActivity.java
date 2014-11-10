package cn.com.pplo.sicauhelper.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;

public class NewGoodsActivity extends BaseActivity {

    private EditText titleEt;
    private EditText contentEt;
    private EditText priceEt;
    private Spinner schoolSpinner;
    private Spinner categorySpinner;

    public static final int REQUEST_CODE_PICK_IMAGE = 1991;
    public static final int REQUEST_CODE_CAPTURE_IMAGE = 1992;
    /**
     * 进入NewGoodsActivity页面
     *
     * @param context
     */
    public static void startNewGoodsActivity(Context context) {
        Intent intent = new Intent(context, NewGoodsActivity.class);
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
            getImageSelectDialog(NewGoodsActivity.this).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 取得图片选择Dialog
     * @param context
     * @return
     */
    private AlertDialog getImageSelectDialog(Context context) {
        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.image_select, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //立刻拍照
                if(which == 0) {

                }
                //选择图片
                else if(which == 1) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
                }
            }
        });
        alertDialog = builder.create();
        return alertDialog;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //若是从图库选择图
        if(requestCode == REQUEST_CODE_PICK_IMAGE) {
            data.getExtras().
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
