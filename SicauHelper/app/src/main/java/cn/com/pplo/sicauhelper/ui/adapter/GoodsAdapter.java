package cn.com.pplo.sicauhelper.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.dao.StudentDAO;
import cn.com.pplo.sicauhelper.model.Goods;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.util.ImageUtil;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by winson on 2014/11/12.
 */
public class GoodsAdapter extends BaseAdapter {

    private Context context;
    private List<AVObject> data;

    public GoodsAdapter(Context context, List<AVObject> data) {
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
            convertView = View.inflate(context, R.layout.item_goods_list, null);
            holder.headIv = (CircleImageView) convertView.findViewById(R.id.goods_head_iv);
            holder.nameTv = (TextView) convertView.findViewById(R.id.goods_name_tv);
            holder.dateTv = (TextView) convertView.findViewById(R.id.goods_time_tv);;
            holder.categoryTv = (TextView) convertView.findViewById(R.id.goods_category_tv);;
            holder.titleTv = (TextView) convertView.findViewById(R.id.goods_title_tv);;
            holder.contentTv = (TextView) convertView.findViewById(R.id.goods_content_tv);;
            holder.imageLayout = (LinearLayout) convertView.findViewById(R.id.goods_image_layout);
            holder.commentBtn = (Button) convertView.findViewById(R.id.goods_comment_btn);;
            holder.deviceTv = (TextView) convertView.findViewById(R.id.goods_device_tv);;
            convertView.setTag(holder);
        }
        else {
            holder = (Viewholder) convertView.getTag();
        }

//        goods.setAddress(avObject.getString(TableContract.TableGoods._ADDRESS));
//        goods.setBrand(avObject.getString(TableContract.TableGoods._BRAND));
//        goods.setCategory(avObject.getInt(TableContract.TableGoods._CATEGORY));
//        goods.setContent(avObject.getString(TableContract.TableGoods._CONTENT));
//        goods.setVersion(avObject.getString(TableContract.TableGoods._VERSION));
//        goods.setModel(avObject.getString(TableContract.TableGoods._MODEL));
//        goods.setPrice(avObject.getInt(TableContract.TableGoods._PRICE));
//        goods.setSchool(avObject.getInt(TableContract.TableGoods._SCHOOL));
//        goods.setTitle(avObject.getString(TableContract.TableGoods._TITLE));
//        goods.setId(avObject.getLong("goods_id"));
//        goods.setCommentCount(avObject.getLong(TableContract.TableGoods._COMMENT_COUNT));
//        goods.setLatitude(avObject.getAVGeoPoint("location").getLatitude() + "");
//        goods.setLongitude(avObject.getAVGeoPoint("location").getLongitude() + "");
//        goods.setUpdatedAt(avObject.getUpdatedAt().toString());
//        goods.setCreatedAt(avObject.getCreatedAt().toString());
//        goods.setStudent(new StudentDAO().toModel(avObject.getAVObject()));
//        goods.setObjectId(avObject.getObjectId());

        AVObject avGoods = getItem(position);
        AVObject avStudent = avGoods.getAVObject(TableContract.TableGoods._STUDENT);
        //头像
        ImageLoader.getInstance().displayImage(avStudent.getString(TableContract.TableStudent._PROFILE_URL), holder.headIv, ImageUtil.getDisplayImageOption(context));
        //名字
        holder.nameTv.setText(avStudent.getString(TableContract.TableStudent._NAME));
        //时间
        holder.dateTv.setText(avGoods.getUpdatedAt().toString());
        //类别
        holder.categoryTv.setText(avGoods.getInt(TableContract.TableGoods._CATEGORY) + "");
        //标题
        holder.titleTv.setText(avGoods.getString(TableContract.TableGoods._TITLE));
        //内容
        holder.contentTv.setText(avGoods.getString(TableContract.TableGoods._CONTENT));
        //评论
        holder.commentBtn.setText(avGoods.getLong(TableContract.TableGoods._COMMENT_COUNT) + "");
        //手机
        holder.deviceTv.setText("来自" + avGoods.getString(TableContract.TableGoods._BRAND) + " "
                + avGoods.getString(TableContract.TableGoods._MODEL) + "("
                + avGoods.getString(TableContract.TableGoods._VERSION) + ")");
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
        Button commentBtn;
        TextView deviceTv;
    }
}
