package cn.com.pplo.sicauhelper.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.ui.StatusActivity;
import cn.com.pplo.sicauhelper.ui.UserActivity;
import cn.com.pplo.sicauhelper.util.ImageUtil;
import cn.com.pplo.sicauhelper.util.TimeUtil;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by winson on 2014/11/12.
 */
public class StatusAdapter extends BaseAdapter {

    private Context context;
    private List<AVObject> data;

    public StatusAdapter(Context context, List<AVObject> data) {
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
        Viewholder holder = null;
        if(convertView == null) {
            holder = new Viewholder();
            convertView = View.inflate(context, R.layout.item_status_list, null);
            holder.headIv = (CircleImageView) convertView.findViewById(R.id.status_head_iv);
            holder.nameTv = (TextView) convertView.findViewById(R.id.status_name_tv);
            holder.dateTv = (TextView) convertView.findViewById(R.id.status_time_tv);
            holder.categoryTv = (TextView) convertView.findViewById(R.id.status_category_tv);
            holder.titleTv = (TextView) convertView.findViewById(R.id.status_title_tv);
            holder.contentTv = (TextView) convertView.findViewById(R.id.status_content_tv);
            holder.imageLayout = (LinearLayout) convertView.findViewById(R.id.status_image_layout);
            holder.commentBtn = (TextView) convertView.findViewById(R.id.status_comment_btn);
            holder.deviceTv = (TextView) convertView.findViewById(R.id.status_device_tv);
            holder.locationTv = (TextView) convertView.findViewById(R.id.status_location_tv);
            holder.allLayout = convertView.findViewById(R.id.all_layout);
            convertView.setTag(holder);
        }
        else {
            holder = (Viewholder) convertView.getTag();
        }

        final AVObject avstatus = getItem(position);
        final AVObject avStudent = avstatus.getAVObject(TableContract.TableStatus._USER);
        //头像
        ImageLoader.getInstance().displayImage(avStudent.getAVFile(TableContract.TableUser._PROFILE_URL).getUrl(), holder.headIv, ImageUtil.getDisplayProfileOption(context));
        //名字
        holder.nameTv.setText(avStudent.getString(TableContract.TableUser._NICKNAME));
        //时间
        holder.dateTv.setText(TimeUtil.timeToFriendlTime(avstatus.getCreatedAt().toString()));
        //类别(暂时不用)
//        holder.categoryTv.setText("￥" + avstatus.getInt(TableContract.TableStatus._PRICE) + "");
        //标题
        holder.titleTv.setText(avstatus.getString(TableContract.TableStatus._TITLE));
        //内容
        holder.contentTv.setText(avstatus.getString(TableContract.TableStatus._CONTENT));
        //评论
        holder.commentBtn.setText(avstatus.getLong(TableContract.TableStatus._COMMENT_COUNT) + "");
        //手机
        holder.deviceTv.setText("来自 " + avstatus.getString(TableContract.TableStatus._BRAND) + " "
                + avstatus.getString(TableContract.TableStatus._MODEL) + " ("
                + avstatus.getString(TableContract.TableStatus._VERSION) + ") ");
        //地址
        String address = avstatus.getString(TableContract.TableGoods._ADDRESS);
        holder.locationTv.setText(address);
        if(TextUtils.isEmpty(address)) {
            holder.locationTv.setVisibility(View.GONE);
        }
        else {
            holder.locationTv.setVisibility(View.VISIBLE);
        }
        
        //显示图片
        List<AVFile> imageList = ImageUtil.getAVFileListByAVObject(avstatus);
        //图片url列表
        final String[] imageUrl = ImageUtil.getImageUrlsByAVFileList(imageList);
        holder.imageLayout.removeAllViews();
        int childPosition = 0;
        for(AVFile avFile : imageList) {
            ImageView imageView = ImageUtil.getThumImageView(imageUrl, childPosition, avFile, context);
            holder.imageLayout.addView(imageView);
            childPosition ++;
        }

        //点击头像打开UserActivity
        holder.headIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserActivity.startUserActivity(context, avStudent.getObjectId());
            }
        });

        //打开详细页面
        holder.allLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatusActivity.startStatusActivity(context, avstatus.getObjectId(), avstatus.getAVObject(TableContract.TableStatus._USER).getInt(TableContract.TableUser._SCHOOL));
            }
        });
        return convertView;
    }

    private static class Viewholder {
        CircleImageView headIv;
        TextView nameTv;
        TextView dateTv;
        TextView categoryTv;
        TextView titleTv;
        TextView contentTv;
        LinearLayout imageLayout;
        TextView commentBtn;
        TextView deviceTv;
        TextView locationTv;
        View allLayout;
    }
}
