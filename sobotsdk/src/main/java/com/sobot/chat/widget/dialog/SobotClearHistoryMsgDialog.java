package com.sobot.chat.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.MarkConfig;
import com.sobot.chat.SobotApi;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;

/**
 * 右上角清空历史记录弹窗
 */

public class SobotClearHistoryMsgDialog extends Dialog {

    private Button sobot_btn_cancle_conversation, sobot_btn_temporary_leave;
    private TextView sobot_tv_clear_his_msg_describe;
    private LinearLayout coustom_pop_layout;
    private View.OnClickListener itemsOnClick;
    private final int screenHeight;

    public SobotClearHistoryMsgDialog(Activity context, View.OnClickListener itemsOnClick) {
        super(context, ResourceUtils.getIdByName(context, "style", "sobot_noAnimDialogStyle"));
        this.itemsOnClick = itemsOnClick;
        screenHeight = ScreenUtils.getScreenHeight(context);

        // 修改Dialog(Window)的弹出位置
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.CENTER;
            //横屏设置dialog全屏
            if (SobotApi.getSwitchMarkStatus(MarkConfig.DISPLAY_INNOTCH) && SobotApi.getSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN)) {
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            }
            setParams(context, layoutParams);
            window.setAttributes(layoutParams);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ResourceUtils.getIdByName(getContext(), "layout", "sobot_clear_history_msg_popup"));
        initView();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!(event.getX() >= -10 && event.getY() >= -10)
                    || event.getY() <= (screenHeight - coustom_pop_layout.getHeight() - 20)) {//如果点击位置在当前View外部则销毁当前视图,其中10与20为微调距离
                dismiss();
            }
        }
        return true;
    }

    private void initView() {
        sobot_tv_clear_his_msg_describe = (TextView) findViewById(ResourceUtils.getIdByName(getContext(), "id", "sobot_tv_clear_his_msg_describe"));
        sobot_tv_clear_his_msg_describe.setText(ResourceUtils.getResString(getContext(),"sobot_clear_his_msg_describe"));
        sobot_btn_cancle_conversation = (Button) findViewById(ResourceUtils.getIdByName(getContext(), "id", "sobot_btn_cancle_conversation"));
        sobot_btn_cancle_conversation.setText(ResourceUtils.getResString(getContext(),"sobot_clear_his_msg_empty"));
        sobot_btn_temporary_leave = (Button) findViewById(ResourceUtils.getIdByName(getContext(), "id", "sobot_btn_temporary_leave"));
        sobot_btn_temporary_leave.setText(ResourceUtils.getResString(getContext(),"sobot_btn_cancle"));
        coustom_pop_layout = (LinearLayout) findViewById(ResourceUtils.getIdByName(getContext(), "id", "pop_layout"));

        sobot_btn_cancle_conversation.setOnClickListener(itemsOnClick);
        sobot_btn_temporary_leave.setOnClickListener(itemsOnClick);
    }

    private void setParams(Context context, WindowManager.LayoutParams lay) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        Rect rect = new Rect();
        View view = getWindow().getDecorView();
        view.getWindowVisibleDisplayFrame(rect);
        lay.width = dm.widthPixels;
    }
}
