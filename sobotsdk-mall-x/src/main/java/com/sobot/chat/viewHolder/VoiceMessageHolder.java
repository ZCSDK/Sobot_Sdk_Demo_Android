package com.sobot.chat.viewHolder;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sobot.chat.SobotUIConfig;
import com.sobot.chat.adapter.SobotMsgAdapter;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.api.model.ZhiChiReplyAnswer;
import com.sobot.chat.utils.DateUtil;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

/**
 * 语音条目
 * Created by jinxl on 2017/3/17.
 */
public class VoiceMessageHolder extends MessageHolderBase {
    TextView voiceTimeLong;
    ImageView voicePlay;
    LinearLayout ll_voice_layout;
    public ZhiChiMessageBase message;
    public VoiceMessageHolder(Context context, View convertView) {
        super(context, convertView);
        voicePlay = (ImageView) convertView.findViewById(ResourceUtils
                .getIdByName(context, "id", "sobot_iv_voice"));
        voiceTimeLong = (TextView) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id",
                        "sobot_voiceTimeLong"));
        ll_voice_layout = (LinearLayout) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id",
                        "sobot_ll_voice_layout"));

        if (ll_voice_layout != null && SobotUIConfig.DEFAULT != SobotUIConfig.sobot_chat_right_bgColor){
            ScreenUtils.setBubbleBackGroud(mContext, ll_voice_layout, SobotUIConfig.sobot_chat_right_bgColor);
        }

        msgProgressBar = (ProgressBar) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id",
                        "sobot_msgProgressBar"));
    }

    @Override
    public void bindData(final Context context, final ZhiChiMessageBase message) {
        this.message = message;
        voiceTimeLong.setText(message.getAnswer().getDuration() == null ?
                "" : (DateUtil.stringToLongMs(message.getAnswer().getDuration()) + "″"));

        applyTextViewUIConfig(voiceTimeLong);
        checkBackground();
        ll_voice_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(msgCallBack != null){
                    msgCallBack.clickAudioItem(message);
                }
            }
        });

        if (isRight) {
            if (message.getSendSuccessState() == ZhiChiConstant.MSG_SEND_STATUS_SUCCESS) {
                msgStatus.setVisibility(View.GONE);
                msgProgressBar.setVisibility(View.GONE);
                voiceTimeLong.setVisibility(View.VISIBLE);
                voicePlay.setVisibility(View.VISIBLE);
            } else if (message.getSendSuccessState() == ZhiChiConstant.MSG_SEND_STATUS_ERROR) {
                msgStatus.setVisibility(View.VISIBLE);
                msgProgressBar.setVisibility(View.GONE);
                voicePlay.setVisibility(View.VISIBLE);
                voiceTimeLong.setVisibility(View.VISIBLE);
                stopAnim();
                // 语音的重新发送
                msgStatus.setOnClickListener(new RetrySendVoiceLisenter(context, message.getId(),
                        message.getAnswer().getMsg(), message.getAnswer().getDuration(), msgStatus,msgCallBack));
            } else if (message.getSendSuccessState() == ZhiChiConstant.MSG_SEND_STATUS_LOADING) {// 发送中
                msgProgressBar.setVisibility(View.VISIBLE);
                msgStatus.setVisibility(View.GONE);
                voiceTimeLong.setVisibility(View.GONE);
                voicePlay.setVisibility(View.GONE);
            } else if (message.getSendSuccessState() == ZhiChiConstant.MSG_SEND_STATUS_ANIM) {
                msgProgressBar.setVisibility(View.GONE);
                msgStatus.setVisibility(View.GONE);
                voiceTimeLong.setVisibility(View.GONE);
                voicePlay.setVisibility(View.GONE);
            }

            //根据语音长短设置长度
            long duration = DateUtil.stringToLongMs(message.getAnswer().getDuration());
            duration = duration == 0 ? 1 : duration;
            int min = ScreenUtils.getScreenWidth((Activity) context) / 5;
            int max = ScreenUtils.getScreenWidth((Activity) context) * 3 / 5;
            int step = (int) ((duration < 10) ? duration : (duration / 10 + 9));
            ll_voice_layout.getLayoutParams().width = (step == 0) ? min
                    : (min + (max - min) / 15 * step);
        }
    }

    public void checkBackground() {
        if (message.isVoideIsPlaying()) {
            resetAnim();
        } else {
            voicePlay.setImageResource(isRight ? ResourceUtils.getIdByName(mContext, "drawable", "sobot_pop_voice_send_anime_3") :
                    ResourceUtils.getIdByName(mContext, "drawable", "sobot_pop_voice_receive_anime_3"));
        }
    }

    private void resetAnim() {
        voicePlay.setImageResource(isRight ? ResourceUtils.getIdByName(mContext, "drawable", "sobot_voice_to_icon") :
                ResourceUtils.getIdByName(mContext, "drawable", "sobot_voice_from_icon"));
        Drawable playDrawable = voicePlay.getDrawable();
        if (playDrawable != null
                && playDrawable instanceof AnimationDrawable) {
            ((AnimationDrawable) playDrawable).start();
        }
    }

    // 开始播放
    public void startAnim() {
        message.setVoideIsPlaying(true);

        Drawable playDrawable = voicePlay.getDrawable();
        if (playDrawable instanceof AnimationDrawable) {
            ((AnimationDrawable) playDrawable).start();
        } else {
            resetAnim();
        }
    }

    // 关闭播放
    public void stopAnim() {
        message.setVoideIsPlaying(false);

        Drawable playDrawable = voicePlay.getDrawable();
        if (playDrawable != null
                && playDrawable instanceof AnimationDrawable) {
            ((AnimationDrawable) playDrawable).stop();
            ((AnimationDrawable) playDrawable).selectDrawable(2);
        }
    }

    // 语音的重新发送
    public static class RetrySendVoiceLisenter implements View.OnClickListener {
        private String voicePath;
        private String id;
        private String duration;
        private ImageView img;
        private Context context;
        private SobotMsgAdapter.SobotMsgCallBack msgCallBack;

        public RetrySendVoiceLisenter(Context context,String id, String voicePath,
                                      String duration, ImageView image,SobotMsgAdapter.SobotMsgCallBack msgCallBack) {
            super();
            this.context = context;
            this.msgCallBack = msgCallBack;
            this.voicePath = voicePath;
            this.id = id;
            this.duration = duration;
            this.img = image;
        }

        @Override
        public void onClick(View arg0) {

            if (img != null) {
                img.setClickable(false);
            }
            showReSendVoiceDialog(context,voicePath, id, duration,img);
        }

        private void showReSendVoiceDialog(final Context context,final String mvoicePath,
                                                  final String mid, final String mduration,final ImageView msgStatus) {
            showReSendDialog(context,msgStatus,new ReSendListener(){

                @Override
                public void onReSend() {
                    if (context != null){
                        ZhiChiMessageBase msgObj = new ZhiChiMessageBase();
                        ZhiChiReplyAnswer answer = new ZhiChiReplyAnswer();
                        answer.setDuration(mduration);
                        msgObj.setContent(mvoicePath);
                        msgObj.setId(mid);
                        msgObj.setAnswer(answer);
                        if (msgCallBack != null) {
                            msgCallBack.sendMessageToRobot(msgObj,2 , 3,"");
                        }
                    }
                }
            });
        }
    }
}