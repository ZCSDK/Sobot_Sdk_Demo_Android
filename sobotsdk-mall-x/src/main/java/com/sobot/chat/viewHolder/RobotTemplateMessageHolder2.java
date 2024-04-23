package com.sobot.chat.viewHolder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.R;
import com.sobot.chat.adapter.SobotRobotTemplatePageAdater;
import com.sobot.chat.api.model.SobotMultiDiaRespInfo;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.listener.NoDoubleClickListener;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.viewHolder.base.MessageHolderBase;
import com.sobot.chat.widget.RobotTemplateViewPager;
import com.sobot.chat.widget.lablesview.SobotLablesViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RobotTemplateMessageHolder2 extends MessageHolderBase {
    // 聊天的消息内容
    private TextView tv_msg;

    public ZhiChiMessageBase message;
    private LinearLayout sobot_ll_transferBtn;//只包含转人工按钮
    private TextView sobot_tv_transferBtn;//机器人转人工按钮
    private TextView sobot_template2_item_previous_page;//上一页
    private TextView sobot_template2_item_last_page;//下一页
    private LinearLayout ll_sobot_template2_item_page;//分页ll


    private static final int PAGE_SIZE = 30;


    private RobotTemplateViewPager view_pager;
    private SobotRobotTemplatePageAdater templatePageAdater;
    private Context mContext;

    public RobotTemplateMessageHolder2(Context context, View convertView) {
        super(context, convertView);
        tv_msg = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template2_msg"));
        sobot_ll_transferBtn = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_transferBtn"));
        sobot_tv_transferBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_transferBtn"));
        sobot_tv_transferBtn.setText(ResourceUtils.getResString(context, "sobot_transfer_to_customer_service"));
        sobot_template2_item_previous_page = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template2_item_previous_page"));
        sobot_template2_item_last_page = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template2_item_last_page"));
        ll_sobot_template2_item_page = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "ll_sobot_template2_item_page"));
        view_pager = convertView.findViewById(R.id.view_pager);
        this.mContext = context;
    }



    @Override
    public void bindData(final Context context, ZhiChiMessageBase message) {
        this.message = message;
        if (message.getAnswer() != null && message.getAnswer().getMultiDiaRespInfo() != null) {
            final SobotMultiDiaRespInfo multiDiaRespInfo = message.getAnswer().getMultiDiaRespInfo();
            String msgStr = ChatUtils.getMultiMsgTitle(multiDiaRespInfo);
            if (!TextUtils.isEmpty(msgStr)) {
                applyTextViewUIConfig(tv_msg);
                HtmlTools.getInstance(context).setRichText(tv_msg, msgStr, getLinkTextColor());
                sobot_ll_content.setVisibility(View.VISIBLE);
            } else {
                sobot_ll_content.setVisibility(View.INVISIBLE);
            }
            checkShowTransferBtn();
            if ("000000".equals(multiDiaRespInfo.getRetCode())) {
                List<Map<String, String>> interfaceRetList = multiDiaRespInfo.getInterfaceRetList();
                String[] inputContent = multiDiaRespInfo.getInputContentList();
                ArrayList<SobotLablesViewModel> label = new ArrayList<>();
                if (interfaceRetList != null && interfaceRetList.size() > 0) {
                    for (int i = 0; i < getDisplayNum(multiDiaRespInfo, interfaceRetList.size()); i++) {
                        Map<String, String> interfaceRet = interfaceRetList.get(i);
                        SobotLablesViewModel lablesViewModel = new SobotLablesViewModel();
                        lablesViewModel.setTitle(interfaceRet.get("title"));
                        lablesViewModel.setAnchor(interfaceRet.get("anchor"));
                        label.add(lablesViewModel);
                    }
                    if (label.size() >= 10) {
                        ll_sobot_template2_item_page.setVisibility(View.VISIBLE);
                    } else {
                        ll_sobot_template2_item_page.setVisibility(View.GONE);
                    }
                    templatePageAdater = new SobotRobotTemplatePageAdater(mContext, "0",label, message, msgCallBack);
                    //绑定adapter 判断上一页下一页 使用 message  缓存当前页，下次加载时滚动上次选中页使用
                    view_pager.setTemplatePageAdater(templatePageAdater, message);
                    view_pager.setAdapter(templatePageAdater);
                    view_pager.selectCurrentItem(message.getCurrentPageNum());
                    initPreAndLastBtn(mContext);
                } else if (inputContent != null && inputContent.length > 0) {
                    for (int i = 0; i < getDisplayNum(multiDiaRespInfo, inputContent.length); i++) {
                        SobotLablesViewModel lablesViewModel = new SobotLablesViewModel();
                        lablesViewModel.setTitle(inputContent[i]);
                        label.add(lablesViewModel);
                    }
                    // 显示更多
                    if (label.size() >= 10) {
                        ll_sobot_template2_item_page.setVisibility(View.VISIBLE);
                    } else {
                        ll_sobot_template2_item_page.setVisibility(View.GONE);
                    }
                    templatePageAdater = new SobotRobotTemplatePageAdater(mContext, multiDiaRespInfo.getTemplate(),label, message, msgCallBack);
                    //绑定adapter 判断上一页下一页 使用 message  缓存当前页，下次加载时滚动上次选中页使用
                    view_pager.setTemplatePageAdater(templatePageAdater, message);
                    view_pager.setAdapter(templatePageAdater);
                    view_pager.selectCurrentItem(message.getCurrentPageNum());
                    initPreAndLastBtn(mContext);
                } else {
                    view_pager.setVisibility(View.GONE);
                }
            } else {
                view_pager.setVisibility(View.GONE);
            }
        }
        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                view_pager.updateMessageSelectItem(i);
                if (view_pager.isFirstPage()) {
                    sobot_template2_item_previous_page.setTextColor(ContextCompat.getColor(context, R.color.sobot_common_gray3));
                    Drawable img = mContext.getResources().getDrawable(R.drawable.sobot_no_pre_page);
                    if (img != null) {
                        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                        sobot_template2_item_previous_page.setCompoundDrawables(null, null, img, null);
                    }
                } else {
                    sobot_template2_item_previous_page.setTextColor(ContextCompat.getColor(context, R.color.sobot_common_gray2));
                    Drawable img = mContext.getResources().getDrawable(R.drawable.sobot_pre_page);
                    if (img != null) {
                        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                        sobot_template2_item_previous_page.setCompoundDrawables(null, null, img, null);
                    }
                }

                if (view_pager.isLastPage()) {
                    sobot_template2_item_last_page.setTextColor(ContextCompat.getColor(context, R.color.sobot_common_gray3));
                    Drawable img = mContext.getResources().getDrawable(R.drawable.sobot_no_last_page);
                    if (img != null) {
                        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                        sobot_template2_item_last_page.setCompoundDrawables(null, null, img, null);
                    }
                } else {
                    sobot_template2_item_last_page.setTextColor(ContextCompat.getColor(context, R.color.sobot_common_gray2));
                    Drawable img = mContext.getResources().getDrawable(R.drawable.sobot_last_page);
                    if (img != null) {
                        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                        sobot_template2_item_last_page.setCompoundDrawables(null, null, img, null);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


        sobot_template2_item_previous_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_pager.selectPreviousPage();
                updatePreBtn(context);
            }
        });
        sobot_template2_item_last_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_pager.selectLastPage();
                updateLastBtn(context);
            }
        });
        refreshRevaluateItem();//左侧消息刷新顶和踩布局
    }

    //上一页下一页ui初始化
    public void initPreAndLastBtn(Context context) {
        sobot_template2_item_last_page.setTextColor(ContextCompat.getColor(context, R.color.sobot_common_gray2));
        Drawable lastImg = mContext.getResources().getDrawable(R.drawable.sobot_last_page);
        lastImg.setBounds(0, 0, lastImg.getMinimumWidth(), lastImg.getMinimumHeight());
        sobot_template2_item_last_page.setCompoundDrawables(null, null, lastImg, null);

        sobot_template2_item_previous_page.setTextColor(ContextCompat.getColor(context, R.color.sobot_common_gray2));
        Drawable preImg = mContext.getResources().getDrawable(R.drawable.sobot_pre_page);
        preImg.setBounds(0, 0, preImg.getMinimumWidth(), preImg.getMinimumHeight());
        sobot_template2_item_previous_page.setCompoundDrawables(null, null, preImg, null);

        if (view_pager.isFirstPage()) {
            sobot_template2_item_previous_page.setTextColor(ContextCompat.getColor(context, R.color.sobot_common_gray3));
            Drawable npPreImg = mContext.getResources().getDrawable(R.drawable.sobot_no_pre_page);
            if (npPreImg != null) {
                npPreImg.setBounds(0, 0, npPreImg.getMinimumWidth(), npPreImg.getMinimumHeight());
                sobot_template2_item_previous_page.setCompoundDrawables(null, null, npPreImg, null);
            }
        }

        if (view_pager.isLastPage()) {
            sobot_template2_item_last_page.setTextColor(ContextCompat.getColor(context, R.color.sobot_common_gray3));
            Drawable noLastImg = mContext.getResources().getDrawable(R.drawable.sobot_no_last_page);
            if (noLastImg != null) {
                noLastImg.setBounds(0, 0, noLastImg.getMinimumWidth(), noLastImg.getMinimumHeight());
                sobot_template2_item_last_page.setCompoundDrawables(null, null, noLastImg, null);
            }
        }
    }
    public void updatePreBtn(Context context) {
        sobot_template2_item_last_page.setTextColor(ContextCompat.getColor(context, ResourceUtils.getResColorId(context, "sobot_common_gray2")));
        Drawable lastImg = mContext.getResources().getDrawable(ResourceUtils.getDrawableId(mContext, "sobot_last_page"));
        lastImg.setBounds(0, 0, lastImg.getMinimumWidth(), lastImg.getMinimumHeight());
        sobot_template2_item_last_page.setCompoundDrawables(null, null, lastImg, null);

        Drawable img = null;
        img = mContext.getResources().getDrawable(ResourceUtils.getDrawableId(mContext, "sobot_pre_page"));
        sobot_template2_item_previous_page.setTextColor(ContextCompat.getColor(context, ResourceUtils.getResColorId(context, "sobot_common_gray2")));
        if (view_pager.isFirstPage()) {
            sobot_template2_item_previous_page.setTextColor(ContextCompat.getColor(context, ResourceUtils.getResColorId(context, "sobot_common_gray3")));
            img = mContext.getResources().getDrawable(ResourceUtils.getDrawableId(mContext, "sobot_no_pre_page"));
        }
        if (img != null) {
            img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
            sobot_template2_item_previous_page.setCompoundDrawables(null, null, img, null);
        }
    }

    public void updateLastBtn(Context context) {
        sobot_template2_item_previous_page.setTextColor(ContextCompat.getColor(context, ResourceUtils.getResColorId(context, "sobot_common_gray2")));
        Drawable preImg = mContext.getResources().getDrawable(ResourceUtils.getDrawableId(mContext, "sobot_pre_page"));
        preImg.setBounds(0, 0, preImg.getMinimumWidth(), preImg.getMinimumHeight());
        sobot_template2_item_previous_page.setCompoundDrawables(null, null, preImg, null);

        sobot_template2_item_last_page.setTextColor(ContextCompat.getColor(context, ResourceUtils.getResColorId(context, "sobot_common_gray2")));
        Drawable img = null;
        img = mContext.getResources().getDrawable(ResourceUtils.getDrawableId(mContext, "sobot_last_page"));
        if (view_pager.isLastPage()) {
            sobot_template2_item_last_page.setTextColor(ContextCompat.getColor(context, ResourceUtils.getResColorId(context, "sobot_common_gray3")));
            img = mContext.getResources().getDrawable(ResourceUtils.getDrawableId(mContext, "sobot_no_last_page"));
        }
        if (img != null) {
            img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
            sobot_template2_item_last_page.setCompoundDrawables(null, null, img, null);
        }
    }


    private int getDisplayNum(SobotMultiDiaRespInfo multiDiaRespInfo, int maxSize) {
        if (multiDiaRespInfo == null) {
            return 0;
        }
        return Math.min(multiDiaRespInfo.getPageNum() * PAGE_SIZE, maxSize);
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