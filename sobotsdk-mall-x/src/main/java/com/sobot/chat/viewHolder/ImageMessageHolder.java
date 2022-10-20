package com.sobot.chat.viewHolder;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.adapter.SobotMsgAdapter;
import com.sobot.chat.api.model.Suggestions;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.listener.NoDoubleClickListener;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.viewHolder.base.MessageHolderBase;
import com.sobot.chat.widget.RoundProgressBar;
import com.sobot.chat.widget.image.SobotRCImageView;
import com.sobot.pictureframe.SobotBitmapUtil;

import java.util.ArrayList;

/**
 * 图片消息
 * Created by jinxl on 2017/3/17.
 */
public class ImageMessageHolder extends MessageHolderBase {
    private TextView stripe;
    private LinearLayout answersList;
    private RelativeLayout sobot_rl_real_pic;
    SobotRCImageView image;
    ImageView pic_send_status;
    public ProgressBar pic_progress;
    public RoundProgressBar sobot_pic_progress_round;
    TextView isGif;
    RelativeLayout sobot_pic_progress_rl;

    private RelativeLayout sobot_right_empty_rl;//顶踩
    private LinearLayout sobot_chat_more_action;//包含以下所有控件
    private LinearLayout sobot_ll_transferBtn;//只包含转人工按钮
    private TextView sobot_tv_transferBtn;//机器人转人工按钮

