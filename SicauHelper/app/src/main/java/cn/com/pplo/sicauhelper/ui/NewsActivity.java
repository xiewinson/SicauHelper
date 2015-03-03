package cn.com.pplo.sicauhelper.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.listener.OnScrollHideOrShowActionBarListener2;
import cn.com.pplo.sicauhelper.model.News;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.ui.fragment.ProgressFragment;
import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;

public class NewsActivity extends BaseActivity {

    private final static String EXTRA_NEWS = "extra_news";
    private News data;

    private AlertDialog progressDialog;
    private TextView newsTv;
    private WebView newsWebView;
    private ScrollView scrollView;

    public static void startNewsActivity(Context context, News news) {
        Intent intent = new Intent(context, NewsActivity.class);
        intent.putExtra(EXTRA_NEWS, news);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        setUp(NewsActivity.this);
    }

    private void setUp(final Context context) {

        newsTv = (TextView) findViewById(R.id.news_tv);
        newsWebView = (WebView) findViewById(R.id.news_webView);
        scrollView = (ScrollView) findViewById(R.id.news_scrollView);

        //设置webView可缩放
        WebSettings webSettings = newsWebView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true);
        newsWebView.setInitialScale(10);

        //对话框
        progressDialog = UIUtil.getProgressDialog(context, "正在寻找新闻内容...", true);
        progressDialog.show();
        //获得news数据
        data = getIntent().getParcelableExtra(EXTRA_NEWS);
        if (data != null) {

            //设置actionBar的标题
            ActionBar actionBar = getSupportActionBar();

            String category = data.getCategory();
            int colorRes = 0;
            if (category.equals("雅安")) {
                colorRes = R.drawable.square_blue;
            } else if (category.equals("成都")) {
                colorRes = R.drawable.square_orange;
            } else if (category.equals("都江堰")) {
                colorRes = R.drawable.square_green;
            } else {
                colorRes = R.drawable.square_red;
            }
            actionBar.setTitle(category + "新闻");
            actionBar.setSubtitle(data.getDate());
            actionBar.setBackgroundDrawable(getResources().getDrawable(colorRes));


            //从数据库中取得新闻
            new AsyncTask<Integer, Integer, News>() {
                @Override
                protected News doInBackground(Integer... params) {
                    News news = new News();
                    Cursor cursor = getContentResolver().query(Uri.parse(SicauHelperProvider.URI_NEWS_SINGLE),
                            null,
                            TableContract.TableNews._ID + " = ?",
                            new String[]{params[0] + ""},
                            null);
                    try {
                        if (cursor.getCount() > 0) {
                            while (cursor.moveToNext()) {
                                news.setContent(cursor.getString(cursor.getColumnIndex(TableContract.TableNews._CONTENT)));
                                news.setSrc(cursor.getString(cursor.getColumnIndex(TableContract.TableNews._SRC)));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        news = null;
                    } finally {
                        cursor.close();
                    }
                    return news;
                }

                @Override
                protected void onPostExecute(News news) {
                    super.onPostExecute(news);
                    if (news != null && news.getContent() != null && !news.getContent().equals("")) {
                        showData(news);
                    } else {
                        requestNewsContent(context, data.getId());
                    }
                }
            }.execute(data.getId());

        }
    }

    /**
     * 将数据显示出来
     *
     * @param news
     */
    private void showData(News news) {
        newsTv.setText(news.getContent());
        UIUtil.dismissProgressDialog(progressDialog);
        loadWebView(newsWebView, news.getSrc());
    }

    /**
     * 请求新闻内容
     *
     * @param context
     * @param id
     */
    private void requestNewsContent(Context context, int id) {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("bianhao", id + "");
        new NetUtil().getNewsHtmlStr(context, requestQueue, params, new NetUtil.NetCallback(context) {
            @Override
            protected void onSuccess(final String result) {
                data.setContent(StringUtil.parseNewsInfo(result));

                StringUtil.handleNewsHtmlStr(result, new StringUtil.Callback() {
                    @Override
                    public void handleParseResult(Object obj) {
                        data.setSrc(obj.toString());
                        //加载webView
                        loadWebView(newsWebView, obj.toString());
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                //更新数据库中的新闻内容
                                ContentValues values = new ContentValues();
                                values.put(TableContract.TableNews._ID, data.getId());
                                values.put(TableContract.TableNews._CATEGORY, data.getCategory());
                                values.put(TableContract.TableNews._DATE, data.getDate());
                                values.put(TableContract.TableNews._TITLE, data.getTitle());
                                values.put(TableContract.TableNews._CONTENT, data.getContent());
                                values.put(TableContract.TableNews._URL, data.getUrl());
                                values.put(TableContract.TableNews._SRC, data.getSrc());
                                getContentResolver().update(Uri.parse(SicauHelperProvider.URI_NEWS_ALL + "/" + data.getId()),
                                        values,
                                        TableContract.TableNews._ID + " = ?",
                                        new String[]{values.getAsString(TableContract.TableNews._ID)});
                            }
                        }.start();
                    }
                });


                //显示新闻内容
                showData(data);
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                UIUtil.dismissProgressDialog(progressDialog);
                super.onErrorResponse(volleyError);
            }
        });
    }

    private void loadWebView(final WebView newsWebView, String htmlStr) {
        newsWebView.loadData(htmlStr, "text/html; charset=utf-8", "utf-8");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.news, menu);
        if (newsTv.getVisibility() == View.VISIBLE) {
            menu.add(1, 1, 1, "网页视图");
        } else {
            menu.add(1, 1, 1, "内容视图");
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == 1) {
            if (newsTv.getVisibility() == View.VISIBLE) {
                newsTv.setVisibility(View.GONE);
                newsWebView.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
            } else {
                newsTv.setVisibility(View.VISIBLE);
                newsWebView.setVisibility(View.GONE);
                invalidateOptionsMenu();
            }
        } else if (item.getItemId() == R.id.action_refresh) {
            requestNewsContent(NewsActivity.this, data.getId());
        }
        return super.onOptionsItemSelected(item);
    }

}
