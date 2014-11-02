package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.listener.OnScrollHideOrShowActionBarListener;
import cn.com.pplo.sicauhelper.model.Classroom;
import cn.com.pplo.sicauhelper.ui.MainActivity;
import cn.com.pplo.sicauhelper.ui.adapter.ClassroomAdapter;
import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;
import cn.com.pplo.sicauhelper.widget.ViewPadding;

/**
 * Created by winson on 2014/11/1.
 */
public class ClassroomFragment extends BaseFragment {

    private GridView gridView;
    private ClassroomAdapter classroomAdapter;
    private List<Classroom> data = new ArrayList<Classroom>();

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
        setUp(view, getActivity());
    }

    private void setUp(View view, Context context) {
        classroomAdapter = new ClassroomAdapter(context, data);
        gridView = (GridView) view.findViewById(R.id.classroom_gridView);
        gridView.setAdapter(classroomAdapter);
        requestClassroomList(getActivity());
    }

    private void requestClassroomList(Context context) {
        NetUtil.getClassroomListHtmlStr(context, new NetUtil.NetCallback(context) {
            @Override
            protected void onSuccess(String result) {
                StringUtil.parseClassroomListHtmlStr(result, new StringUtil.Callback() {
                    @Override
                    public void handleParseResult(Object obj) {
                        List<Classroom> tempList = (List<Classroom>) obj;
                        data.addAll(tempList);
                        classroomAdapter.notifyDataSetChanged();
                        Log.d("winson", "最后结果：" + tempList.size() + " =");
                        for (Classroom c : tempList) {
                            Log.d("winson", c + "");
                        }
                    }
                });
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                Log.d("winson", volleyError.getMessage() + "错误了。" + volleyError.networkResponse + volleyError.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }
}
