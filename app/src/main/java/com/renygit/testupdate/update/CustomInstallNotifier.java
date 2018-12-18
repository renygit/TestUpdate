package com.renygit.testupdate.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import org.lzh.framework.updatepluginlib.base.InstallNotifier;
import org.lzh.framework.updatepluginlib.util.SafeDialogHandle;

import java.lang.reflect.Field;

/**
 * Created by reny on 2018/12/14.
 */

public class CustomInstallNotifier extends InstallNotifier {

    @Override
    public Dialog create(Activity activity) {
        String updateContent = update.getUpdateContent();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle("新版本已下载，是否安装？")
                .setMessage(updateContent)
                .setPositiveButton("立即安装", (dialog, which) -> {
                    preventDismissDialog(dialog);
                    sendToInstall();
                });

        if (!update.isForced()) {
            if(update.isIgnore()) {
                builder.setNeutralButton("忽略此版本", (dialog, which) -> {
                    try {
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        //设置mShowing值
                        field.set(dialog, true);
                    } catch (Exception e) {
                        // ignore
                    }
                    sendCheckIgnore();
                    SafeDialogHandle.safeDismissDialog((Dialog) dialog);
                });
            }
        }

        if (!update.isForced()) {
            builder.setNegativeButton("取消", (dialog, which) -> {
                try {
                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    //设置mShowing值
                    field.set(dialog, true);
                } catch (Exception e) {
                    // ignore
                }
                sendUserCancel();
                SafeDialogHandle.safeDismissDialog((Dialog) dialog);
            });
        }

        AlertDialog installDialog = builder.create();
        installDialog.setCancelable(false);
        installDialog.setCanceledOnTouchOutside(false);
        return installDialog;
    }

    /**
     * 通过反射 阻止自动关闭对话框
     */
    private void preventDismissDialog(DialogInterface dialog) {
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            //设置mShowing值，欺骗android系统
            field.set(dialog, false);
        } catch (Exception e) {
            // ignore
        }
    }
}
