package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.model.Student;
import cn.com.pplo.sicauhelper.ui.MainActivity;
import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;

public class ScoreFragment extends Fragment {

    public static ScoreFragment newInstance() {
        ScoreFragment fragment = new ScoreFragment();
        return fragment;
    }
    public ScoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity)activity).onSectionAttached("成绩");
        super.onAttach(activity);
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
        return inflater.inflate(R.layout.fragment_score, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        //此处需要修改

        Map<String, String> params = new HashMap<String, String>();
        Student student = SicauHelperApplication.getStudent();
        if(student != null) {
            params.put("user", student.getSid() + "");
            params.put("pwd", student.getPswd());
            NetUtil.getScoreHtmlStr(getActivity(), params, new NetUtil.NetCallbcak(getActivity()) {
                @Override
                public void onResponse(String result) {
                    super.onResponse(result);
                    Log.d("winson", "成绩" + result);
                    Log.d("winson", "最终的数据：" + StringUtil.parseScoreInfo(result));
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    super.onErrorResponse(volleyError);
                }
            });
        }

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
