package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.model.News;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.service.SaveIntentService;
import cn.com.pplo.sicauhelper.ui.MainActivity;
import cn.com.pplo.sicauhelper.ui.NewsActivity;
import cn.com.pplo.sicauhelper.util.CursorUtil;
import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;

public class NewsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView listView;
    private ProgressDialog progressDialog;
    private List<News> newsList = new ArrayList<News>();
    private SearchView searchView;
    private NewsAdapter newsAdapter;

    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached("新闻");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UIUtil.getSupportActionBar(getActivity()).setBackgroundDrawable(getResources().getDrawable(R.color.deep_purple_500));
        setUp(view);
    }

    private void setUp(View view) {
        listView = (ListView) view.findViewById(R.id.news_listView);
//        listView.setTextFilterEnabled(true);
        progressDialog = UIUtil.getProgressDialog(getActivity(), "新闻呢，是我从教务系统搬过来的～");
        //listView上下补点间距
//        TextView paddingTv = ListViewPadding.getListViewPadding(getActivity());
//        listView.addHeaderView(paddingTv);
//        listView.addFooterView(paddingTv);

        newsAdapter = new NewsAdapter(getActivity(), newsList);
        listView.setAdapter(newsAdapter);
        //滚动隐藏
        setScrollHideOrShowActionBar(listView);
        //启动Loader
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //创建菜单
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.news_list, menu);
        initSearchView(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //初始化searchView
    private void initSearchView(Menu menu) {
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("搜索");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("winson", "点击了" + item.getItemId());
        //刷新
        if(item.getItemId() == R.id.action_refresh){
            requestNewsList(getActivity());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), Uri.parse(SicauHelperProvider.URI_NEWS_ALL), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if(cursor != null && cursor.getCount() > 0){
            notifyDataSetChanged(CursorUtil.parseNewsList(cursor));
        }
        else {
//            Intent intent = new Intent(getActivity(), NewsService.class);
//            getActivity().bindService(intent, serviceConn, Context.BIND_AUTO_CREATE);
            requestNewsList(getActivity());
        }
    }

    /**
     * 从网络请求数据
     * @param context
     */
    public void requestNewsList(final Context context) {
        progressDialog.show();
        NetUtil.getNewsListHtmlStr(context, new NetUtil.NetCallback(context) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("winson", "发生了错误");
                UIUtil.dismissProgressDialog(progressDialog);
                super.onErrorResponse(volleyError);
            }

            @Override
            public void onSuccess(String result) {
                final List<News> tempList = StringUtil.parseNewsListInfo(result);
                notifyDataSetChanged(tempList);
                UIUtil.dismissProgressDialog(progressDialog);
                SaveIntentService.startActionNewsAll(context, tempList);
            }
        });
    }

    /**
     * 通知ListView数据改变
     * @param list
     */
    private void notifyDataSetChanged(List<News> list){
        if(list != null && list.size() > 0){
            newsList.clear();
            newsList.addAll(list);
            newsAdapter.notifyDataSetChanged();
        }
    }




    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private class NewsAdapter extends BaseAdapter implements Filterable {

        private Context context;
        private List<News> data;

        public NewsAdapter(Context context, List<News> list){
            this.context = context;
            this.data = list;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public News getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = View.inflate(context, R.layout.item_fragment_news_list, null);
                holder.titleTv = (TextView) convertView.findViewById(R.id.title_tv);
                holder.dateTv = (TextView) convertView.findViewById(R.id.date_tv);
                holder.categoryTv = (TextView) convertView.findViewById(R.id.category_tv);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            News news = getItem(position);
            holder.titleTv.setText(news.getTitle());
            holder.dateTv.setText(news.getDate());
            String category = news.getCategory();
            int shapeRes = 0;
            if(category.equals("雅安")){
                category = "雅";
                shapeRes = R.drawable.square_blue;
            }
            else if(category.equals("成都")){
                category = "成";
                shapeRes = R.drawable.square_orange;
            }
            else if(category.equals("都江堰")){
                category = "堰";
                shapeRes = R.drawable.square_green;
            }
            else {
                category = "全";
                shapeRes = R.drawable.square_red;
            }
            holder.categoryTv.setText(category);
            holder.categoryTv.setBackgroundResource(shapeRes);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewsActivity.startNewsActivity(context, getItem(position));
                }
            });
            return convertView;
        }

        @Override
        public android.widget.Filter getFilter() {
            return new android.widget.Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    return null;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                }
            };
        }
    }

    private static class ViewHolder {
        TextView titleTv;
        TextView dateTv;
        TextView categoryTv;
    }
}
