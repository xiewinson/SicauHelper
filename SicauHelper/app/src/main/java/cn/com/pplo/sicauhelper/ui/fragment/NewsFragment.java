package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.model.News;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.service.NewsService;
import cn.com.pplo.sicauhelper.ui.MainActivity;
import cn.com.pplo.sicauhelper.util.CursorUtil;
import cn.com.pplo.sicauhelper.widget.ListViewPadding;


public class NewsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView listView;
    private List<News> newsList = new ArrayList<News>();
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
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.deep_purple_500));
        setUp(view);
    }

    private void setUp(View view) {
        listView = (ListView) view.findViewById(R.id.news_listView);

        //listView上下补点间距
        TextView paddingTv = ListViewPadding.getListViewPadding(getActivity());
        listView.addHeaderView(paddingTv);
        listView.addFooterView(paddingTv);

        newsAdapter = new NewsAdapter(getActivity());
        //滚动隐藏
        setScrollHideOrShowActionBar(listView);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getLoaderManager().destroyLoader(0);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), Uri.parse(SicauHelperProvider.URI_NEWS_ALL), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if(cursor != null && cursor.getCount() > 0){
            newsAdapter.setData(CursorUtil.parseNewsList(cursor));
            listView.setAdapter(newsAdapter);
        }
        else {
            Log.d("winson", "去开始服务");
            Intent intent = new Intent(getActivity(), NewsService.class);
            getActivity().bindService(intent, serviceConn, Context.BIND_AUTO_CREATE);
        }
    }

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            NewsService.NewsServiceBinder mBinder = (NewsService.NewsServiceBinder) service;
            NewsService newsService = mBinder.getNewsService();
            Log.d("winson", "去开始轻轻");

            newsService.requestNewsList(new NewsService.NewsCallback() {
                @Override
                public void onSuccess(List<News> data) {
                }

                @Override
                public void onFailure() {

                }

                @Override
                public void onSaveFinish(boolean isSaveSuccess) {
                    getActivity().unbindService(serviceConn);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private class NewsAdapter extends BaseAdapter {

        private Context context;

        public NewsAdapter(Context context){
            this.context = context;
        }

        private List<News> data = new ArrayList<News>();

        public void setData(List<News> list){
            if(list != null && list.size() > 0){
                data.clear();
                data.addAll(list);
            }
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
        public View getView(int position, View convertView, ViewGroup parent) {
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
                shapeRes = R.drawable.square_purple;
            }
            holder.categoryTv.setText(category);
            holder.categoryTv.setBackgroundResource(shapeRes);
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView titleTv;
        TextView dateTv;
        TextView categoryTv;
    }
}