    public ImageMessageHolder(Context context, View convertView){
        super(context,convertView);
        isGif = (TextView) convertView.findViewById(ResourceUtils
                .getIdByName(context, "id", "sobot_pic_isgif"));
        image = (SobotRCImageView) convertView.findViewById(ResourceUtils
                .getIdByName(context, "id", "sobot_iv_picture"));

        pic_send_status = (ImageView) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id",
                        "sobot_pic_send_status"));
        pic_progress = (ProgressBar) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id",
                        "sobot_pic_progress"));
        sobot_pic_progress_round = (RoundProgressBar) convertView.findViewById(ResourceUtils.getIdByName(context, "id",
                "sobot_pic_progress_round"));
        stripe = (TextView) convertView.findViewById(ResourceUtils
                .getIdByName(context, "id", "sobot_stripe"));
        answersList = (LinearLayout) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id",
                        "sobot_answersList"));
        sobot_rl_real_pic= (RelativeLayout) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id",
                        "sobot_rl_real_pic"));
        sobot_pic_progress_rl = (RelativeLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_pic_progress_rl"));
        sobot_right_empty_rl = (RelativeLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_right_empty_rl"));
        sobot_chat_more_action = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_chat_more_action"));
        sobot_ll_transferBtn = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_transferBtn"));
        sobot_tv_transferBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_transferBtn"));
        sobot_tv_transferBtn.setText(ResourceUtils.getResString(context, "sobot_transfer_to_customer_service"));
    }

    @Override
    public void bindData(final Context context,final ZhiChiMessageBase message) {
        isGif.setVisibility(View.GONE);
        image.setVisibility(View.VISIBLE);
        if(isRight){
            sobot_pic_progress_round.setVisibility(View.GONE);
            sobot_pic_progress_rl.setVisibility(View.VISIBLE);
            if (ZhiChiConstant.MSG_SEND_STATUS_ERROR == message.getSendSuccessState()) {
                pic_send_status.setVisibility(View.VISIBLE);
                pic_progress.setVisibility(View.GONE);
                sobot_pic_progress_round.setVisibility(View.GONE);
                sobot_pic_progress_rl.setVisibility(View.GONE);
                // 点击重新发送按钮
                pic_send_status.setOnClickListener(new RetrySendImageLisenter(context,message
                        .getId(), message.getAnswer().getMsg(), pic_send_status,msgCallBack));
            } else if (ZhiChiConstant.MSG_SEND_STATUS_SUCCESS == message.getSendSuccessState()) {
                pic_send_status.setVisibility(View.GONE);
                pic_progress.setVisibility(View.GONE);
                sobot_pic_progress_round.setVisibility(View.GONE);
                sobot_pic_progress_rl.setVisibility(View.GONE);
            } else if (ZhiChiConstant.MSG_SEND_STATUS_LOADING == message.getSendSuccessState()) {
                pic_progress.setVisibility(View.VISIBLE);
                pic_send_status.setVisibility(View.GONE);
            } else {
                pic_send_status.setVisibility(View.GONE);
                pic_progress.setVisibility(View.GONE);
                sobot_pic_progress_round.setVisibility(View.GONE);
                sobot_pic_progress_rl.setVisibility(View.GONE);
            }
        }else{
            if (message.getSugguestions() != null && message.getSugguestions().length > 0) {
                resetAnswersList();
                if (stripe != null) {
                    // 回复语的答复
                    String stripeContent = message.getStripe() != null ? message.getStripe().trim() : "";
                    if (!TextUtils.isEmpty(stripeContent)) {
                        //去掉p标签
                        stripeContent = stripeContent.replace("<p>", "").replace("</p>", "");
                        // 设置提醒的内容
                        stripe.setVisibility(View.VISIBLE);
                        HtmlTools.getInstance(context).setRichText(stripe, stripeContent, getLinkTextColor());
                    } else {
                        stripe.setText(null);
                        stripe.setVisibility(View.GONE);
                    }
                }
            } else {
                answersList.setVisibility(View.GONE);
            }
            refreshItem();
            checkShowTransferBtn();
        }

//        String picPath = message.getAnswer().getMsg();
//        if(!TextUtils.isEmpty(picPath) && (picPath.endsWith("gif") || picPath.endsWith("GIF"))){
//            isGif.setVisibility(View.VISIBLE);
//        }else{
//            isGif.setVisibility(View.GONE);
//        }
        SobotBitmapUtil.display(context, message.getAnswer().getMsg(), image);
        image.setOnClickListener(new ImageClickLisenter(context,message.getAnswer().getMsg(), isRight));
    }

    //设置问题列表
    private void resetAnswersList() {
        if (message == null) {
            return;
        }
        if (message.getListSuggestions() != null && message.getListSuggestions().size() > 0) {
            ArrayList<Suggestions> listSuggestions = message.getListSuggestions();
            answersList.removeAllViews();
            answersList.setVisibility(View.VISIBLE);
            int startNum = 0;
            int endNum = listSuggestions.size();
            if (message.isGuideGroupFlag() && message.getGuideGroupNum() > -1) {//有分组且不是全部
                startNum = message.getCurrentPageNum() * message.getGuideGroupNum();
                endNum = Math.min(startNum + message.getGuideGroupNum(), listSuggestions.size());
            }
            for (int i = startNum; i < endNum; i++) {
                TextView answer = ChatUtils.initAnswerItemTextView(mContext, false);
                int currentItem = i + 1;
                answer.setOnClickListener(new RichTextMessageHolder.AnsWerClickLisenter(mContext, null,
                        listSuggestions.get(i).getQuestion(), null, listSuggestions.get(i).getDocId(), msgCallBack));
                String tempStr = processPrefix(message, currentItem) + listSuggestions.get(i).getQuestion();
                answer.setText(tempStr);
                answersList.addView(answer);
            }
        } else {
            String[] answerStringList = message.getSugguestions();
            answersList.removeAllViews();
            answersList.setVisibility(View.VISIBLE);
            for (int i = 0; i < answerStringList.length; i++) {
                TextView answer = ChatUtils.initAnswerItemTextView(mContext, true);
                int currentItem = i + 1;
                String tempStr = processPrefix(message, currentItem) + answerStringList[i];
                answer.setText(tempStr);
                answersList.addView(answer);
            }
        }
        resetMaxWidth();
    }

    private int msgMaxWidth;//气泡最大宽度

    private void resetMaxWidth() {
        //102=左间距12+内间距30+右间距60
        msgMaxWidth = ScreenUtils.getScreenWidth((Activity) mContext) - ScreenUtils.dip2px(mContext, 102);
        if (answersList != null) {
            ViewGroup.LayoutParams layoutParams = answersList.getLayoutParams();
            layoutParams.width = msgMaxWidth;
            answersList.setLayoutParams(layoutParams);
        }
        if (stripe != null) {
            ViewGroup.LayoutParams stripelayoutParams = stripe.getLayoutParams();
            stripelayoutParams.width = msgMaxWidth;
            stripe.setLayoutParams(stripelayoutParams);
        }
        if (sobot_rl_real_pic != null) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) sobot_rl_real_pic.getLayoutParams();
            lp.setMargins(ScreenUtils.dip2px(mContext, 15), ScreenUtils.dip2px(mContext, 15), ScreenUtils.dip2px(mContext, 15), 0);
        }
    }

    // 图片的重新发送监听
    public static class RetrySendImageLisenter implements View.OnClickListener {
        private String id;
        private String imageUrl;
        private ImageView img;

        private Context context;
        SobotMsgAdapter.SobotMsgCallBack mMsgCallBack;

        public RetrySendImageLisenter(final Context context,String id, String imageUrl,
                                      ImageView image,final SobotMsgAdapter.SobotMsgCallBack msgCallBack) {
            super();
            this.id = id;
            this.imageUrl = imageUrl;
            this.img = image;
            this.context = context;
            mMsgCallBack = msgCallBack;
        }

        @Override
        public void onClick(View view) {

            if (img != null) {
                img.setClickable(false);
            }
            showReSendPicDialog(context, imageUrl, id,img);
        }

        private void showReSendPicDialog(final Context context, final String mimageUrl, final String mid, final ImageView msgStatus) {

            showReSendDialog(context,msgStatus,new ReSendListener(){

                @Override
                public void onReSend() {
                    // 获取图片的地址url
                    // 上传url
                    // 采用广播进行重发
                    if (context != null) {
                        ZhiChiMessageBase msgObj = new ZhiChiMessageBase();
                        msgObj.setContent(mimageUrl);
                        msgObj.setId(mid);
                        msgObj.setSendSuccessState(ZhiChiConstant.MSG_SEND_STATUS_LOADING);
                        if(mMsgCallBack != null){
                            mMsgCallBack.sendMessageToRobot(msgObj, 3, 3, "");
                        }
                    }
                }
            });
        }
    }
    private void checkShowTransferBtn() {
        if (message.isShowTransferBtn()) {
            showTransferBtn();
        } else {
            hideTransferBtn();
        }
    }

    private void hideContainer() {
        if (!message.isShowTransferBtn()) {
            sobot_chat_more_action.setVisibility(View.GONE);
        } else {
            sobot_chat_more_action.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏转人工按钮
     */
    public void hideTransferBtn() {
        hideContainer();
        sobot_ll_transferBtn.setVisibility(View.GONE);
        sobot_tv_transferBtn.setVisibility(View.GONE);
        if (message != null) {
            message.setShowTransferBtn(false);
        }
    }

    /**
     * 显示转人工按钮
     */
    public void showTransferBtn() {
        sobot_chat_more_action.setVisibility(View.VISIBLE);
        sobot_tv_transferBtn.setVisibility(View.VISIBLE);
        sobot_ll_transferBtn.setVisibility(View.VISIBLE);
        if (message != null) {
            message.setShowTransferBtn(true);
        }
        sobot_ll_transferBtn.setOnClickListener(new NoDoubleClickListener() {

            @Override
            public void onNoDoubleClick(View v) {
                if (msgCallBack != null) {
                    msgCallBack.doClickTransferBtn(message);
                }
            }
        });
    }

    public void refreshItem() {
        //找不到顶和踩就返回
        if (sobot_tv_likeBtn == null ||
                sobot_tv_dislikeBtn == null ||
                sobot_ll_likeBtn == null ||
                sobot_ll_dislikeBtn == null) {
            return;
        }
        //顶 踩的状态 0 不显示顶踩按钮  1显示顶踩 按钮  2 显示顶之后的view  3显示踩之后view
        switch (message.getRevaluateState()) {
            case 1:
                showRevaluateBtn();
                break;
            case 2:
                showLikeWordView();
                break;
            case 3:
                showDislikeWordView();
                break;
            default:
                hideRevaluateBtn();
                break;
        }
    }

    /**
     * 显示 顶踩 按钮
     */
    public void showRevaluateBtn() {
        sobot_tv_likeBtn.setVisibility(View.VISIBLE);
        sobot_tv_dislikeBtn.setVisibility(View.VISIBLE);
        sobot_ll_likeBtn.setVisibility(View.VISIBLE);
        sobot_ll_dislikeBtn.setVisibility(View.VISIBLE);
        sobot_right_empty_rl.setVisibility(View.VISIBLE);
        sobot_tv_likeBtn.setEnabled(true);
        sobot_tv_dislikeBtn.setEnabled(true);
        sobot_tv_likeBtn.setSelected(false);
        sobot_tv_dislikeBtn.setSelected(false);
        sobot_tv_likeBtn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                doRevaluate(true);
            }
        });
        sobot_tv_dislikeBtn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                doRevaluate(false);
            }
        });
    }

    /**
     * 顶踩 操作
     *
     * @param revaluateFlag true 顶  false 踩
     */
    private void doRevaluate(boolean revaluateFlag) {
        if (msgCallBack != null) {
            msgCallBack.doRevaluate(revaluateFlag, message);
        }
    }

    /**
     * 隐藏 顶踩 按钮
     */
    public void hideRevaluateBtn() {
        hideContainer();
        sobot_tv_likeBtn.setVisibility(View.GONE);
        sobot_tv_dislikeBtn.setVisibility(View.GONE);
        sobot_ll_likeBtn.setVisibility(View.GONE);
        sobot_right_empty_rl.setVisibility(View.GONE);
        sobot_ll_dislikeBtn.setVisibility(View.GONE);
    }

    /**
     * 显示顶之后的view
     */
    public void showLikeWordView() {
        sobot_tv_likeBtn.setSelected(true);
        sobot_tv_likeBtn.setEnabled(false);
        sobot_tv_dislikeBtn.setEnabled(false);
        sobot_tv_dislikeBtn.setSelected(false);
        sobot_tv_likeBtn.setVisibility(View.VISIBLE);
        sobot_tv_dislikeBtn.setVisibility(View.GONE);
        sobot_ll_likeBtn.setVisibility(View.VISIBLE);
        sobot_right_empty_rl.setVisibility(View.VISIBLE);
        sobot_ll_dislikeBtn.setVisibility(View.GONE);
    }

    /**
     * 显示踩之后的view
     */
    public void showDislikeWordView() {
        sobot_tv_dislikeBtn.setSelected(true);
        sobot_tv_dislikeBtn.setEnabled(false);
        sobot_tv_likeBtn.setEnabled(false);
        sobot_tv_likeBtn.setSelected(false);
        sobot_tv_likeBtn.setVisibility(View.GONE);
        sobot_tv_dislikeBtn.setVisibility(View.VISIBLE);
        sobot_right_empty_rl.setVisibility(View.VISIBLE);
        sobot_ll_likeBtn.setVisibility(View.GONE);
        sobot_ll_dislikeBtn.setVisibility(View.VISIBLE);
    }
}
