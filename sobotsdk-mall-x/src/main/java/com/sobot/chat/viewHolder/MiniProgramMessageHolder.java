package com.sobot.chat.viewHolder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.R;
import com.sobot.chat.api.model.MiniProgramModel;
import com.sobot.chat.api.model.Suggestions;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.listener.NoDoubleClickListener;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.viewHolder.base.MessageHolderBase;
import com.sobot.pictureframe.SobotBitmapUtil;

import java.util.ArrayList;

/**
 * 小程序卡片
 */
public class MiniProgramMessageHolder extends MessageHolderBase implements View.OnClickListener {
    private LinearLayout answersList;
    private TextView stripe;
    private View mContainer;
    private ImageView tv_mimi_logo;
    private TextView tv_mimi_des;
    private TextView tv_mimi_title;
    private ImageView tv_mimi_thumbUrl;
    private MiniProgramModel miniProgramModel;
    private RelativeLayout sobot_right_empty_rl;//顶踩
    private LinearLayout sobot_ll_transferBtn;//只包含转人工按钮
    private TextView sobot_tv_transferBtn;//机器人转人工按钮

    public MiniProgramMessageHolder(Context context, View convertView) {
        super(context, convertView);
        mContainer = convertView.findViewById(ResourceUtils.getResId(context, "sobot_ll_container"));
        tv_mimi_logo = (ImageView) convertView.findViewById(ResourceUtils.getResId(context, "tv_mimi_logo"));
        tv_mimi_des = (TextView) convertView.findViewById(ResourceUtils.getResId(context, "tv_mimi_des"));
        tv_mimi_title = (TextView) convertView.findViewById(ResourceUtils.getResId(context, "tv_mimi_title"));
        tv_mimi_thumbUrl = (ImageView) convertView.findViewById(ResourceUtils.getResId(context, "tv_mimi_thumbUrl"));
        sobot_right_empty_rl = (RelativeLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_right_empty_rl"));
        sobot_ll_transferBtn = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_transferBtn"));
        sobot_tv_transferBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_transferBtn"));
        sobot_tv_transferBtn.setText(ResourceUtils.getResString(context, "sobot_transfer_to_customer_service"));
        answersList = (LinearLayout) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id",
                        "sobot_answersList"));
        stripe = (TextView) convertView.findViewById(ResourceUtils
                .getIdByName(context, "id", "sobot_stripe"));
    }

    @Override
    public void bindData(final Context context, final ZhiChiMessageBase message) {
        miniProgramModel = message.getMiniProgramModel();
        if (miniProgramModel != null) {
            if (!TextUtils.isEmpty(miniProgramModel.getLogo())) {
                SobotBitmapUtil.display(mContext, miniProgramModel.getLogo(), tv_mimi_logo, R.drawable.sobot_default_pic, R.drawable.sobot_default_pic_err);
                tv_mimi_logo.setVisibility(View.VISIBLE);
            } else {
                tv_mimi_logo.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(miniProgramModel.getDescribe())) {
                tv_mimi_des.setText(miniProgramModel.getDescribe());
                tv_mimi_des.setVisibility(View.VISIBLE);
            } else {
                tv_mimi_des.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(miniProgramModel.getTitle())) {
                tv_mimi_title.setText(miniProgramModel.getTitle());
                tv_mimi_title.setVisibility(View.VISIBLE);
            } else {
                tv_mimi_title.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(miniProgramModel.getThumbUrl())) {
                SobotBitmapUtil.display(mContext, miniProgramModel.getThumbUrl(), tv_mimi_thumbUrl, R.drawable.sobot_default_pic, R.drawable.sobot_default_pic_err);
                tv_mimi_thumbUrl.setVisibility(View.VISIBLE);
            } else {
                tv_mimi_thumbUrl.setVisibility(View.GONE);
            }
        }
        mContainer.setOnClickListener(this);
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

    private void resetMaxWidth() {
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
    }

    @Override
    public void onClick(View v) {
        if (v == mContainer && miniProgramModel != null) {
            if (SobotOption.miniProgramClickListener != null) {
                SobotOption.miniProgramClickListener.onClick(mContext, miniProgramModel);
            } else {
                ToastUtil.showToast(mContext, ResourceUtils.getResString(mContext, "sobot_mini_program_only_open_by_weixin"));
            }
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
            sobot_ll_transferBtn.setVisibility(View.GONE);
        } else {
            sobot_ll_transferBtn.setVisibility(View.VISIBLE);
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
                sobot_ll_dislikeBtn == null || sobot_tv_bottom_likeBtn == null ||
                sobot_tv_bottom_dislikeBtn == null ||
                sobot_ll_bottom_likeBtn == null ||
                sobot_ll_bottom_dislikeBtn == null) {
            return;
        }
        try {
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
        } catch (Exception e) {
        }
    }

    /**
     * 显示 顶踩 按钮
     */
    public void showRevaluateBtn() {
        if (dingcaiIsShowRight()) {
            sobot_tv_likeBtn.setVisibility(View.VISIBLE);
            sobot_tv_dislikeBtn.setVisibility(View.VISIBLE);
            sobot_ll_likeBtn.setVisibility(View.VISIBLE);
            sobot_ll_dislikeBtn.setVisibility(View.VISIBLE);
            sobot_right_empty_rl.setVisibility(View.VISIBLE);
            sobot_tv_bottom_likeBtn.setVisibility(View.GONE);
            sobot_tv_bottom_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_bottom_likeBtn.setVisibility(View.GONE);
            sobot_ll_bottom_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_likeBtn.setBackground(mContext.getResources().getDrawable(R.drawable.sobot_chat_dingcai_right_def));
            sobot_ll_dislikeBtn.setBackground(mContext.getResources().getDrawable(R.drawable.sobot_chat_dingcai_right_def));
        } else {
            sobot_tv_bottom_likeBtn.setVisibility(View.VISIBLE);
            sobot_tv_bottom_dislikeBtn.setVisibility(View.VISIBLE);
            sobot_ll_bottom_likeBtn.setVisibility(View.VISIBLE);
            sobot_ll_bottom_dislikeBtn.setVisibility(View.VISIBLE);
            sobot_tv_likeBtn.setVisibility(View.GONE);
            sobot_tv_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_likeBtn.setVisibility(View.GONE);
            sobot_ll_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_bottom_likeBtn.setBackground(mContext.getResources().getDrawable(R.drawable.sobot_chat_dingcai_bottom_def));
            sobot_ll_bottom_dislikeBtn.setBackground(mContext.getResources().getDrawable(R.drawable.sobot_chat_dingcai_bottom_def));
        }
        sobot_tv_likeBtn.setEnabled(true);
        sobot_tv_dislikeBtn.setEnabled(true);
        sobot_tv_likeBtn.setSelected(false);
        sobot_tv_dislikeBtn.setSelected(false);

        sobot_tv_bottom_likeBtn.setEnabled(true);
        sobot_tv_bottom_dislikeBtn.setEnabled(true);
        sobot_tv_bottom_likeBtn.setSelected(false);
        sobot_tv_bottom_dislikeBtn.setSelected(false);

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

        sobot_tv_bottom_likeBtn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                doRevaluate(true);
            }
        });
        sobot_tv_bottom_dislikeBtn.setOnClickListener(new NoDoubleClickListener() {
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
        sobot_tv_bottom_likeBtn.setVisibility(View.GONE);
        sobot_tv_bottom_dislikeBtn.setVisibility(View.GONE);
        sobot_ll_bottom_likeBtn.setVisibility(View.GONE);
        sobot_ll_bottom_dislikeBtn.setVisibility(View.GONE);
    }

    /**
     * 显示顶之后的view
     */
    public void showLikeWordView() {
        if (dingcaiIsShowRight()) {
            sobot_tv_likeBtn.setSelected(true);
            sobot_tv_likeBtn.setEnabled(false);
            sobot_tv_dislikeBtn.setEnabled(false);
            sobot_tv_dislikeBtn.setSelected(false);
            sobot_tv_likeBtn.setVisibility(View.VISIBLE);
            sobot_tv_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_likeBtn.setVisibility(View.VISIBLE);
            sobot_right_empty_rl.setVisibility(View.VISIBLE);
            sobot_ll_dislikeBtn.setVisibility(View.GONE);
            sobot_tv_bottom_likeBtn.setVisibility(View.GONE);
            sobot_tv_bottom_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_bottom_likeBtn.setVisibility(View.GONE);
            sobot_ll_bottom_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_likeBtn.setBackground(mContext.getResources().getDrawable(R.drawable.sobot_chat_dingcai_right_sel));
        } else {
            sobot_tv_bottom_likeBtn.setSelected(true);
            sobot_tv_bottom_likeBtn.setEnabled(false);
            sobot_tv_bottom_dislikeBtn.setEnabled(false);
            sobot_tv_bottom_dislikeBtn.setSelected(false);
            sobot_tv_bottom_likeBtn.setVisibility(View.VISIBLE);
            sobot_tv_bottom_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_bottom_likeBtn.setVisibility(View.VISIBLE);
            sobot_ll_bottom_dislikeBtn.setVisibility(View.GONE);
            sobot_tv_likeBtn.setVisibility(View.GONE);
            sobot_tv_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_likeBtn.setVisibility(View.GONE);
            sobot_ll_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_bottom_likeBtn.setBackground(mContext.getResources().getDrawable(R.drawable.sobot_chat_dingcai_bottom_sel));
        }
    }

    /**
     * 显示踩之后的view
     */
    public void showDislikeWordView() {
        if (dingcaiIsShowRight()) {
            sobot_tv_dislikeBtn.setSelected(true);
            sobot_tv_dislikeBtn.setEnabled(false);
            sobot_tv_likeBtn.setEnabled(false);
            sobot_tv_likeBtn.setSelected(false);
            sobot_tv_likeBtn.setVisibility(View.GONE);
            sobot_tv_dislikeBtn.setVisibility(View.VISIBLE);
            sobot_right_empty_rl.setVisibility(View.VISIBLE);
            sobot_ll_likeBtn.setVisibility(View.GONE);
            sobot_ll_dislikeBtn.setVisibility(View.VISIBLE);
            sobot_tv_bottom_likeBtn.setVisibility(View.GONE);
            sobot_tv_bottom_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_bottom_likeBtn.setVisibility(View.GONE);
            sobot_ll_bottom_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_dislikeBtn.setBackground(mContext.getResources().getDrawable(R.drawable.sobot_chat_dingcai_right_sel));
        } else {
            sobot_tv_bottom_dislikeBtn.setSelected(true);
            sobot_tv_bottom_dislikeBtn.setEnabled(false);
            sobot_tv_bottom_likeBtn.setEnabled(false);
            sobot_tv_bottom_likeBtn.setSelected(false);
            sobot_tv_bottom_likeBtn.setVisibility(View.GONE);
            sobot_tv_bottom_dislikeBtn.setVisibility(View.VISIBLE);
            sobot_ll_bottom_likeBtn.setVisibility(View.GONE);
            sobot_ll_bottom_dislikeBtn.setVisibility(View.VISIBLE);
            sobot_tv_likeBtn.setVisibility(View.GONE);
            sobot_tv_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_likeBtn.setVisibility(View.GONE);
            sobot_ll_dislikeBtn.setVisibility(View.GONE);
            sobot_ll_bottom_dislikeBtn.setBackground(mContext.getResources().getDrawable(R.drawable.sobot_chat_dingcai_bottom_sel));
        }
    }
}
