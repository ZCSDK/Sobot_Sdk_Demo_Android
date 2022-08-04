package com.sobot.chat.viewHolder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.api.model.MiniProgramModel;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.listener.NoDoubleClickListener;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.viewHolder.base.MessageHolderBase;
import com.sobot.pictureframe.SobotBitmapUtil;

/**
 * 小程序卡片
 */
public class MiniProgramMessageHolder extends MessageHolderBase implements View.OnClickListener {
    private View mContainer;
    private ImageView tv_mimi_logo;
    private TextView tv_mimi_des;
    private TextView tv_mimi_title;
    private ImageView tv_mimi_thumbUrl;
    private MiniProgramModel miniProgramModel;
    private RelativeLayout sobot_right_empty_rl;//顶踩
    private LinearLayout sobot_chat_more_action;//包含以下所有控件
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
        sobot_chat_more_action = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_chat_more_action"));
        sobot_ll_transferBtn = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_transferBtn"));
        sobot_tv_transferBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_transferBtn"));
        sobot_tv_transferBtn.setText(ResourceUtils.getResString(context, "sobot_transfer_to_customer_service"));
    }

    @Override
    public void bindData(final Context context, final ZhiChiMessageBase message) {
        miniProgramModel = message.getMiniProgramModel();
        if (miniProgramModel != null) {
            if (!TextUtils.isEmpty(miniProgramModel.getLogo())) {
                SobotBitmapUtil.display(mContext, miniProgramModel.getLogo(), tv_mimi_logo);
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
                SobotBitmapUtil.display(mContext, miniProgramModel.getThumbUrl(), tv_mimi_thumbUrl);
                tv_mimi_thumbUrl.setVisibility(View.VISIBLE);
            } else {
                tv_mimi_thumbUrl.setVisibility(View.GONE);
            }
        }
        mContainer.setOnClickListener(this);
        refreshItem();
        checkShowTransferBtn();
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
        sobot_chat_more_action.setVisibility(View.VISIBLE);
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
        sobot_chat_more_action.setVisibility(View.VISIBLE);
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
        sobot_chat_more_action.setVisibility(View.VISIBLE);
        sobot_tv_likeBtn.setVisibility(View.GONE);
        sobot_tv_dislikeBtn.setVisibility(View.VISIBLE);
        sobot_right_empty_rl.setVisibility(View.VISIBLE);
        sobot_ll_likeBtn.setVisibility(View.GONE);
        sobot_ll_dislikeBtn.setVisibility(View.VISIBLE);
    }
}
