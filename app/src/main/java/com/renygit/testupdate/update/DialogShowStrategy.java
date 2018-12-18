package com.renygit.testupdate.update;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.lzh.framework.updatepluginlib.base.UpdateStrategy;
import org.lzh.framework.updatepluginlib.impl.WifiFirstStrategy;
import org.lzh.framework.updatepluginlib.model.Update;
import org.lzh.framework.updatepluginlib.util.ActivityManager;

/**
 * 自定义强制显示所有Dialog策略，
 * 默认使用参考 {@link WifiFirstStrategy}
 */
public class DialogShowStrategy extends UpdateStrategy {

    private boolean isWifi;
    //private Update update;
    public static boolean forceShowUpdateDialog = false;

    /** 指定是否在判断出有需要更新的版本时。弹出更新提醒弹窗
     * @param update 需要更新的版本信息
     * @return true 显示弹窗
     */
    @Override
    public boolean isShowUpdateDialog(Update update) {
        isWifi = isConnectedByWifi();
        //this.update = update;
        return forceShowUpdateDialog || !isWifi;
    }

    /**
     * 指定是否下载完成后自动进行安装页不显示弹窗
     * @return true 直接安装，不显示弹窗
     */
    @Override
    public boolean isAutoInstall() {
        return false;
    }

    /**
     * 指定是否在下载的时候显示下载进度弹窗
     * @return true 显示弹窗
     */
    @Override
    public boolean isShowDownloadDialog() {
        return true;
    }

    private boolean isConnectedByWifi() {
        Context context = ActivityManager.get().getApplicationContext();
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        return info != null
                && info.isConnected()
                && info.getType() == ConnectivityManager.TYPE_WIFI;
    }
}
