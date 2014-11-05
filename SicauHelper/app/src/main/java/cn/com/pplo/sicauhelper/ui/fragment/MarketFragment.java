package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.ui.MainActivity;

public class MarketFragment extends BaseFragment {
    public static MarketFragment newInstance() {
        MarketFragment fragment = new MarketFragment();
        return fragment;
    }

    public MarketFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached("川农市场");
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
        return inflater.inflate(R.layout.fragment_market, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AVObject user = new AVObject("Student");
        user.put("sid", "20118622");
        user.put("name", "谢豪");
        user.put("nickName", "谢文森");
        user.put("pswd", "winson");
        user.put("school", "都江堰");
        user.put("profileUrl", "www.winsontse.com");
        user.put("background", "winsontse.jpg");
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e != null) {

                    Log.d("winson", e.getMessage());
                }
                else {
                    Log.d("winson", "保存成功");
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

        super.onCreateOptionsMenu(menu, inflater);

    }
}
