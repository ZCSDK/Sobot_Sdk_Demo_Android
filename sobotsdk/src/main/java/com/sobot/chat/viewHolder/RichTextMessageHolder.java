package com.sobot.chat.viewHolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.activity.SobotFileDetailActivity;
import com.sobot.chat.activity.SobotVideoActivity;
import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.adapter.SobotMsgAdapter;
import com.sobot.chat.api.model.ChatMessageRichListModel;
import com.sobot.chat.api.model.SobotCacheFile;
import com.sobot.chat.api.model.Suggestions;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.camera.util.FileUtil;
import com.sobot.chat.listener.NoDoubleClickListener;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.SobotBitmapUtil;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.viewHolder.base.MessageHolderBase;
import com.sobot.chat.widget.attachment.FileTypeConfig;

import java.util.ArrayList;

/**
 * 富文本消息
 * Created by jinxl on 2017/3/17.
 */
public class RichTextMessageHolder extends MessageHolderBase implements View.OnClickListener {
    private TextView msg; // 聊天的消息内容
    private LinearLayout sobot_rich_ll;//拆分的富文本消息
    private TextView sobot_msgStripe; // 多轮会话中配置的引导语
    private LinearLayout answersList;
    private TextView stripe;

    private LinearLayout sobot_chat_more_action;//包含以下所有控件

    private LinearLayout sobot_ll_transferBtn;//只包含转人工按钮
    private TextView sobot_tv_transferBtn;//机器人转人工按钮

    private RelativeLayout sobot_right_empty_rl;
    private LinearLayout sobot_ll_content;
    private LinearLayout sobot_ll_likeBtn;
    private LinearLayout sobot_ll_dislikeBtn;
    private TextView sobot_tv_likeBtn;//机器人评价 顶 的按钮
    private TextView sobot_tv_dislikeBtn;//机器人评价 踩 的按钮
    private LinearLayout sobot_ll_switch;//换一组按钮
    private TextView sobot_tv_switch;
    private View sobot_view_split;//换一组和查看详情分割线
    int msgMaxWidth;//气泡最大宽度

    public RichTextMessageHolder(Context context, View convertView) {
        super(context, convertView);
        //102=左间距12+内间距30+右间距60
        msgMaxWidth = ScreenUtils.getScreenWidth((Activity) mContext) - ScreenUtils.dip2px(mContext, 102);
        msg = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_msg"));
        sobot_rich_ll = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_rich_ll"));
        sobot_msgStripe = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_msgStripe"));
        sobot_chat_more_action = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_chat_more_action"));
        sobot_ll_transferBtn = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_transferBtn"));
        sobot_ll_likeBtn = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_likeBtn"));
        sobot_ll_dislikeBtn = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_dislikeBtn"));
        sobot_ll_content = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_content"));
        sobot_ll_switch = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_switch"));
        sobot_tv_switch = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_switch"));
        sobot_tv_switch.setText(ResourceUtils.getResString(context, "sobot_switch"));
        sobot_view_split = convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_view_split"));
        sobot_right_empty_rl = (RelativeLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_right_empty_rl"));

        stripe = (TextView) convertView.findViewById(ResourceUtils
                .getIdByName(context, "id", "sobot_stripe"));
        answersList = (LinearLayout) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id",
                        "sobot_answersList"));

