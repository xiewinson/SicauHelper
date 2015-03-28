package cn.com.pplo.sicauhelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import cn.com.pplo.sicauhelper.service.MessageService;
import cn.com.pplo.sicauhelper.util.UIUtil;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if(intent.getAction() == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("winson", "启动应用");
           try {
               MessageService.startMessageService(context);
           }catch (Exception e) {
               MobclickAgent.reportError(context, e);
           }
        }
    }
}
