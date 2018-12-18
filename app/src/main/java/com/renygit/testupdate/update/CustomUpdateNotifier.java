package com.renygit.testupdate.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import org.lzh.framework.updatepluginlib.base.CheckNotifier;
import org.lzh.framework.updatepluginlib.util.SafeDialogHandle;

/**
 * Created by reny on 2018/12/14.
 */

public class CustomUpdateNotifier extends CheckNotifier {
    @Override
    public Dialog create(Activity activity) {
        String updateContent = update.getUpdateContent();
        AlertDialog.Builder builder =  new AlertDialog.Builder(activity)
                .setMessage(updateContent)
                .setTitle("发现新版本")
                .setPositiveButton("立即更新", (dialog, which) -> {
                    sendDownloadRequest();
                    SafeDialogHandle.safeDismissDialog((Dialog) dialog);
                });
        if (update.isIgnore() && !update.isForced()) {
            builder.setNeutralButton("忽略此版本", (dialog, which) -> {
                sendUserIgnore();
                SafeDialogHandle.safeDismissDialog((Dialog) dialog);
            });
        }

        if (!update.isForced()) {
            builder.setNegativeButton("取消", (dialog, which) -> {
                sendUserCancel();
                SafeDialogHandle.safeDismissDialog((Dialog) dialog);
            });
        }
        builder.setCancelable(false);
        return builder.create();
    }
}
