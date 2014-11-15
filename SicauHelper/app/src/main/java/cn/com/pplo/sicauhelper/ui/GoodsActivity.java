package cn.com.pplo.sicauhelper.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.avos.avoscloud.AVObject;

import cn.com.pplo.sicauhelper.R;

public class GoodsActivity extends BaseActivity {

    private static final String EXTRA_OBJECT_ID = "object_id";

    private String objectId;
    private AVObject avGoods;

    public static void startGoodsActivity(Context context, String objectId) {
        Intent intent = new Intent(context, GoodsActivity.class);
        intent.putExtra(EXTRA_OBJECT_ID, objectId);
        context.startActivity(intent);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods);
        getSupportActionBar().setTitle("");
        setUp();
    }

    private void setUp() {
        objectId = getIntent().getStringExtra(EXTRA_OBJECT_ID);
//        avGoods = AVObject.createWithoutData("Test")
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_goods, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
