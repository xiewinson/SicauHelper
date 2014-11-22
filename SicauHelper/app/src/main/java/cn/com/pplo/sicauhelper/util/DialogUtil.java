package cn.com.pplo.sicauhelper.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.feedback.Comment;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.avos.avoscloud.feedback.FeedbackThread;

import java.util.Date;
import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.action.CommentAction;
import cn.com.pplo.sicauhelper.action.GoodsAction;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.provider.TableContract;

/**
 * Created by winson on 2014/11/18.
 */
public class DialogUtil {

    public static final int GOODS_COMMENT = 1;
    public static final int GOODS = 2;

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
        if(type == GOODS_COMMENT) {
            msgRes = R.string.is_delete_comment;
        }
        else if(type == GOODS) {
            msgRes = R.string.is_delete_goods;
        }
        builder.setMessage(msgRes)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //删除评论
                        if (type == GOODS_COMMENT) {
                            new CommentAction().delete(avObject, callback);
                        }
                        //删除商品
                        else if (type == GOODS) {
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

    /**
     * 显示投诉对话框
     * @param context
     * @param type
     * @param avObject
     */
    public static void showComplainDialog(final Context context, final int type, final AVObject avObject) {
        final AlertDialog progressDialog = UIUtil.getProgressDialog(context, "投诉中...");
        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_complain, null);
        final TextView tv = (TextView) view.findViewById(R.id.dialog_complain_tv);
        final EditText et = (EditText) view.findViewById(R.id.dialog_complain_et);
        et.setHint("写下你的投诉理由");
        //投诉评论
        if(type == GOODS_COMMENT) {
            tv.setText("为何你要投诉这条评论?");
        }
        //投诉商品
        else if(type == GOODS) {
            tv.setText("为何你要投诉这件商品?");
        }

        alertDialog = builder.setView(view)
                .setPositiveButton("发送", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String complainStr = et.getText().toString().trim();
                        if(TextUtils.isEmpty(complainStr)) {
                            UIUtil.showShortToast(context, "投诉不可为空呀！");
                            return;
                        }
                        progressDialog.show();
                        //开始构建反馈数据
                        FeedbackAgent agent = new FeedbackAgent(context);
                        FeedbackThread thread = agent.getDefaultThread();
                        //投诉人
                        thread.setContact("【投诉人："
                                + SicauHelperApplication.getStudent().getString(TableContract.TableStudent._NAME)
                                + "(" + SicauHelperApplication.getStudent().getString(TableContract.TableStudent._SID)  + ")" +"】");
                        Comment newComment = new Comment("这是一个用户反馈");
                        //设置投诉者的类型
                        newComment.setCommentType(Comment.CommentType.USER);
                        //创建时间
                        newComment.setCreatedAt(new Date());

                        //投诉评论
                        if(type == GOODS_COMMENT) {
                            newComment.setType("1");
                            AVUser avUser = avObject.getAVObject(TableContract.TableGoodsComment._SEND_USER);
                            newComment.setContent("【评论objectId: " + avObject.getObjectId() +"】"
                                    + "【评论内容: " + avObject.getString(TableContract.TableGoodsComment._CONTENT) +"】"
                                    + "【评论者: " + avUser.getString(TableContract.TableStudent._NAME) + avUser.getString(TableContract.TableStudent._SID) +"】"
                                    + complainStr);
                        }
                        //投诉商品
                        else if(type == GOODS) {
                            newComment.setType("2");
                            AVUser avUser = avObject.getAVObject(TableContract.TableGoods._USER);
                            newComment.setContent("【商品objectId: " + avObject.getObjectId() +"】"
                                    + "【商品标题: " + avObject.getString(TableContract.TableGoods._TITLE) +"】"
                                    + "【发布者: " + avUser.getString(TableContract.TableStudent._NAME) + avUser.getString(TableContract.TableStudent._SID) +"】"
                                    + complainStr);
                        }
                        thread.add(newComment);
                        thread.sync(new FeedbackThread.SyncCallback() {
                            @Override
                            public void onCommentsSend(List<Comment> comments, AVException e) {
                                UIUtil.dismissProgressDialog(progressDialog);
                                if(e == null) {

                                }
                                else {
                                    Log.d("winson", "投诉出错：" + e.getMessage());
//                                    UIUtil.showShortToast(context, "投诉出错...");
                                }
                                UIUtil.showShortToast(context, "投诉成功，我会尽快给你一个满意的答复");
                            }

                            @Override
                            public void onCommentsFetch(List<Comment> comments, AVException e) {

                            }
                        });

                    }
                })
                .setNegativeButton(R.string.cancel, null).create();
        alertDialog.show();
    }
}
