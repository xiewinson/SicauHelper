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
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.feedback.Comment;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.avos.avoscloud.feedback.FeedbackThread;

import java.util.Date;
import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.action.CommentAction;
import cn.com.pplo.sicauhelper.action.GoodsAction;
import cn.com.pplo.sicauhelper.action.StatusAction;
import cn.com.pplo.sicauhelper.action.UserAction;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.provider.TableContract;

/**
 * Created by winson on 2014/11/18.
 */
public class DialogUtil {

    public static final int GOODS_COMMENT = 1;
    public static final int GOODS = 2;
    public static final int STATUS_COMMENT = 3;
    public static final int STATUS = 4;

    /**
     * 输入数量类别
     */
    public static enum TYPE_COUNT_EDIT {
        GOODS_STATUS, COMMENT
    }

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
        else if(type == STATUS_COMMENT) {
            msgRes = R.string.is_delete_comment;
        }
        else if(type == STATUS) {
            msgRes = R.string.is_delete_status;
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
                        else if(type == STATUS_COMMENT) {
                            new CommentAction().delete(avObject, callback);
                        }
                        else if(type == STATUS) {
                            new StatusAction().delete(avObject, callback);
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
        //投诉帖子评论
        else if(type == STATUS_COMMENT) {
            tv.setText("为何你要投诉这条评论?");
        }
        //投诉帖子
        else if(type == STATUS) {
            tv.setText("为何你要投诉这个帖子?");
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
                                + SicauHelperApplication.getStudent().getString(TableContract.TableUser._NAME)
                                + "(" + SicauHelperApplication.getStudent().getString(TableContract.TableUser._SID)  + ")" +"】");
                        Comment newComment = new Comment();
                        //设置投诉者的类型
                        newComment.setCommentType(Comment.CommentType.USER);
                        //创建时间
                        newComment.setCreatedAt(new Date());

                        //投诉评论
                        if(type == GOODS_COMMENT) {
                            newComment.setType("1");
                            AVUser avUser = avObject.getAVObject(TableContract.TableGoodsComment._SEND_USER);
                            newComment.setContent("【商品评论objectId: " + avObject.getObjectId() +"】"
                                    + "【评论内容: " + avObject.getString(TableContract.TableGoodsComment._CONTENT) +"】"
                                    + "【评论者: " + avUser.getString(TableContract.TableUser._NAME) + avUser.getString(TableContract.TableUser._SID) +"】"
                                    + complainStr);
                        }
                        //投诉商品
                        else if(type == GOODS) {
                            newComment.setType("2");
                            AVUser avUser = avObject.getAVObject(TableContract.TableGoods._USER);
                            newComment.setContent("【商品objectId: " + avObject.getObjectId() +"】"
                                    + "【商品标题: " + avObject.getString(TableContract.TableGoods._TITLE) +"】"
                                    + "【发布者: " + avUser.getString(TableContract.TableUser._NAME) + avUser.getString(TableContract.TableUser._SID) +"】"
                                    + complainStr);
                        }

                        //投诉评论
                        if(type == STATUS_COMMENT) {
                            newComment.setType("3");
                            AVUser avUser = avObject.getAVObject(TableContract.TableStatusComment._SEND_USER);
                            newComment.setContent("【帖子评论objectId: " + avObject.getObjectId() +"】"
                                    + "【评论内容: " + avObject.getString(TableContract.TableStatusComment._CONTENT) +"】"
                                    + "【评论者: " + avUser.getString(TableContract.TableUser._NAME) + avUser.getString(TableContract.TableUser._SID) +"】"
                                    + complainStr);
                        }
                        //投诉商品
                        else if(type == STATUS) {
                            newComment.setType("4");
                            AVUser avUser = avObject.getAVObject(TableContract.TableStatus._USER);
                            newComment.setContent("【商品objectId: " + avObject.getObjectId() +"】"
                                    + "【帖子标题: " + avObject.getString(TableContract.TableStatus._TITLE) +"】"
                                    + "【发布者: " + avUser.getString(TableContract.TableUser._NAME) + avUser.getString(TableContract.TableUser._SID) +"】"
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

    /**
     * 编辑每次加载数量
     * @param context
     * @param type
     */
    public static void showCountEditDialog(final Context context, final TYPE_COUNT_EDIT type) {
        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_count_edit, null);
        final EditText et = (EditText) view.findViewById(R.id.count_et);
        if(type == TYPE_COUNT_EDIT.GOODS_STATUS) {
            et.setText(SharedPreferencesUtil.get(context, SharedPreferencesUtil.PER_GOODS_STATUS_COUNT, 10) + "");
        }
        else {
            et.setText(SharedPreferencesUtil.get(context, SharedPreferencesUtil.PER_COMMENT_COUNT, 10) + "");
        }
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               String str = et.getText().toString();
                if(TextUtils.isEmpty(str)) {
                    if(type == TYPE_COUNT_EDIT.GOODS_STATUS) {
                        SharedPreferencesUtil.put(context, SharedPreferencesUtil.PER_GOODS_STATUS_COUNT, 10);
                    }
                    else {
                        SharedPreferencesUtil.put(context, SharedPreferencesUtil.PER_COMMENT_COUNT, 10);
                    }
                }
                else {
                    int count = Integer.parseInt(str);
                    if(count <= 10 || count >= 100) {
                        count = 10;
                    }
                    if(type == TYPE_COUNT_EDIT.GOODS_STATUS) {
                        SharedPreferencesUtil.put(context, SharedPreferencesUtil.PER_GOODS_STATUS_COUNT, count);
                    }
                    else {
                        SharedPreferencesUtil.put(context, SharedPreferencesUtil.PER_COMMENT_COUNT, count);
                    }
                }
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();
    }

    /**
     * 显示修改昵称对话框
     * @param context
     * @param avUser
     */
    public static void showEditNicknameDialog(final Context context, final AVUser avUser, final SaveCallback saveCallback) {
        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_nickname_edit, null);
        final EditText et = (EditText) view.findViewById(R.id.nickname_et);
        et.setText(avUser.getString(TableContract.TableUser._NICKNAME));
        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String nickname = et.getText().toString().trim();
                        if (TextUtils.isEmpty(nickname)) {
                            UIUtil.showShortToast(context, "昵称不可以为空");
                            return;
                        }
                        String sid = SharedPreferencesUtil.get(context, SharedPreferencesUtil.LOGIN_SID, "").toString();
                        new UserAction().logIn(sid, sid,
                                new LogInCallback() {
                                    @Override
                                    public void done(AVUser avUser, AVException e) {
                                        if(e != null) {
                                            Log.d("winson", "保存失败：" + e.getMessage());
                                        }
                                        else {
                                            avUser.put(TableContract.TableUser._NICKNAME, nickname);
                                            avUser.saveInBackground(saveCallback);
                                        }
                                    }
                                });

                    }
                }).setNegativeButton(R.string.cancel, null);
        alertDialog = builder.create();
        alertDialog.show();
    }

}