        sobot_tv_transferBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_transferBtn"));
        sobot_tv_transferBtn.setText(ResourceUtils.getResString(context, "sobot_transfer_to_customer_service"));
        sobot_tv_likeBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_likeBtn"));
        sobot_tv_dislikeBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_dislikeBtn"));
        sobot_ll_switch.setOnClickListener(this);
        msg.setMaxWidth(msgMaxWidth);
    }

    @Override
    public void bindData(final Context context, final ZhiChiMessageBase message) {
        // 更具消息类型进行对布局的优化
        if (message.getAnswer() != null) {
            setupMsgContent(context, message);
            if (!TextUtils.isEmpty(message.getAnswer().getMsgStripe())) {
                sobot_msgStripe.setVisibility(View.VISIBLE);
                sobot_msgStripe.setText(message.getAnswer().getMsgStripe());
            } else {
                sobot_msgStripe.setVisibility(View.GONE);
            }
        }

        // 回复语的答复
        String stripeContent = message.getStripe() != null ? message.getStripe().trim() : "";
        if (!TextUtils.isEmpty(stripeContent)) {
            //去掉p标签
            stripeContent=stripeContent.replace("<p>","").replace("</p>","");
            // 设置提醒的内容
            stripe.setVisibility(View.VISIBLE);
            HtmlTools.getInstance(context).setRichText(stripe, stripeContent, getLinkTextColor());
        } else {
            stripe.setText(null);
            stripe.setVisibility(View.GONE);
        }

        if (message.isGuideGroupFlag()//有分组
                && message.getListSuggestions() != null//有分组问题列表
                && message.getGuideGroupNum() > -1//分组不是全部
                && message.getListSuggestions().size() > 0//问题数量大于0
                && message.getGuideGroupNum() < message.getListSuggestions().size()//分组数量小于问题数量
        ) {
            sobot_ll_switch.setVisibility(View.VISIBLE);
            sobot_view_split.setVisibility(View.VISIBLE);
        } else {
            sobot_ll_switch.setVisibility(View.GONE);
            sobot_view_split.setVisibility(View.GONE);

        }

        if (message.getSugguestions() != null && message.getSugguestions().length > 0) {
            resetAnswersList();
        } else {
            answersList.setVisibility(View.GONE);
        }

        checkShowTransferBtn();

        msg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!TextUtils.isEmpty(message.getAnswer().getMsg())) {
                    ToastUtil.showCopyPopWindows(context, view, message.getAnswer().getMsg(), 30, 0);
                }
                return false;
            }
        });

        applyTextViewUIConfig(msg);

        refreshItem();
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
                answer.setOnClickListener(new AnsWerClickLisenter(mContext, null,
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
        ViewGroup.LayoutParams layoutParams = sobot_ll_content.getLayoutParams();
        layoutParams.width = ScreenUtils.getScreenWidth((Activity) mContext) - ScreenUtils.dip2px(mContext, 72);
        sobot_ll_content.setLayoutParams(layoutParams);
    }

    private void resetMinWidth() {
        ViewGroup.LayoutParams layoutParams = sobot_ll_content.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        sobot_ll_content.setLayoutParams(layoutParams);
    }

    private void checkShowTransferBtn() {
        if (message.isShowTransferBtn()) {
            showTransferBtn();
        } else {
            hideTransferBtn();
        }
    }

    private void hideContainer() {
        if (!message.isShowTransferBtn() && message.getRevaluateState() == 0) {
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
                    msgCallBack.doClickTransferBtn();
                }
            }
        });
    }

    public void refreshItem() {
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
        //有顶和踩时显示信息显示两行 72-10-10=52 总高度减去上下内间距
        msg.setMinHeight(ScreenUtils.dip2px(mContext, 52));
        //有顶和踩时,拆分后的富文本消息如果只有一个并且是文本类型设置最小高度 72-10-10=52 总高度减去上下内间距
        if (sobot_rich_ll != null && sobot_rich_ll.getChildCount() == 1) {
            for (int i = 0; i < sobot_rich_ll.getChildCount(); i++) {
                View view = sobot_rich_ll.getChildAt(i);
                if (view instanceof TextView) {
                    TextView tv = (TextView) view;
                    tv.setMinHeight(ScreenUtils.dip2px(mContext, 52));
                }
            }
        }

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
        //没有顶和踩时显示信息显示一行 42-10-10=52 总高度减去上下内间距
        msg.setMinHeight(ScreenUtils.dip2px(mContext, 22));
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
        //有顶和踩时显示信息显示两行
        msg.setMinHeight(ScreenUtils.dip2px(mContext, 52));
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
        //有顶和踩时显示信息显示两行
        msg.setMinHeight(ScreenUtils.dip2px(mContext, 52));
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_ll_switch) {
            // 换一组
            if (message != null && message.getListSuggestions() != null
                    && message.getListSuggestions().size() > 0) {
                LogUtils.i(message.getCurrentPageNum() + "==================");
                int pageNum = message.getCurrentPageNum() + 1;
                int total = message.getListSuggestions().size();
                int pre = message.getGuideGroupNum();
                int maxNum = (total % pre == 0) ? (total / pre) : (total / pre + 1);
                LogUtils.i(maxNum + "=========maxNum=========");
                pageNum = (pageNum >= maxNum) ? 0 : pageNum;
                message.setCurrentPageNum(pageNum);

                LogUtils.i(message.getCurrentPageNum() + "==================");
                resetAnswersList();
            }


        }
    }

    // 查看阅读全文的监听
    public static class ReadAllTextLisenter implements View.OnClickListener {
        private String mUrlContent;
        private Context context;

        public ReadAllTextLisenter(Context context, String urlContent) {
            super();
            this.mUrlContent = urlContent;
            this.context = context;
        }

        @Override
        public void onClick(View arg0) {

            if (!mUrlContent.startsWith("http://")
                    && !mUrlContent.startsWith("https://")) {
                mUrlContent = "http://" + mUrlContent;
            }
            // 内部浏览器
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("url", mUrlContent);
            context.startActivity(intent);
        }
    }

    // 问题的回答监听
    public static class AnsWerClickLisenter implements View.OnClickListener {

        private String msgContent;
        private String id;
        private ImageView img;
        private String docId;
        private Context context;
        private SobotMsgAdapter.SobotMsgCallBack mMsgCallBack;

        public AnsWerClickLisenter(Context context, String id, String msgContent, ImageView image,
                                   String docId, SobotMsgAdapter.SobotMsgCallBack msgCallBack) {
            super();
            this.context = context;
            this.msgContent = msgContent;
            this.id = id;
            this.img = image;
            this.docId = docId;
            mMsgCallBack = msgCallBack;
        }

        @Override
        public void onClick(View arg0) {
            if (img != null) {
                img.setVisibility(View.GONE);
            }

            if (mMsgCallBack != null) {
                mMsgCallBack.hidePanelAndKeyboard();
                ZhiChiMessageBase msgObj = new ZhiChiMessageBase();
                msgObj.setContent(msgContent);
                msgObj.setId(id);
                mMsgCallBack.sendMessageToRobot(msgObj, 0, 1, docId);
            }
        }
    }

    int getTextSize(TextView view) {

        CharSequence text = view.getText();

        TextPaint paint = view.getPaint();

        int textSize = (int) Layout.getDesiredWidth(text, 0, text.length(), paint);

        return textSize;

    }

    private void setupMsgContent(final Context context, final ZhiChiMessageBase message) {
        if (message.getAnswer() != null && message.getAnswer().getRichList() != null && message.getAnswer().getRichList().size() > 0) {
            LinearLayout.LayoutParams wlayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            wlayoutParams.setMargins(0, ScreenUtils.dip2px(context, 3), 0, 0);
            sobot_rich_ll.removeAllViews();
            for (int i = 0; i < message.getAnswer().getRichList().size(); i++) {
                final ChatMessageRichListModel richListModel = message.getAnswer().getRichList().get(i);
                if (richListModel != null) {
                    // 0：文本，1：图片，2：音频，3：视频，4：文件
                    if (richListModel.getType() == 0) {
                        TextView textView = new TextView(mContext);
                        textView.setLayoutParams(wlayoutParams);
                        textView.setMaxWidth(msgMaxWidth);
                        if (!TextUtils.isEmpty(richListModel.getName()) && HtmlTools.isHasPatterns(richListModel.getMsg())) {
                            textView.setTextColor(ContextCompat.getColor(mContext, ResourceUtils.getResColorId(mContext, "sobot_color_link")));
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (SobotOption.newHyperlinkListener != null) {
                                        //如果返回true,拦截;false 不拦截
                                        boolean isIntercept = SobotOption.newHyperlinkListener.onUrlClick(mContext,richListModel.getMsg());
                                        if (isIntercept) {
                                            return;
                                        }
                                    }
                                    Intent intent = new Intent(context, WebViewActivity.class);
                                    intent.putExtra("url", richListModel.getMsg());
                                    context.startActivity(intent);
                                }
                            });
                            textView.setText(richListModel.getName());
                        } else {
                            textView.setTextColor(ContextCompat.getColor(mContext, ResourceUtils.getResColorId(mContext, "sobot_left_msg_text_color")));
                            HtmlTools.getInstance(mContext).setRichText(textView, richListModel.getMsg(), getLinkTextColor());
                        }
                        sobot_rich_ll.addView(textView);
                    } else if (richListModel.getType() == 1 && HtmlTools.isHasPatterns(richListModel.getMsg())) {
                        LinearLayout.LayoutParams mlayoutParams = new LinearLayout.LayoutParams(msgMaxWidth,
                                ScreenUtils.dip2px(context, 200));
                        mlayoutParams.setMargins(0, ScreenUtils.dip2px(context, 3), 0, 0);
                        ImageView imageView = new ImageView(mContext);
                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        imageView.setLayoutParams(mlayoutParams);
                        SobotBitmapUtil.display(mContext, richListModel.getMsg(), imageView);
                        imageView.setOnClickListener(new ImageClickLisenter(context, richListModel.getMsg(), isRight));
                        sobot_rich_ll.addView(imageView);
                    } else if (richListModel.getType() == 3 && HtmlTools.isHasPatterns(richListModel.getMsg())) {
                        TextView videoView = new TextView(mContext);
                        videoView.setMaxWidth(msgMaxWidth);
                        HtmlTools.getInstance(mContext).setRichText(videoView, TextUtils.isEmpty(richListModel.getName()) ? richListModel.getMsg() : richListModel.getName(), getLinkTextColor());
                        videoView.setLayoutParams(wlayoutParams);
                        videoView.setTextColor(ContextCompat.getColor(mContext, ResourceUtils.getResColorId(mContext, "sobot_color_link")));
                        sobot_rich_ll.addView(videoView);
                        videoView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SobotCacheFile cacheFile = new SobotCacheFile();
                                cacheFile.setFileName(richListModel.getName());
                                cacheFile.setUrl(richListModel.getMsg());
                                cacheFile.setFileType(FileTypeConfig.getFileType(FileUtil.getFileEndWith(richListModel.getMsg())));
                                cacheFile.setMsgId(message.getMsgId() + richListModel.getMsg());
                                Intent intent = SobotVideoActivity.newIntent(mContext, cacheFile);
                                mContext.startActivity(intent);
                            }
                        });
                    } else if ((richListModel.getType() == 4 || richListModel.getType() == 2) && HtmlTools.isHasPatterns(richListModel.getMsg())) {
                        TextView fileView = new TextView(mContext);
                        fileView.setMaxWidth(msgMaxWidth);
                        HtmlTools.getInstance(mContext).setRichText(fileView, TextUtils.isEmpty(richListModel.getName()) ? richListModel.getMsg() : richListModel.getName(), getLinkTextColor());
                        fileView.setLayoutParams(wlayoutParams);
                        fileView.setTextColor(ContextCompat.getColor(mContext, ResourceUtils.getResColorId(mContext, "sobot_color_link")));
                        sobot_rich_ll.addView(fileView);
                        fileView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 打开详情页面
                                Intent intent = new Intent(mContext, SobotFileDetailActivity.class);
                                SobotCacheFile cacheFile = new SobotCacheFile();
                                cacheFile.setFileName(richListModel.getName());
                                cacheFile.setUrl(richListModel.getMsg());
                                cacheFile.setFileType(FileTypeConfig.getFileType(FileUtil.getFileEndWith(richListModel.getMsg())));
                                cacheFile.setMsgId(message.getMsgId() + richListModel.getMsg());
                                intent.putExtra(ZhiChiConstant.SOBOT_INTENT_DATA_SELECTED_FILE, cacheFile);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intent);
                            }
                        });
                    }
                }
            }
            sobot_rich_ll.setVisibility(View.VISIBLE);
            msg.setVisibility(View.GONE);
            LinearLayout.LayoutParams contentlayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            contentlayoutParams.leftMargin = ScreenUtils.dip2px(context, 12);
            sobot_ll_content.setLayoutParams(contentlayoutParams);
        } else {
            sobot_rich_ll.setVisibility(View.GONE);
            if (message.getAnswer() != null && !TextUtils.isEmpty(message.getAnswer().getMsg())) {
                msg.setVisibility(View.VISIBLE);
                String robotAnswer = "";
                if ("9".equals(message.getAnswer().getMsgType())) {
                    if (message.getAnswer().getMultiDiaRespInfo() != null) {
                        robotAnswer = message.getAnswer().getMultiDiaRespInfo().getAnswer();
                    }

                } else {
                    robotAnswer = message.getAnswer().getMsg();
                }
                HtmlTools.getInstance(context).setRichText(msg, robotAnswer, getLinkTextColor());
            } else {
                msg.setVisibility(View.GONE);
            }
        }
    }

    private String processPrefix(final ZhiChiMessageBase message, int num) {
        if (message != null && message.getAnswer() != null && message.getAnswer().getMultiDiaRespInfo() != null
                && message.getAnswer().getMultiDiaRespInfo().getIcLists() != null) {
            return "•";
        }
        return num + ".";
    }
}