package com.renygit.testupdate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.renygit.testupdate.update.CustomInstallNotifier;
import com.renygit.testupdate.update.CustomUpdateNotifier;
import com.renygit.testupdate.update.DialogShowStrategy;
import com.renygit.testupdate.update.NotificationDownloadCreator;
import com.renygit.testupdate.update.ToastCallback;

import org.lzh.framework.updatepluginlib.UpdateBuilder;
import org.lzh.framework.updatepluginlib.UpdateConfig;
import org.lzh.framework.updatepluginlib.base.UpdateParser;
import org.lzh.framework.updatepluginlib.model.Update;
import org.lzh.framework.updatepluginlib.util.UpdatePreference;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToastCallback callback = new ToastCallback(this);
        UpdateConfig.getConfig()
                .setUrl("https://raw.githubusercontent.com/yjfnypeu/UpdatePlugin/master/update.json")// 配置检查更新的API接口
                .setUpdateParser(new UpdateParser() {
                    @Override
                    public Update parse(String response) throws Exception {
                        Update update = new Update();
                        update.setUpdateUrl("https://raw.githubusercontent.com/yjfnypeu/UpdatePlugin/master/update_plugin.apk");
                        update.setVersionCode(10);
                        update.setVersionName("2.0.0");
                        update.setUpdateContent("test");
                        update.setForced(false);
                        update.setIgnore(true);
                        return update;
                    }
                })
                .setCheckCallback(callback)
                .setDownloadCallback(callback)
                .setDownloadNotifier(new NotificationDownloadCreator())
                .setUpdateStrategy(new DialogShowStrategy())
                .setInstallNotifier(new CustomInstallNotifier())
                // 自定义检查出更新后显示的Dialog，
                .setCheckNotifier(new CustomUpdateNotifier());
    }

    public void clickIgnore(View view){
        Set<String> ignoreVersions = UpdatePreference.getIgnoreVersions();
        String ignoreList = "忽略版本号：";
        for (String str : ignoreVersions) {
            ignoreList += str;
            Log.e("clickIgnore", "ignoreVersions:"+str);
        }
        Toast.makeText(this, ignoreList, Toast.LENGTH_LONG).show();
    }

    public void clickCheck(View view){
        UpdateBuilder.create().check();// 启动更新任务
    }


    public void clickClear(View view){
        SharedPreferences sp = getSharedPreferences("update_preference", Context.MODE_PRIVATE);
        sp.edit().putStringSet("ignoreVersions",new HashSet<String>()).apply();
        Toast.makeText(this, "已清除", Toast.LENGTH_LONG).show();
    }



}
