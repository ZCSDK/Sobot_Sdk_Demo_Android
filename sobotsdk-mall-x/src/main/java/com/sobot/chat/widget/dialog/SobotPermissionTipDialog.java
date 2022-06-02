package com.sobot.chat.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;

/**
 * 权限用途提示弹窗
 * （支付金融等行业app上线审核要求高，需要弹出权限作用提示框）
 */

public class SobotPermissionTipDialog extends Dialog implements View.OnClickListener {

    private Button sobot_btn_cancle_conversation, sobot_btn_temporary_leave;
    private LinearLayout coustom_pop_layout;
    private ClickViewListener viewListenern;
    private final int screenHeight;
    private TextView titleView;
    private TextView contentTV;
    private String title;
    private String mContent;

    public SobotPermissionTipDialog(Activity context, ClickViewListener itemsOnClick) {
        super(context, ResourceUtils.getIdByName(context, "style", "sobot_noAnimDialogStyle"));
        this.viewListenern = itemsOnClick;
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

    public SobotPermissionTipDialog(Activity activity, String title, String content, ClickViewListener clickViewListener) {
        this(activity, clickViewListener);
        this.title = title;
        this.mContent = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ResourceUtils.getIdByName(getContext(), "layout", "sobot_permission_purpose_tip_popup"));
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
        titleView = (TextView) findViewById(ResourceUtils.getIdByName(getContext(), "id", "sobot_dialog_title"));
        if (!TextUtils.isEmpty(title)) {
            titleView.setText("\"" + CommonUtils.getAppName(getContext()) + "\" " + ResourceUtils.getResString(getContext(), "sobot_want_use_your") +  title);
        }
        contentTV = (TextView) findViewById(ResourceUtils.getIdByName(getContext(), "id", "sobot_dialog_content"));
        if (!TextUtils.isEmpty(mContent)) {
            contentTV.setText(mContent);
        }
        sobot_btn_cancle_conversation = (Button) findViewById(ResourceUtils.getIdByName(getContext(), "id", "sobot_btn_left"));
        sobot_btn_cancle_conversation.setText(ResourceUtils.getResString(getContext(), "sobot_btn_cancle"));
        sobot_btn_temporary_leave = (Button) findViewById(ResourceUtils.getIdByName(getContext(), "id", "sobot_btn_right"));
        sobot_btn_temporary_leave.setText(ResourceUtils.getResString(getContext(), "sobot_btn_submit"));
        coustom_pop_layout = (LinearLayout) findViewById(ResourceUtils.getIdByName(getContext(), "id", "pop_layout"));

        sobot_btn_cancle_conversation.setOnClickListener(this);
        sobot_btn_temporary_leave.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        if (v == sobot_btn_cancle_conversation) {
            if (viewListenern != null) {
                viewListenern.clickLeftView(getContext(), this);
            }
        }
        if (v == sobot_btn_temporary_leave) {
            if (viewListenern != null) {
                viewListenern.clickRightView(getContext(), this);
            }
        }

    }

    public interface ClickViewListener {
        void clickRightView(Context context, SobotPermissionTipDialog dialog);

        void clickLeftView(Context context, SobotPermissionTipDialog dialog);
    }
}
