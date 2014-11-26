package cn.com.pplo.sicauhelper.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.action.CommentAction;
import cn.com.pplo.sicauhelper.action.UserAction;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.util.ColorUtil;
import cn.com.pplo.sicauhelper.util.ImageUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends BaseActivity {

    public static final String EXTRA_OBJECT_ID = "object_id";

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

        findUser(objectId, AVQuery.CachePolicy.CACHE_THEN_NETWORK);
    }

    /**
     * 获取user
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
                        UIUtil.showShortToast(UserActivity.this, "你的网络好像有点问题，下拉刷新试试吧");
                    }
                }
            }
        });
    }

    private void initView(List<AVUser> list) {
        avUser = list.get(0);

        primaryColor = ColorUtil.getColorBySchool(this, avUser.getInt(TableContract.TableUser._SCHOOL));

        OnCommentBtnClickListener onCommentBtnClickListener = new OnCommentBtnClickListener(this);

        nameTv.setText(avUser.getString(TableContract.TableUser._NICKNAME));
        int school = avUser.getInt(TableContract.TableUser._SCHOOL);
        backgroundView.setBackgroundResource(primaryColor);
        schoolTv.setText(getResources().getStringArray(R.array.school)[school] + "校区");
        gradeTv.setText(avUser.getString(TableContract.TableUser._SID).substring(0, 4) + " 级");
        ImageLoader.getInstance().displayImage(avUser.getAVFile(TableContract.TableUser._PROFILE_URL).getUrl(), headIv, ImageUtil.getDisplayImageOption(this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
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

    private class OnCommentBtnClickListener implements View.OnClickListener {
        private Context context;

        private OnCommentBtnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            //点击商品数量
            if(id == R.id.user_goods_tv){

            }
            //点击评论数量
            else if(id == R.id.user_status_tv){

            }
        }
    }
}
