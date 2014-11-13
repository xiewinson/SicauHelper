package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.FindCallback;

import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.dao.GoodsDAO;
import cn.com.pplo.sicauhelper.ui.AddGoodsActivity;
import cn.com.pplo.sicauhelper.ui.MainActivity;
import cn.com.pplo.sicauhelper.widget.PagerSlidingTabStrip;

public class SchoolMarketFragment extends BaseFragment {

    private static final String SCHOOL_POSITION = "school_position";
    private int schoolPosition = 0;
    private String[] schoolArray;

    private ListView listView;

    public static SchoolMarketFragment newInstance(int position) {
        SchoolMarketFragment fragment = new SchoolMarketFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(SCHOOL_POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    public SchoolMarketFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        schoolPosition = getArguments().getInt(SCHOOL_POSITION);
        schoolArray = getResources().getStringArray(R.array.school);
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
        return inflater.inflate(R.layout.fragment_school_market, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUp(getActivity(), view);
    }

    private void setUp(Context context, View view) {
        listView = (ListView) view.findViewById(R.id.goods_listView);

        new GoodsDAO().find(schoolPosition, new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e == null) {
                    Log.d("winson", list.size() + "个");
                    for (AVObject avObject : list) {
                        Log.d("winson", new GoodsDAO().toModel(avObject).toString());
                    }
                }
                else {
                    Log.d("winson", "出错：" + e.getMessage());
                }
            }
        });

        //TODO 实现分页查询
        new GoodsDAO().find(schoolPosition, new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e == null) {
                    Log.d("winson", "新的：" + list.size() + "个");
                    for (AVObject avObject : list) {
                        Log.d("winson", new GoodsDAO().toModel(avObject).toString());
                    }
                }
                else {
                    Log.d("winson", "出错：" + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.market, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
