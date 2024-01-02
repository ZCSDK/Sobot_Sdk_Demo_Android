package com.sobot.chat.viewHolder;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.R;
import com.sobot.chat.api.model.SobotMultiDiaRespInfo;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.listener.NoDoubleClickListener;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

import java.util.List;
import java.util.Map;

public class RobotTemplateMessageHolder6 extends MessageHolderBase {

    private TextView sobot_template6_title;
    private TextView sobot_template6_msg;
    public ZhiChiMessageBase message;
    private LinearLayout sobot_ll_transferBtn;//只包含转人工按钮
    private TextView sobot_tv_transferBtn;//机器人转人工按钮
    public RobotTemplateMessageHolder6(Context context, View convertView) {
        super(context, convertView);
        sobot_template6_msg = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template6_msg"));
        sobot_template6_title = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template6_title"));
        sobot_ll_transferBtn = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_transferBtn"));
        sobot_tv_transferBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_transferBtn"));
        sobot_tv_transferBtn.setText(ResourceUtils.getResString(context, "sobot_transfer_to_customer_service"));
        //102=左间距12+内间距30+右间距60
        sobot_template6_title.setMaxWidth(ScreenUtils.getScreenWidth((Activity) mContext) - ScreenUtils.dip2px(mContext, 102));
        sobot_template6_msg.setMaxWidth(ScreenUtils.getScreenWidth((Activity) mContext) - ScreenUtils.dip2px(mContext, 102));
    }


    @Override
    public void bindData(final Context context, final ZhiChiMessageBase message) {
        this.message = message;
        if (message.getAnswer() != null && message.getAnswer().getMultiDiaRespInfo() != null) {
            checkShowTransferBtn();
            final SobotMultiDiaRespInfo multiDiaRespInfo = message.getAnswer().getMultiDiaRespInfo();
            HtmlTools.getInstance(context).setRichText(sobot_template6_msg, ChatUtils.getMultiMsgTitle(multiDiaRespInfo).replaceAll("\n", "<br/>"), getLinkTextColor());
            applyTextViewUIConfig(sobot_template6_msg);
            final List<Map<String, String>> interfaceRetList = multiDiaRespInfo.getInterfaceRetList();
            if ("000000".equals(multiDiaRespInfo.getRetCode()) && interfaceRetList != null && interfaceRetList.size() > 0) {
                final Map<String, String> interfaceRet = interfaceRetList.get(0);
                if (interfaceRet != null && interfaceRet.size() > 0) {
                    setSuccessView();
                    if (TextUtils.isEmpty(interfaceRet.get("tempStr"))) {
                        sobot_template6_title.setVisibility(View.GONE);
                    } else {
                        sobot_template6_title.setVisibility(View.VISIBLE);
                        HtmlTools.getInstance(context).setRichText(sobot_template6_title, interfaceRet.get("tempStr").replace("<div>", "").replace("</div>", "").replace("<p>", "").replace("</p>", "<br/>"), getLinkTextColor());
                    }
                }
            } else {
                setFailureView();
            }
        }
        refreshRevaluateItem();//左侧消息刷新顶和踩布局
    }


    private void setSuccessView() {
        sobot_template6_msg.setVisibility(View.VISIBLE);
        sobot_template6_title.setVisibility(View.VISIBLE);
    }

    private void setFailureView() {
        sobot_template6_msg.setVisibility(View.VISIBLE);
        sobot_template6_title.setVisibility(View.GONE);
    }

    private void checkShowTransferBtn() {
        if (message.getTransferType() == 4) {
            //4 多次命中 显示转人工
            showTransferBtn();
        } else {
            //隐藏转人工
            hideTransferBtn();
        }
    }


    /**
     * 隐藏转人工按钮
     */
    public void hideTransferBtn() {
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

    public void refreshRevaluateItem() {
        if (message == null) {
            return;
        }
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
            rightEmptyRL.setVisibility(View.VISIBLE);
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
        sobot_tv_likeBtn.setVisibility(View.GONE);
        sobot_tv_dislikeBtn.setVisibility(View.GONE);
        sobot_ll_likeBtn.setVisibility(View.GONE);
        rightEmptyRL.setVisibility(View.GONE);
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
            rightEmptyRL.setVisibility(View.VISIBLE);
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
            rightEmptyRL.setVisibility(View.VISIBLE);
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