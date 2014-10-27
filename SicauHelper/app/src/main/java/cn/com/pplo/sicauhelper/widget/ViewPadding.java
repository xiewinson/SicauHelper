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
    public static TextView getListViewPadding(Context context){
        TextView tv = new TextView(context);
        tv.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 1));
        return tv;
    }

    public static View getActionBarPadding(Context context){
        return View.inflate(context, R.layout.actionbar_padding_header, null);
    }
}
