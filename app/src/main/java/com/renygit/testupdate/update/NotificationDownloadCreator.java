package com.renygit.testupdate.update;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.renygit.testupdate.R;

import org.lzh.framework.updatepluginlib.base.DownloadCallback;
import org.lzh.framework.updatepluginlib.base.DownloadNotifier;
import org.lzh.framework.updatepluginlib.impl.DefaultDownloadNotifier;
import org.lzh.framework.updatepluginlib.model.Update;

import java.io.File;
import java.util.UUID;

/**
 * <p>
 *     很多小伙伴提意见说需要一个下载时在通知栏进行进度条显示更新的功能。
 *     此类用于提供此种需求的解决方案。以及如何对其进行定制。满足任意场景使用
 *     默认使用参考：{@link DefaultDownloadNotifier}
 * </p>
 */
public class NotificationDownloadCreator extends DownloadNotifier {
    @Override
    public DownloadCallback create(Update update, Activity activity) {
        // 返回一个UpdateDownloadCB对象用于下载时使用来更新界面。
        return new NotificationCB(update, activity);
    }

    private static class NotificationCB implements DownloadCallback {

        NotificationManager manager;
        Notification.Builder builder;
        int id;
        int preProgress;
        Update update;

        NotificationCB (Update update, Activity activity) {
            this.manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

            String CHANNEL_ID = activity.getPackageName() + "test";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence CHANNEL_NAME = "testUpdate";
                String Description = "";//新版本下载
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                mChannel.setSound(null, null);
                mChannel.setDescription(Description);
                mChannel.enableLights(false);
                mChannel.enableVibration(false);
                mChannel.setShowBadge(false);
                manager.createNotificationChannel(mChannel);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = new Notification.Builder(activity, CHANNEL_ID);//NotificationUtils.getNotificationBuilder(activity, CHANNEL_ID);
            }else {
                builder = new Notification.Builder(activity);
            }
            builder.setProgress(100, 0, false)
                    .setVibrate(new long[]{0})
                    .setSound(null)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(false)
                    .setContentTitle("下载进度")
                    .setContentText("版本号："+update.getVersionName())
                    .build();

            id = Math.abs(UUID.randomUUID().hashCode());
            this.update = update;
        }

        @Override
        public void onDownloadStart() {
            // 下载开始时的通知回调。运行于主线程
            manager.notify(id,builder.build());
        }

        @Override
        public void onDownloadComplete(File file) {
            // 下载完成的回调。运行于主线程
            manager.cancel(id);
        }

        @Override
        public void onDownloadProgress(long current, long total) {
            // 下载过程中的进度信息。在此获取进度信息。运行于主线程
            int progress = (int) (current * 1f / total * 100);
            // 过滤不必要的刷新进度
            if (preProgress < progress) {
                preProgress = progress;
                builder.setProgress(100,progress,false);
                manager.notify(id, builder.build());
            }
        }

        @Override
        public void onDownloadError(Throwable t) {
            // 下载时出错。运行于主线程
            manager.cancel(id);
        }
    }
}
