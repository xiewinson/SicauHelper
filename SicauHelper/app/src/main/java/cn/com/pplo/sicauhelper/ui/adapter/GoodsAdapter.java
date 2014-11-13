package cn.com.pplo.sicauhelper.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import cn.com.pplo.sicauhelper.model.Goods;

/**
 * Created by winson on 2014/11/12.
 */
public class GoodsAdapter extends BaseAdapter {

    private Context context;
    private List<Goods> data;

    public GoodsAdapter(Context context, List<Goods> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Goods getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

}
