package com.sobot.chat.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.adapter.base.SobotBaseGvAdapter;
import com.sobot.chat.api.model.SobotRobot;
import com.sobot.chat.utils.ResourceUtils;

import java.util.List;

public class SobotRobotListAdapter extends SobotBaseGvAdapter<SobotRobot> {

    public SobotRobotListAdapter(Context context, List<SobotRobot> list) {
        super(context, list);
    }

    @Override
    protected String getContentLayoutName() {
        return "sobot_list_item_robot";
    }

    @Override
    protected BaseViewHolder getViewHolder(Context context, View view) {
        return new ViewHolder(context, view);
    }

    private static class ViewHolder extends BaseViewHolder<SobotRobot> {
        private TextView sobot_tv_content;
        private LinearLayout sobot_ll_content;

        private ViewHolder(Context context, View view) {
            super(context, view);
            sobot_ll_content = (LinearLayout) view.findViewById(ResourceUtils
                    .getIdByName(context, "id", "sobot_ll_content"));
            sobot_tv_content = (TextView) view.findViewById(ResourceUtils
                    .getIdByName(context, "id", "sobot_tv_content"));
        }

        public void bindData(SobotRobot sobotRobot, int position) {
            if (sobotRobot != null && !TextUtils.isEmpty(sobotRobot.getOperationRemark())) {
                sobot_ll_content.setVisibility(View.VISIBLE);
                sobot_ll_content.setSelected(sobotRobot.isSelected());
                sobot_tv_content.setText(sobotRobot.getOperationRemark());
            } else {
                sobot_ll_content.setVisibility(View.INVISIBLE);
                sobot_ll_content.setSelected(false);
                sobot_tv_content.setText("");
            }
        }
    }


}