package cn.com.pplo.sicauhelper.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.util.ImageUtil;
import cn.com.pplo.sicauhelper.util.TimeUtil;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by winson on 2014/11/16.
 */
public class CommentAdapter extends BaseAdapter {

    private Context context;
    private List<AVObject> data;

    public CommentAdapter(Context context, List<AVObject> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public AVObject getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_comment_list, null);
            holder.headIv = (CircleImageView) convertView.findViewById(R.id.comment_head_iv);
            holder.nameTv = (TextView) convertView.findViewById(R.id.comment_name_tv);
            holder.timeTv = (TextView) convertView.findViewById(R.id.comment_time_tv);
            holder.contentTv = (TextView) convertView.findViewById(R.id.comment_comment_tv);
            holder.deviceTv = (TextView) convertView.findViewById(R.id.comment_device_tv);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        final AVObject avComment = getItem(position);
        AVObject avStudent = avComment.getAVObject(TableContract.TableGoodsComment._SEND_USER);
        //头像
        ImageLoader.getInstance().displayImage(avStudent.getAVFile(TableContract.TableUser._PROFILE_URL).getUrl(), holder.headIv, ImageUtil.getDisplayImageOption(context));
        //名字
        holder.nameTv.setText(avStudent.getString(TableContract.TableUser._NICKNAME));
        //时间
        holder.timeTv.setText(TimeUtil.timeToFriendlTime(avComment.getCreatedAt().toString()));
        //内容
        String content = avComment.getString(TableContract.TableGoodsComment._CONTENT);
        AVObject avReceive = avComment.getAVObject(TableContract.TableGoodsComment._RECEIVE_USER);
        if(avReceive != null) {
            content = "回复 " + avReceive.getString(TableContract.TableUser._NICKNAME) + ": " + content;
        }
        holder.contentTv.setText(content);
        //手机
        holder.deviceTv.setText("来自 " + avComment.getString(TableContract.TableGoods._BRAND) + " "
                + avComment.getString(TableContract.TableGoods._MODEL) + " ("
                + avComment.getString(TableContract.TableGoods._VERSION) + ") ");
        return convertView;
    }

    private static class ViewHolder {
        CircleImageView headIv;
        TextView nameTv;
        TextView timeTv;
        TextView contentTv;
        TextView deviceTv;
    }
}
