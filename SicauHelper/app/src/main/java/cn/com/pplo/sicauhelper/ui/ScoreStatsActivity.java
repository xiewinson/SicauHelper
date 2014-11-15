package cn.com.pplo.sicauhelper.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.listener.OnScrollHideOrShowActionBarListener;
import cn.com.pplo.sicauhelper.model.Score;
import cn.com.pplo.sicauhelper.model.ScoreStats;
import cn.com.pplo.sicauhelper.ui.adapter.ScoreStatsAdapter;
import cn.com.pplo.sicauhelper.util.StringUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;
import cn.com.pplo.sicauhelper.widget.ViewPadding;

public class ScoreStatsActivity extends BaseActivity {
    private ListView listView;
    private ScoreStatsAdapter statsAdapter;

    public static final String EXTRA_DATA = "extra_score_list";

    public static void startScoreStatsAcitivity(Context context, List<Score> data){
        Intent intent = new Intent(context, ScoreStatsActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_DATA, (ArrayList<? extends android.os.Parcelable>) data);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_stats);
        setUp(this);
    }

    private void setUp(Context context) {
        List<Score> data = getIntent().getParcelableArrayListExtra(EXTRA_DATA);
        UIUtil.setActionBarColor(context, getSupportActionBar(), R.color.teal_500);
        listView = (ListView) findViewById(R.id.score_static_listView);
        listView.setOnScrollListener(new OnScrollHideOrShowActionBarListener(getSupportActionBar()));
        listView.addHeaderView(ViewPadding.getActionBarPadding(context, R.color.grey_200));
        statsAdapter = new ScoreStatsAdapter(context, StringUtil.parseScoreStatsList(data));
        UIUtil.setListViewInitAnimation("bottom", listView, statsAdapter);
        UIUtil.setListViewScrollHideOrShowActionBar(context, listView, getSupportActionBar());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }
}
