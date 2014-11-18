package cn.com.pplo.sicauhelper.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.DeleteCallback;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.action.GoodsAction;
import cn.com.pplo.sicauhelper.action.GoodsCommentAction;

/**
 * Created by winson on 2014/11/18.
 */
public class DialogUtil {

    public static final int DELETE_COMMENT = -1;
    public static final int DELETE_GOODS = -2;

    /**
     * 显示删除dialog
     * @param context
     * @param type
     * @param avObject
     */
    public static void showDeleteDialog(Context context, final int type, final AVObject avObject, final DeleteCallback callback) {
        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        int msgRes = 0;
        if(type == DELETE_COMMENT) {
            msgRes = R.string.is_delete_comment;
        }
        else if(type == DELETE_GOODS) {
            msgRes = R.string.is_delete_goods;
        }
        builder.setMessage(msgRes)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //删除评论
                        if (type == DELETE_COMMENT) {
                            new GoodsCommentAction().delete(avObject, callback);
                        }
                        //删除商品
                        else if (type == DELETE_GOODS) {
                            new GoodsAction().delete(avObject, callback);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }
}
