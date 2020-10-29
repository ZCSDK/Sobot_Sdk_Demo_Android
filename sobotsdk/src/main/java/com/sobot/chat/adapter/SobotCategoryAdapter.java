package com.sobot.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.sobot.chat.MarkConfig;
import com.sobot.chat.SobotApi;
import com.sobot.chat.adapter.base.SobotBaseAdapter;
import com.sobot.chat.api.model.StDocModel;
import com.sobot.chat.notchlib.INotchScreen;
import com.sobot.chat.notchlib.NotchScreenManager;
import com.sobot.chat.utils.ResourceUtils;

import java.util.List;

public class SobotCategoryAdapter extends SobotBaseAdapter<StDocModel> {
    private LayoutInflater mInflater;
    private Activity mActivity;

    public SobotCategoryAdapter(Context context, Activity activity, List<StDocModel> list) {
        super(context, list);
        this.mActivity = activity;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(ResourceUtils
                    .getResLayoutId(context, "sobot_list_item_help_category"), null);
            viewHolder = new ViewHolder(context, mActivity, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.bindData(position, list.get(position));

        return convertView;
    }

    private static class ViewHolder {
        private TextView sobot_tv_title;
        private Activity mActivity;

        public ViewHolder(Context context, Activity activity, View view) {
            this.mActivity = activity;
            sobot_tv_title = (TextView) view.findViewById(ResourceUtils
                    .getResId(context, "sobot_tv_title"));
        }

        public void bindData(int position, StDocModel data) {
            sobot_tv_title.setText(data.getQuestionTitle());
            displayInNotch(sobot_tv_title);
        }

        public void displayInNotch(final View view) {
            if (SobotApi.getSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN) && SobotApi.getSwitchMarkStatus(MarkConfig.DISPLAY_INNOTCH) && view != null) {
                // 支持显示到刘海区域
                NotchScreenManager.getInstance().setDisplayInNotch(mActivity);
                // 设置Activity全屏
                mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

                // 获取刘海屏信息
                NotchScreenManager.getInstance().getNotchInfo(mActivity, new INotchScreen.NotchScreenCallback() {
                    @Override
                    public void onResult(INotchScreen.NotchScreenInfo notchScreenInfo) {
                        if (notchScreenInfo.hasNotch) {
                            for (Rect rect : notchScreenInfo.notchRects) {
                                view.setPadding(rect.right, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
                            }
                        }
                    }
                });

            }
        }
    }


}