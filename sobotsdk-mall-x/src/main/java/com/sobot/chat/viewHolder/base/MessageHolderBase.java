package com.sobot.chat.viewHolder.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.R;
import com.sobot.chat.SobotUIConfig;
import com.sobot.chat.activity.SobotPhotoActivity;
import com.sobot.chat.adapter.SobotMsgAdapter;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.ZhiChiInitModeBase;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.ReSendDialog;
import com.sobot.chat.widget.image.SobotRCImageView;
import com.sobot.pictureframe.SobotBitmapUtil;

/**
 * view基类
 * Created by jinxl on 2017/3/17.
 */
public abstract class MessageHolderBase {

    public ZhiChiMessageBase message;//消息体
    protected Context mContext;
    protected boolean isRight = false;
    protected SobotMsgAdapter.SobotMsgCallBack msgCallBack;

    public TextView name; // 用户姓名
    private SobotRCImageView imgHead;// 头像
    public TextView reminde_time_Text;//时间提醒

    protected FrameLayout frameLayout;
    protected ImageView msgStatus;// 消息发送的状态
    protected ProgressBar msgProgressBar; // 重新发送的进度条的信信息；
    protected View sobot_ll_content;
    protected RelativeLayout sobot_rl_hollow_container;//文件类型的气泡
    protected LinearLayout sobot_ll_hollow_container;//文件类型的气泡
    protected int sobot_chat_file_bgColor;//文件类型的气泡默认颜色


    protected RelativeLayout rightEmptyRL;//左侧消息右边的空白区域
    protected LinearLayout sobot_ll_likeBtn;
    protected LinearLayout sobot_ll_dislikeBtn;
    protected TextView sobot_tv_likeBtn;//机器人评价 顶 的按钮
    protected TextView sobot_tv_dislikeBtn;//机器人评价 踩 的按钮

    protected LinearLayout sobot_ll_bottom_likeBtn;
    protected LinearLayout sobot_ll_bottom_dislikeBtn;
    protected TextView sobot_tv_bottom_likeBtn;//气泡下边 机器人评价 顶 的按钮
    protected TextView sobot_tv_bottom_dislikeBtn;//气泡下边 机器人评价 踩 的按钮

    private TextView msgContentTV; // 聊天的消息内容

    protected View mItemView;

    protected int msgMaxWidth;//气泡最大宽度

    public static ZhiChiInitModeBase initMode;
    public Information information;

    // 是否显示左侧消息的头像
    private boolean isShowLeftMsgFace = false;
    // 是否显示左侧消息的昵称
    private boolean isShowLeftMsgNickName = false;
    // 是否显示右侧消息的头像
    private boolean isShowRightMsgFace = false;
    // 是否显示右侧消息的昵称
    private boolean isShowRightMsgNickName = false;


