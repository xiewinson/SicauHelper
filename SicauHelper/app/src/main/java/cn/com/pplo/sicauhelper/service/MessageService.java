package cn.com.pplo.sicauhelper.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.action.CommentAction;
import cn.com.pplo.sicauhelper.ui.CommentActivity;
import cn.com.pplo.sicauhelper.ui.FeedbackActivity;
import cn.com.pplo.sicauhelper.ui.MainActivity;
import cn.com.pplo.sicauhelper.util.SharedPreferencesUtil;

/**
 *
 */
public class MessageService extends Service {

    public static final int TYPE_GOODS = 1001;
    public static final int TYPE_STATUS = 1002;

    private boolean isGetGoodsMessage = false;
    private boolean isGetStatusMessage = false;

    /**
     * 启动服务
     * @param context
     */
    public static void startMessageService(Context context) {
        Intent intent = new Intent(context, MessageService.class);
        context.startService(intent);
    }

    /**
     * 循环读取
     * @param context
     */
    public static void startRepeatMessageService(Context context) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MessageService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        //首先进行一次获取
        startMessageService(context);
        //不精确地循环获取
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5 * 60 * 1000, 5 * 60 * 1000, pendingIntent);
    }


    public MessageService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final String objectId = AVUser.getCurrentUser().getObjectId();
        if(!TextUtils.isEmpty(objectId)) {
            new CommentAction().countCommentByType(this, CommentAction.GOODS_RECEIVE_COMMENT, objectId, new CountCallback() {
                @Override
                public void done(int i, AVException e) {
                    Log.d("winson", "当前有：" + i + "个商品回复");
                    isGetGoodsMessage = true;
                    showNotification(TYPE_GOODS, objectId, MessageService.this, i, e);
                    stopMessageService();
                }
            });

            new CommentAction().countCommentByType(this, CommentAction.STATUS_RECEIVE_COMMENT, objectId, new CountCallback() {
                @Override
                public void done(int i, AVException e) {
                    Log.d("winson", "当前有：" + i + "个帖子回复");
                    isGetStatusMessage = true;
                    showNotification(TYPE_STATUS, objectId, MessageService.this, i, e);
                    stopMessageService();
                }
            });
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 若两个数据都已获取到则停止服务
     */
    private void stopMessageService() {
        if(isGetStatusMessage && isGetStatusMessage) {
            stopSelf();
        }
    }

    /**
     * 显示通知信息
     * @param context
     */
    private void showNotification(int type, String objectId, Context context, int count, AVException e) {
        int goodsCommentCount = (int) SharedPreferencesUtil.get(context, SharedPreferencesUtil.CURRENT_GOODS_COMMENT_COUNT, 0);
        int statusCommentCount = (int) SharedPreferencesUtil.get(context, SharedPreferencesUtil.CURRENT_STATUS_COMMENT_COUNT, 0);
        Log.d("winson", "商品评论：" + goodsCommentCount + "   圈子评论：" + statusCommentCount);
        if(e != null) {
            Log.d("winson", type + " 获取消息出错： " + e.getMessage());
        }
        else {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra( CommentActivity.EXTRA_OBJECT_ID, objectId);
            String title = "";
            if(type == TYPE_GOODS) {
                intent.putExtra( CommentActivity.EXTRA_COMMENT_TYPE, CommentAction.GOODS_RECEIVE_COMMENT);
                intent.putExtra( CommentActivity.EXTRA_TITLE, "收到的商品评论");
                title = "商品评论";
                if(count <= goodsCommentCount) {
                    return;
                }
                else {
                    count = count - goodsCommentCount;
                }
            }
            else if(type == TYPE_STATUS) {
                intent.putExtra( CommentActivity.EXTRA_COMMENT_TYPE, CommentAction.STATUS_RECEIVE_COMMENT);
                intent.putExtra( CommentActivity.EXTRA_TITLE, "收到的圈子评论");
                title = "圈子评论";
                if(count <= statusCommentCount) {
                    return;
                }
                else {
                    count = count - statusCommentCount;
                }
            }
            title = "收到了" + count + "条" + title;

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentTitle(title)
                    .setContentText("点击查看")
                    .setTicker(title)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_face_unlock_grey600_48dp))
                    .setSmallIcon( R.drawable.ic_stars_white_24dp)
                    .setContentIntent(pi)
                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_SOUND)
                    .setAutoCancel(true);
            notificationManager.notify(type, builder.build());
        }
    }
}
