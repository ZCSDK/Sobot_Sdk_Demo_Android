package com.sobot.chat.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
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

import com.sobot.chat.R;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.ZhiChiConstant;

import java.util.Locale;

public class ClearHistoryDialog extends Dialog implements View.OnClickListener {

    private Button sobot_btn_take_photo, sobot_btn_cancel;
    private DialogOnClickListener listener;
    private LinearLayout sobot_pop_layout;
    private final int screenHeight;

    public void changeAppLanguage() {
        Locale language = (Locale) SharedPreferencesUtil.getObject(getContext(), ZhiChiConstant.SOBOT_LANGUAGE);
        try {
            if (language != null) {
                // 本地语言设置
                Resources res = getContext().getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = new Configuration();
                conf.locale = language;
                res.updateConfiguration(conf, dm);
            }
        } catch (Exception e) {
        }
    }

    public ClearHistoryDialog(Activity context) {
        // 给Dialog的Window设置样式
        super(context, ResourceUtils.getIdByName(context, "style", "sobot_clearHistoryDialogStyle"));
        // 修改Dialog(Window)的弹出位置
        screenHeight = getScreenHeight(context);
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            setParams(context, layoutParams);
            window.setAttributes(layoutParams);
        }
    }

    private void setParams(Context context, WindowManager.LayoutParams lay) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        Rect rect = new Rect();
        if (getWindow() != null) {
            View view = getWindow().getDecorView();
            view.getWindowVisibleDisplayFrame(rect);
            lay.width = dm.widthPixels;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!(event.getX() >= -10 && event.getY() >= -10)
                    || event.getY() <= (screenHeight - sobot_pop_layout.getHeight() - 20)) {//如果点击位置在当前View外部则销毁当前视图,其中10与20为微调距离
                dismiss();
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeAppLanguage();
        setContentView(R.layout.sobot_clear_history_dialog);
        initView();
        initData();
    }

    private void initData() {

    }

    private void initView() {
        sobot_btn_take_photo = (Button) findViewById(ResourceUtils.getIdByName(getContext(), "id",
                "sobot_btn_take_photo"));
        sobot_btn_cancel = (Button) findViewById(ResourceUtils.getIdByName(getContext(), "id",
                "sobot_btn_cancel"));
        sobot_pop_layout = (LinearLayout) findViewById(ResourceUtils.getIdByName(getContext(), "id",
                "sobot_pop_layout"));
        sobot_btn_take_photo.setTextColor(getContext().getResources()
                .getColor(ResourceUtils.getIdByName(getContext(), "color",
                        "sobot_text_delete_hismsg_color")));
        // 使用context直接获取字符串
        sobot_btn_take_photo.setText(getContext().getString(R.string.sobot_clear_history_message));
        sobot_btn_cancel.setText(getContext().getString(R.string.sobot_btn_cancle));
        sobot_btn_take_photo.setOnClickListener(this);
        sobot_btn_cancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        this.dismiss();
        if (v == sobot_btn_take_photo) {
            if (listener != null) {
                listener.onSure();
            }
        }
    }

    public void setOnClickListener(DialogOnClickListener listener) {
        this.listener = listener;
    }

    public interface DialogOnClickListener {
        void onSure();
    }

    public static int getScreenHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }
}