    public MessageHolderBase(Context context, View convertView) {
        mItemView = convertView;
        mContext = context;
        reminde_time_Text = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_reminde_time_Text"));
        imgHead = convertView.findViewById(R.id.sobot_msg_face_iv);
        name = convertView.findViewById(R.id.sobot_msg_nike_name_tv);
        frameLayout = (FrameLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_frame_layout"));
        msgProgressBar = (ProgressBar) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_msgProgressBar"));// 重新发送的进度条信息
        // 消息的状态
        msgStatus = (ImageView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_msgStatus"));
        sobot_ll_content = convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_content"));
        sobot_rl_hollow_container = (RelativeLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_rl_hollow_container"));
        sobot_ll_hollow_container = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_hollow_container"));

        rightEmptyRL = (RelativeLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_right_empty_rl"));
        sobot_ll_likeBtn = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_likeBtn"));
        sobot_ll_dislikeBtn = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_dislikeBtn"));
        sobot_tv_likeBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_likeBtn"));
        sobot_tv_dislikeBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_dislikeBtn"));
        msgContentTV = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_msg"));

        sobot_ll_bottom_likeBtn = convertView.findViewById(R.id.sobot_ll_bottom_likeBtn);
        sobot_ll_bottom_dislikeBtn = convertView.findViewById(R.id.sobot_ll_bottom_dislikeBtn);
        sobot_tv_bottom_likeBtn = convertView.findViewById(R.id.sobot_tv_bottom_likeBtn);
        sobot_tv_bottom_dislikeBtn = convertView.findViewById(R.id.sobot_tv_bottom_dislikeBtn);

        sobot_chat_file_bgColor = ResourceUtils.getIdByName(mContext, "color", "sobot_chat_file_bgColor");
        applyCustomHeadUI();

        initMode = (ZhiChiInitModeBase) SharedPreferencesUtil.getObject(context,
                ZhiChiConstant.sobot_last_current_initModel);
        information = (Information) SharedPreferencesUtil.getObject(context,
                ZhiChiConstant.sobot_last_current_info);
        isShowLeftMsgFace = information.isShowLeftMsgFace();
        isShowLeftMsgNickName = information.isShowLeftMsgNickName();
        isShowRightMsgFace = information.isShowRightMsgFace();
        isShowRightMsgNickName = information.isShowRightMsgNickName();
        if (null != sobot_tv_bottom_likeBtn && null != sobot_tv_dislikeBtn) {
            if (CommonUtils.checkSDKIsZh(mContext) || CommonUtils.checkSDKIsEn(mContext)) {
                sobot_tv_bottom_likeBtn.setText(ResourceUtils.getResString(mContext, "sobot_ding"));
                sobot_tv_dislikeBtn.setText(ResourceUtils.getResString(mContext, "sobot_cai"));
            } else {
                sobot_tv_bottom_likeBtn.setText("");
                sobot_tv_dislikeBtn.setText("");
            }
        }

        if (isRight()) {
            if (isShowRightMsgFace) {
                //带有客服头像和昵称
                //148=左间距12+内间距30+右间距60+头像36+头像间距10
                msgMaxWidth = ScreenUtils.getScreenWidth((Activity) mContext) - ScreenUtils.dip2px(mContext, 148);

            } else {
                //不带客服头像和昵称
                //102=左间距12+内间距30+右间距60
                msgMaxWidth = ScreenUtils.getScreenWidth((Activity) mContext) - ScreenUtils.dip2px(mContext, 102);
            }
        } else {
            if (isShowLeftMsgFace) {
                //带有客服头像和昵称
                //148=左间距12+内间距30+右间距60+头像36+头像间距10
                msgMaxWidth = ScreenUtils.getScreenWidth((Activity) mContext) - ScreenUtils.dip2px(mContext, 148);
            } else {
                //不带客服头像和昵称
                //102=左间距12+内间距30+右间距60
                msgMaxWidth = ScreenUtils.getScreenWidth((Activity) mContext) - ScreenUtils.dip2px(mContext, 102);
            }
        }
    }

    public abstract void bindData(Context context, final ZhiChiMessageBase message);

    /**
     * 根据收发消息的标识，设置客服客户的头像
     *
     * @param itemType
     */
    public void initNameAndFace(int itemType) {
        try {
            switch (itemType) {
                case SobotMsgAdapter.MSG_TYPE_IMG_R:
                case SobotMsgAdapter.MSG_TYPE_TXT_R:
                case SobotMsgAdapter.MSG_TYPE_FILE_R:
                case SobotMsgAdapter.MSG_TYPE_VIDEO_R:
                case SobotMsgAdapter.MSG_TYPE_LOCATION_R:
                case SobotMsgAdapter.MSG_TYPE_CARD_R:
                case SobotMsgAdapter.MSG_TYPE_ROBOT_ORDERCARD_R:
                case SobotMsgAdapter.MSG_TYPE_AUDIO_R:
                case SobotMsgAdapter.MSG_TYPE_MULTI_ROUND_R:
                    this.isRight = true;
                    break;
                case SobotMsgAdapter.MSG_TYPE_TXT_L:
                case SobotMsgAdapter.MSG_TYPE_FILE_L:
                case SobotMsgAdapter.MSG_TYPE_RICH:
                case SobotMsgAdapter.MSG_TYPE_IMG_L:
                case SobotMsgAdapter.MSG_TYPE_ROBOT_TEMPLATE1:
                case SobotMsgAdapter.MSG_TYPE_ROBOT_TEMPLATE2:
                case SobotMsgAdapter.MSG_TYPE_ROBOT_TEMPLATE3:
                case SobotMsgAdapter.MSG_TYPE_ROBOT_TEMPLATE4:
                case SobotMsgAdapter.MSG_TYPE_ROBOT_TEMPLATE5:
                case SobotMsgAdapter.MSG_TYPE_ROBOT_TEMPLATE6:
                case SobotMsgAdapter.MSG_TYPE_ROBOT_ANSWER_ITEMS:
                case SobotMsgAdapter.MSG_TYPE_ROBOT_QUESTION_RECOMMEND:
                case SobotMsgAdapter.MSG_TYPE_ROBOT_KEYWORD_ITEMS:
                case SobotMsgAdapter.MSG_TYPE_MINIPROGRAM_CARD_L:
                    this.isRight = false;
                    break;
                default:
                    break;
            }
            if (isRight()) {
                if (name != null) {
                    name.setMaxWidth(msgMaxWidth);
                    if (isShowRightMsgNickName) {
                        name.setVisibility(View.VISIBLE);
                        if (message != null && !TextUtils.isEmpty(message.getSenderName())) {
                            name.setText(message.getSenderName());
                        } else {
                            name.setVisibility(View.GONE);
                        }
                    } else {
                        name.setVisibility(View.GONE);
                    }
                }
                if (imgHead != null) {
                    if (isShowRightMsgFace) {
                        imgHead.setVisibility(View.VISIBLE);
                        if (message != null && !TextUtils.isEmpty(message.getSenderFace())) {
                            SobotBitmapUtil.display(mContext, CommonUtils.encode(message.getSenderFace()),
                                    imgHead, R.drawable.sobot_default_pic, R.drawable.sobot_default_pic_err);
                        } else {
                            SobotBitmapUtil.display(mContext, R.drawable.sobot_default_pic, imgHead);
                        }
                    } else {
                        imgHead.setVisibility(View.GONE);
                    }
                }
            } else {
                if (name != null) {
                    name.setMaxWidth(msgMaxWidth);
                    if (isShowLeftMsgNickName) {
                        name.setVisibility(View.VISIBLE);
                        if (message != null) {
                            if (!TextUtils.isEmpty(message.getSenderName())) {
                                name.setText(message.getSenderName());
                            } else {
                                name.setVisibility(View.GONE);
                            }
                            if (!message.isShowFaceAndNickname()) {
                                name.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        name.setVisibility(View.GONE);
                    }
                }
                if (imgHead != null) {
                    if (isShowLeftMsgFace) {
                        imgHead.setVisibility(View.VISIBLE);
                        if (message != null && !TextUtils.isEmpty(message.getSenderFace())) {
                            SobotBitmapUtil.display(mContext, CommonUtils.encode(message.getSenderFace()),
                                    imgHead, R.drawable.sobot_default_pic, R.drawable.sobot_default_pic_err);
                        } else {
                            SobotBitmapUtil.display(mContext, R.drawable.sobot_default_pic, imgHead);
                        }
                        if (!message.isShowFaceAndNickname()) {
                            SobotBitmapUtil.display(mContext, "",
                                    imgHead);
                        }
                    } else {
                        imgHead.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void applyCustomUI() {
        if (isRight()) {
            if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_chat_right_bgColor) {
                if (sobot_ll_content != null) {
                    ScreenUtils.setBubbleBackGroud(mContext, sobot_ll_content, SobotUIConfig.sobot_chat_right_bgColor);
                }
            }
        } else {
            if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_chat_left_bgColor) {
                if (sobot_ll_content != null) {
                    ScreenUtils.setBubbleBackGroud(mContext, sobot_ll_content, SobotUIConfig.sobot_chat_left_bgColor);
                }
            }
        }
        if (sobot_rl_hollow_container != null && sobot_rl_hollow_container.getBackground() != null) {
            //文件类型气泡颜色特殊处理
            int filleContainerBgColor = SobotUIConfig.DEFAULT != SobotUIConfig.sobot_chat_file_bgColor
                    ? SobotUIConfig.sobot_chat_file_bgColor : sobot_chat_file_bgColor;
            Drawable drawable1 = sobot_rl_hollow_container.getBackground().mutate();
            GradientDrawable drawable = null;
            if (drawable1 instanceof GradientDrawable) {
                drawable = (GradientDrawable) drawable1;
            }

            if (drawable != null) {
                drawable.setStroke(ScreenUtils.dip2px(mContext, 1), mContext.getResources().getColor(filleContainerBgColor));
            }
        }
        if (sobot_ll_hollow_container != null && sobot_ll_hollow_container.getBackground() != null) {
            //文件类型气泡颜色特殊处理
            int filleContainerBgColor = SobotUIConfig.DEFAULT != SobotUIConfig.sobot_chat_file_bgColor
                    ? SobotUIConfig.sobot_chat_file_bgColor : sobot_chat_file_bgColor;
            Drawable drawable1 = sobot_ll_hollow_container.getBackground().mutate();
            GradientDrawable drawable = null;
            if (drawable1 instanceof GradientDrawable) {
                drawable = (GradientDrawable) drawable1;
            }
            if (drawable != null) {
                drawable.setStroke(ScreenUtils.dip2px(mContext, 1), mContext.getResources().getColor(filleContainerBgColor));
            }
        }

    }



    //气泡里边控件最大宽度
    public void resetMaxWidth(View view) {
        if (view != null) {
            if (view.getParent() instanceof LinearLayout) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                layoutParams.width = msgMaxWidth;
                view.setLayoutParams(layoutParams);
            } else if (view.getParent() instanceof RelativeLayout) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.width = msgMaxWidth;
                view.setLayoutParams(layoutParams);
            } else if (view.getParent() instanceof FrameLayout) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                layoutParams.width = msgMaxWidth;
                view.setLayoutParams(layoutParams);
            } else if (view.getParent() instanceof ViewGroup) {
                ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) view.getLayoutParams();
                layoutParams.width = msgMaxWidth;
                view.setLayoutParams(layoutParams);
            }
        }
    }

    //左右两边气泡内文字字体颜色
    protected void applyTextViewUIConfig(TextView view) {
        if (view != null) {
            if (!isRight()) {
                if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_chat_left_textColor) {
                    view.setTextColor(mContext.getResources().getColor(SobotUIConfig.sobot_chat_left_textColor));
                }
                view.setMaxWidth(msgMaxWidth);
            } else {
                if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_chat_right_textColor) {
                    view.setTextColor(mContext.getResources().getColor(SobotUIConfig.sobot_chat_right_textColor));
                }
            }
        }
    }

    //左右两边气泡内链接文字的字体颜色
    protected int getLinkTextColor() {
        if (isRight()) {
            if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_chat_right_link_textColor) {
                return SobotUIConfig.sobot_chat_right_link_textColor;
            } else {
                return ResourceUtils.getIdByName(mContext, "color", "sobot_color_rlink");
            }
        } else {
            if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_chat_left_link_textColor) {
                return SobotUIConfig.sobot_chat_left_link_textColor;
            } else {
                return ResourceUtils.getIdByName(mContext, "color", "sobot_color_link");
            }
        }
    }

    public boolean isRight() {
        return isRight;
    }

    public void setRight(boolean right) {
        isRight = right;
    }

    public void setMsgCallBack(SobotMsgAdapter.SobotMsgCallBack msgCallBack) {
        this.msgCallBack = msgCallBack;
    }

    /**
     * 显示重新发送dialog
     *
     * @param msgStatus
     * @param reSendListener
     */
    public static void showReSendDialog(Context context, final ImageView msgStatus,
                                        final ReSendListener reSendListener) {
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int widths = 0;
        if (width == 480) {
            widths = 80;
        } else {
            widths = 120;
        }
        final ReSendDialog reSendDialog = new ReSendDialog(context);
        reSendDialog.setOnClickListener(new ReSendDialog.OnItemClick() {
            @Override
            public void OnClick(int type) {
                if (type == 0) {// 0：确定 1：取消
                    reSendListener.onReSend();
                }
                reSendDialog.dismiss();
            }
        });
        reSendDialog.show();
        msgStatus.setClickable(true);
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        if (reSendDialog.getWindow() != null) {
            WindowManager.LayoutParams lp = reSendDialog.getWindow().getAttributes();
            lp.width = (int) (display.getWidth() - widths); // 设置宽度
            reSendDialog.getWindow().setAttributes(lp);
        }
    }

    public interface ReSendListener {
        void onReSend();
    }

    // 图片的事件监听
    public static class ImageClickLisenter implements View.OnClickListener {
        private Context context;
        private String imageUrl;
        private boolean isRight;

        public ImageClickLisenter(Context context, String imageUrl) {
            super();
            this.imageUrl = imageUrl;
            this.context = context;
        }

        // isRight: 我发送的图片显示时，gif当一般图片处理
        public ImageClickLisenter(Context context, String imageUrl, boolean isRight) {
            this(context, imageUrl);
            this.isRight = isRight;
        }

        @Override
        public void onClick(View arg0) {
            if (TextUtils.isEmpty(imageUrl)) {
                ToastUtil.showToast(context, ResourceUtils.getResString(context, "sobot_pic_type_error"));
                return;
            }
            if (SobotOption.imagePreviewListener != null) {
                //如果返回true,拦截;false 不拦截
                boolean isIntercept = SobotOption.imagePreviewListener.onPreviewImage(context, imageUrl);
                if (isIntercept) {
                    return;
                }
            }
            Intent intent = new Intent(context, SobotPhotoActivity.class);
            intent.putExtra("imageUrL", imageUrl);
            if (isRight) {
                intent.putExtra("isRight", isRight);
            }
            context.startActivity(intent);
        }

    }

    public void bindZhiChiMessageBase(ZhiChiMessageBase zhiChiMessageBase) {
        this.message = zhiChiMessageBase;
    }


    /**
     * 设置头像UI
     */
    private void applyCustomHeadUI() {
        if (imgHead != null) {
//            imgHead.setCornerRadius(4);
            imgHead.setRoundAsCircle(true);
        }
    }

    public String processPrefix(final ZhiChiMessageBase message, int num) {
        if (message != null && message.getAnswer() != null && message.getAnswer().getMultiDiaRespInfo() != null
                && message.getAnswer().getMultiDiaRespInfo().getIcLists() != null) {
            return "•";
        }
        return num + ".";
    }

    //顶踩是否显示在气泡右侧，默认是的
    public static boolean dingcaiIsShowRight() {
        if (initMode != null && initMode.getRealuateStyle() == 1) {
            return false;
        }
        return true;
    }
}