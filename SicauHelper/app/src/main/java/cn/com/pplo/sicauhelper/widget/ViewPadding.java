package cn.com.pplo.sicauhelper.widget;

import android.content.Context;

import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import cn.com.pplo.sicauhelper.R;

/**
 * Created by winson on 2014/9/27.
 */
public class ViewPadding {
    public static View getListViewPadding(Context context){
        View view = View.inflate(context, R.layout.view_header_footer_padding, null);
        return view;
    }

    public static View getActionBarPadding(Context context, int res){
        View view = View.inflate(context, R.layout.actionbar_padding_header, null);
        view.setBackgroundResource(res);
        return view;
    }
}
