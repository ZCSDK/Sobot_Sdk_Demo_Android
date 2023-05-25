package com.sobot.chat.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;

import com.sobot.chat.ZCSobotApi;
import com.sobot.chat.activity.SobotTicketDetailActivity;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.SobotLeaveReplyModel;
import com.sobot.chat.api.model.SobotUserTicketInfo;
import com.sobot.chat.api.model.ZhiChiPushMessage;
import com.sobot.chat.conversation.SobotChatActivity;

import java.util.ArrayList;
import java.util.List;

public class NotificationUtils {

    private static final String SOBOT_CHANNEL_ID = "sobot_channel_id";
    private static List<Integer> notifyIds = new ArrayList<>();

    public static void createNotification(Context context, String title, String content, String ticker, int id, ZhiChiPushMessage pushMessage) {

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) {
            return;
        }

        Intent detailIntent = new Intent(context, SobotChatActivity.class);
        Information information = ZCSobotApi.getCurrentInfoSetting(context);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ZhiChiConstant.SOBOT_BUNDLE_INFO, information);
        detailIntent.putExtra(ZhiChiConstant.SOBOT_BUNDLE_INFORMATION, bundle);
        //传递数据想要成功，需要设置这里的flag参数
        detailIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        detailIntent.setPackage(context.getPackageName());
        int flag = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= 23) {
            flag |= PendingIntent.FLAG_IMMUTABLE;
        }
        //SDK31 Android12中待处理的PendingIntent必须声明可变性 要声明给定PendingIntent对象是可变的或不可变的 ，请分别使用 PendingIntent.FLAG_MUTABLE 或 PendingIntent.FLAG_IMMUTABLE标志。
        // Intent中需要传递Id或者其他数据，一定要用FLAG_UPDATE_CURRENT或者FLAG_CANCEL_CURRENT，曾经一直接收不到data，查了半天原来是这个原因
        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0,
                detailIntent, flag);

        int smallicon = SharedPreferencesUtil.getIntData(context, ZhiChiConstant
                .SOBOT_NOTIFICATION_SMALL_ICON, ResourceUtils.getIdByName(context, "drawable", "sobot_logo_small_icon"));
        int largeicon = SharedPreferencesUtil.getIntData(context, ZhiChiConstant
                .SOBOT_NOTIFICATION_LARGE_ICON, ResourceUtils.getIdByName(context, "drawable", "sobot_logo_icon"));

        BitmapDrawable bd = (BitmapDrawable) context.getResources().getDrawable(largeicon);
        Bitmap bitmap = bd.getBitmap();
        String contentTemp = HtmlTools.getInstance(context).getHTMLStr(content);
        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(smallicon) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
                // icon)
                // .setLargeIcon(bitmap)
                .setTicker(ticker)
//                .setContentTitle(title)
                .setContentText(contentTemp)
                .setContentIntent(pendingIntent2);

        boolean compatFlag = CommonUtils.getTargetSdkVersion(context) >= 26;
        if (Build.VERSION.SDK_INT >= 26 && compatFlag) {
            String SOBOT_CHANNEL_NAME = ResourceUtils.getResString(context, "sobot_notification_name");//"客服通知";
            NotificationChannel mChannel = new NotificationChannel(SOBOT_CHANNEL_ID, SOBOT_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(mChannel);
            builder.setChannelId(SOBOT_CHANNEL_ID);
        }
        Notification notify2 = builder.getNotification();
        notify2.flags |= Notification.FLAG_AUTO_CANCEL;

        notify2.defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;

        notifyIds.add(id);
        manager.notify(id, notify2);
    }

    public static void createLeaveReplyNotification(Context context, String title, String content, String ticker, int id, String companyId, String uid, SobotLeaveReplyModel leaveReplyModel) {
        try {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager == null) {
                return;
            }
            SobotUserTicketInfo item = new SobotUserTicketInfo();
            if (leaveReplyModel != null) {
                item.setTicketId(leaveReplyModel.getTicketId());
                LogUtils.i(" 留言回复：" + leaveReplyModel);
            }
            Intent detailIntent = new Intent(context, SobotTicketDetailActivity.class);
            detailIntent.putExtra(SobotTicketDetailActivity.INTENT_KEY_UID, uid);
            detailIntent.putExtra(SobotTicketDetailActivity.INTENT_KEY_COMPANYID, companyId);
            detailIntent.putExtra(SobotTicketDetailActivity.INTENT_KEY_TICKET_INFO, item);
            //传递数据想要成功，需要设置这里的flag参数
            detailIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            detailIntent.setPackage(context.getPackageName());
            int flag = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= 23) {
                flag |= PendingIntent.FLAG_IMMUTABLE;
            }
            PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0,
                    detailIntent, flag);
            int smallicon = SharedPreferencesUtil.getIntData(context, ZhiChiConstant
                    .SOBOT_NOTIFICATION_SMALL_ICON, ResourceUtils.getIdByName(context, "drawable", "sobot_logo_small_icon"));
            int largeicon = SharedPreferencesUtil.getIntData(context, ZhiChiConstant
                    .SOBOT_NOTIFICATION_LARGE_ICON, ResourceUtils.getIdByName(context, "drawable", "sobot_logo_icon"));

            BitmapDrawable bd = (BitmapDrawable) context.getResources().getDrawable(largeicon);
            Bitmap bitmap = bd.getBitmap();
            Notification.Builder builder = new Notification.Builder(context)
                    .setSmallIcon(smallicon) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
                    // icon)
//                .setLargeIcon(bitmap)
                    .setTicker(ticker)
                    .setContentTitle(title)
                    .setShowWhen(true)
                    .setContentText(Html.fromHtml(content))
                    .setContentIntent(pendingIntent2);

            boolean compatFlag = CommonUtils.getTargetSdkVersion(context) >= 26;
            if (Build.VERSION.SDK_INT >= 26 && compatFlag) {
                String SOBOT_CHANNEL_NAME = ResourceUtils.getResString(context, "sobot_notification_name");//"客服通知";
                NotificationChannel mChannel = new NotificationChannel(SOBOT_CHANNEL_ID, SOBOT_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(mChannel);
                builder.setChannelId(SOBOT_CHANNEL_ID);
            } else {

            }

            Notification notify2 = builder.getNotification();
            notify2.flags |= Notification.FLAG_AUTO_CANCEL;

            notify2.defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;
            int noId = getNotificationId();
            notifyIds.add(noId);
            manager.notify(noId, notify2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cancleAllNotification(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            try {
                if(notifyIds!=null && notifyIds.size()>0) {
                    for (int i = 0; i < notifyIds.size(); i++) {
                        nm.cancel(notifyIds.get(i));
                    }
                    notifyIds.clear();
                }
            } catch (Exception e) {
                //ignore
            }
        }
    }


    public static int tmpNotificationId = 1000;

    /**
     * 获取通知的id  如果id涨到了1999那么重置为0，从1000开始发送
     *
     * @return
     */
    public static final int getNotificationId() {
        if (tmpNotificationId == 1999) {
            tmpNotificationId = 1000;
        }
        tmpNotificationId++;
        return tmpNotificationId;
    }
}