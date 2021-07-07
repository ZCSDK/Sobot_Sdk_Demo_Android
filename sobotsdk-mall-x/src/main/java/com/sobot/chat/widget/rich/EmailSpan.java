package com.sobot.chat.widget.rich;

import android.app.Activity;
import android.content.Context;
import androidx.core.app.ShareCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.sobot.chat.utils.SobotOption;

public class EmailSpan extends ClickableSpan {

    private String email;
    private int color;
    private Context context;

    public EmailSpan(Context context, String email, int color) {
        this.email = email;
        this.context=context;
        this.color = context.getResources().getColor(color);
    }

    @Override
    public void onClick(View widget) {
        if (SobotOption.hyperlinkListener != null) {
            SobotOption.hyperlinkListener.onEmailClick(email);
            return;
        }

        if (SobotOption.newHyperlinkListener != null) {
            //如果返回true,拦截;false 不拦截
            boolean isIntercept = SobotOption.newHyperlinkListener.onEmailClick(context,email);
            if (isIntercept) {
                return;
            }
        }
        try {
            ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder
                    .from((Activity) widget.getContext());
            builder.setType("message/rfc822");
            builder.addEmailTo(email);
            builder.setSubject("");
            builder.setChooserTitle("");
            builder.startChooser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(color);
        ds.setUnderlineText(false); // 去掉下划线
    }
}