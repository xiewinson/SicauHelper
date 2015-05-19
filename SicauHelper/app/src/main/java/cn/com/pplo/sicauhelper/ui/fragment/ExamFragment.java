package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.model.Exam;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.service.SaveIntentService;
import cn.com.pplo.sicauhelper.ui.MainActivity;
import cn.com.pplo.sicauhelper.ui.adapter.ExamAdapter;
import cn.com.pplo.sicauhelper.util.CursorUtil;
import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.SharedPreferencesUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;
import cn.com.pplo.sicauhelper.widget.ViewPadding;

public class ExamFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView listView;
    private AlertDialog progressDialog;
    private List<Exam> examList = new ArrayList<Exam>();
    private ExamAdapter examAdapter;
    private LinearLayout emptyLayout;
    private Button importBtn;

    public static ExamFragment newInstance() {
        ExamFragment fragment = new ExamFragment();
        return fragment;
    }

    public ExamFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached("考试安排");
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
        return inflater.inflate(R.layout.fragment_exam, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //根据校区设置actionBar颜色

        setUp(getActivity(), view);
    }

    private void setUp(final Context context, View view) {
        listView = (ListView) view.findViewById(R.id.exam_listView);
        listView.addHeaderView(ViewPadding.getActionBarPadding(getActivity(), R.color.grey_200));
//        new NetUtil().getBooksByName(getActivity(), requestQueue, new NetUtil.NetCallback(getActivity()) {
//            @Override
//            protected void onSuccess(String result) {
//                Log.d("winson", "取得的是:" + result);
//            }
//        });


        emptyLayout = (LinearLayout) view.findViewById(R.id.empty_layout);
        importBtn = (Button) view.findViewById(R.id.import_btn);
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestExamList(getActivity());
            }
        });

        progressDialog = UIUtil.getProgressDialog(getActivity(), "正在寻找你的考试安排...", true);

        examAdapter = new ExamAdapter(getActivity(), examList);
//        UIUtil.setListViewInitAnimation("bottom", listView, examAdapter);
        listView.setAdapter(examAdapter);

        //滚动隐藏
//        listView.setOnScrollListener(new OnScrollHideOrShowActionBarListener(getSupportActionBar(getActivity())));

        //listView点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
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
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //刷新
        if (item.getItemId() == R.id.action_refresh) {
            requestExamList(getActivity());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), Uri.parse(SicauHelperProvider.URI_EXAM_ALL), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d("winson", "men shu:" + cursor.getCount());
        if (cursor != null && cursor.getCount() > 0) {
            List<Exam> tempList = CursorUtil.parseExamList(cursor);
            notifyDataSetChanged(tempList);
        } else {
//            Intent intent = new Intent(getActivity(), ExamService.class);
//            getActivity().bindService(intent, serviceConn, Context.BIND_AUTO_CREATE);
            //显示导入考试安排按钮
            listView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
//            requestExamList(getActivity());
        }
    }

    /**
     * 从网络请求数据
     *
     * @param context
     */
    public void requestExamList(final Context context) {
        progressDialog.show();

        Map<String, String> params = new HashMap<String, String>();
        params.put("user", SharedPreferencesUtil.get(context, SharedPreferencesUtil.LOGIN_SID, "").toString());
        params.put("pwd", SharedPreferencesUtil.get(context, SharedPreferencesUtil.LOGIN_PSWD, "").toString());
        params.put("lb", "S");
        new NetUtil().getExamHtmlStr(context, requestQueue, params, new NetUtil.NetCallback(context) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("winson", "发生了错误");
                UIUtil.dismissProgressDialog(progressDialog);
                super.onErrorResponse(volleyError);
            }

            @Override
            public void onSuccess(String result) {
                final List<Exam> tempList = StringUtil.parseExamListInfo(result);
                notifyDataSetChanged(tempList);
                UIUtil.dismissProgressDialog(progressDialog);
                SaveIntentService.startActionExamAll(context, tempList);
            }
        });
    }

    /**
     * 通知ListView数据改变
     *
     * @param list
     */
    private void notifyDataSetChanged(List<Exam> list) {
        if (list != null) {
            examList.clear();
            examList.addAll(list);
            if (examList.size() > 0) {
                listView.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);
            }
            examAdapter.notifyDataSetChanged();
            //恢复到第一个
            listView.setSelection(0);
//            UIUtil.setListViewScrollHideOrShowActionBar(getActivity(), listView, getSupportActionBar(getActivity()));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
