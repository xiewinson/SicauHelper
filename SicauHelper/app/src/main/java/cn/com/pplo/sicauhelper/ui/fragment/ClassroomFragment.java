package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;

import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.model.Classroom;
import cn.com.pplo.sicauhelper.ui.MainActivity;
import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;

/**
 * Created by winson on 2014/11/1.
 */
public class ClassroomFragment extends BaseFragment {
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
        NetUtil.getClassroomListHtmlStr(getActivity(), new NetUtil.NetCallback(getActivity()) {
            @Override
            protected void onSuccess(String result) {
                StringUtil.parseClassroomListHtmlStr(result, new StringUtil.Callback() {
                    @Override
                    public  void handleParseResult(Object obj) {
                        List<Classroom> tempList = (List<Classroom>) obj;
                        Log.d("winson", "最后结果：" + tempList.size() + " =");
                        for(Classroom c : tempList) {
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
