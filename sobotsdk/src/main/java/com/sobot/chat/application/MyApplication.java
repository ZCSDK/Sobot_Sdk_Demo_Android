package com.sobot.chat.application;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

public class MyApplication extends Application {

    private List<Activity> activityList = new LinkedList<Activity>();
    private static MyApplication instance;

    // 单例模式获取唯一的MyApplication
    public static MyApplication getInstance() {
        if (null == instance) {
            instance = new MyApplication();
        }
        return instance;
    }

    // 添加activity到容器中
    public void addActivity(Activity aty) {
        activityList.add(aty);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /*退出时关闭所有的activity*/
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
    }


    /*获取智齿最后一个activity*/
    public Activity getLastActivity() {
        if (activityList != null && activityList.size() > 0) {
            return activityList.get(activityList.size() - 1);
        }
        return null;
    }

    public void deleteActivity(Activity aty) {
        activityList.remove(aty);
    }

}