package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.GridView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.model.Classroom;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.service.SaveIntentService;
import cn.com.pplo.sicauhelper.ui.MainActivity;
import cn.com.pplo.sicauhelper.ui.adapter.ClassroomAdapter;
import cn.com.pplo.sicauhelper.util.CursorUtil;
import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;

/**
 * Created by winson on 2014/11/1.
 */
public class ClassroomFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private GridView gridView;
    private AlertDialog progressDialog;
    private ClassroomAdapter classroomAdapter;
    private List<Classroom> data = new ArrayList<Classroom>();
    private List<Classroom> filterData = new ArrayList<Classroom>();
    private List<Classroom> originalData = new ArrayList<Classroom>();
    private boolean isOnlyShowThisSchool = true;

    public static ClassroomFragment newInstance() {
        ClassroomFragment fragment = new ClassroomFragment();
        return fragment;
    }

    public ClassroomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached("空闲教室");
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
        return inflater.inflate(R.layout.fragment_classroom, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UIUtil.setActionBarColorBySchool(getActivity(),
                SicauHelperApplication.getStudent().getInt(TableContract.TableStudent._SCHOOL),
                getSupportActionBar(getActivity()));
        setUp(view, getActivity());
    }

    private void setUp(View view, Context context) {
        classroomAdapter = new ClassroomAdapter(context, data);
        gridView = (GridView) view.findViewById(R.id.classroom_gridView);
        progressDialog = UIUtil.getProgressDialog(context, "我算算哪些教室是空的，这个过程是相当的漫长～");
//        gridView.setAdapter(classroomAdapter);
        UIUtil.setListViewInitAnimation("bottom", gridView, classroomAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * 网络请求空闲教室列表
     * @param context
     */
    private void requestClassroomList(final Context context) {
        progressDialog.show();
        NetUtil.getClassroomListHtmlStr(context, new NetUtil.NetCallback(context) {
            @Override
            protected void onSuccess(String result) {
                StringUtil.parseClassroomListHtmlStr(result, new StringUtil.Callback() {
                    @Override
                    public void handleParseResult(Object obj) {
                        List<Classroom> tempList = (List<Classroom>) obj;
                        keepOriginalData(tempList);
                        selectOnlyThisSchoolData(true);
//                        keepFilterData(tempList);
                        notifyDataSetChanged();
                        UIUtil.dismissProgressDialog(progressDialog);
                        SaveIntentService.startActionClassroomAll(context, tempList);
                    }
                });
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                UIUtil.dismissProgressDialog(progressDialog);
                Log.d("winson", volleyError.getMessage() + "错误了。" + volleyError.networkResponse + volleyError.getLocalizedMessage());
            }
        });
    }

    /**
     * 选择是否仅所在校区的数据
     * @param isOnlyShowThisSchool
     */
    private void selectOnlyThisSchoolData(boolean isOnlyShowThisSchool) {
        if(isOnlyShowThisSchool) {
            data.clear();
            String school = StringUtil.schoolCodeToSchool(SicauHelperApplication.getStudent().getInt(TableContract.TableStudent._SCHOOL));

            for(Classroom classroom : originalData) {
                if(classroom.getSchool().contains(school)){
                    data.add(classroom);
                }
            }
            keepFilterData(data);
        }
        else {
            data.clear();
            data.addAll(originalData);
            keepFilterData(data);
        }
        Log.d("winson", data.size() + "的长度是。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。");
    }

    /**
     * 保持过滤数据
     * @param tempList
     */
    private void keepFilterData(List<Classroom> tempList) {
        filterData.clear();
        filterData.addAll(tempList);
    }

    /**
     * 保持原始数据
     * @param tempList
     */
    private void keepOriginalData(List<Classroom> tempList) {
        originalData.clear();
        originalData.addAll(tempList);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.classroom, menu);
        initSearchView(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //初始化搜索
    private void initSearchView(Menu menu) {
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("搜索时间/教室");
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("winson", "焦点：" + hasFocus);
                if (hasFocus) {
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String s) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String s) {
                            Log.d("winson", "开始匹配");
                            classroomAdapter.setFilter(new ClassroomFilter());
                            classroomAdapter.getFilter().filter(s);
                            return false;
                        }
                    });
                    searchView.setOnQueryTextFocusChangeListener(null);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //刷新
        int itemId = item.getItemId();
        if(itemId == R.id.action_refresh){
            getActivity().invalidateOptionsMenu();
            requestClassroomList(getActivity());
        }
        else if(itemId == R.id.action_only_this_school){
            boolean isChecked = item.isChecked();
            selectOnlyThisSchoolData(!isChecked);
            notifyDataSetChanged();
            item.setChecked(!item.isChecked());

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), Uri.parse(SicauHelperProvider.URI_CLASSROOM_ALL), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        if (cursor != null  && cursor.getCount() > 0) {
            List<Classroom> tempList = CursorUtil.parseClassroomList(cursor);
            keepOriginalData(tempList);
            selectOnlyThisSchoolData(true);
//            keepFilterData(tempList);
            notifyDataSetChanged();
        } else {
            requestClassroomList(getActivity());
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    /**
     * 通知ListView数据改变
     */
    private void notifyDataSetChanged(){

            classroomAdapter.notifyDataSetChanged();
            gridView.setSelection(0);
    }

    //空闲教室过滤
    public class ClassroomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String query = constraint.toString();
            List<Classroom> values = new ArrayList<Classroom>();
            FilterResults results = new FilterResults();
            if(TextUtils.isEmpty(query)) {
                values.addAll(filterData);
            }
            else {
                for(Classroom classroom : filterData) {
                    if(classroom.getTime().contains(query) ||
                            classroom.getName().contains(query) ||
                            classroom.getSchool().contains(query)){
                        values.add(classroom);
                    }
                }
            }
            results.values = values;
            results.count = values.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //更新数据
            data.clear();
            data.addAll((List<Classroom>) results.values);
            notifyDataSetChanged();
            //恢复到第一个
        }
    }

}
