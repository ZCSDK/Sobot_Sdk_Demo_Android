package com.sobot.chat.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.api.apiUtils.GsonUtil;
import com.sobot.chat.api.model.SobotMultiDiaRespInfo;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.listener.NoDoubleClickListener;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.viewHolder.base.MessageHolderBase;
import com.sobot.chat.widget.horizontalgridpage.HorizontalGridPage;
import com.sobot.chat.widget.horizontalgridpage.PageBuilder;
import com.sobot.chat.widget.horizontalgridpage.PageCallBack;
import com.sobot.chat.widget.horizontalgridpage.PageGridAdapter;
import com.sobot.chat.widget.horizontalgridpage.PagerGridLayoutManager;
import com.sobot.chat.widget.lablesview.SobotLablesViewModel;

import java.util.ArrayList;
import java.util.HashMap;
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


    private PageGridAdapter adapter;
    private HorizontalGridPage pageView;
    private Context mContext;
    private PageBuilder pageBuilder;

    public RobotTemplateMessageHolder2(Context context, View convertView) {
        super(context, convertView);
        tv_msg = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template2_msg"));
        pageView = (HorizontalGridPage) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "pageView"));
        sobot_ll_transferBtn = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_transferBtn"));
        sobot_tv_transferBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_transferBtn"));
        sobot_tv_transferBtn.setText(ResourceUtils.getResString(context, "sobot_transfer_to_customer_service"));
        sobot_template2_item_previous_page = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template2_item_previous_page"));
        sobot_template2_item_last_page = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template2_item_last_page"));
        ll_sobot_template2_item_page = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "ll_sobot_template2_item_page"));
        this.mContext = context;
    }

    //初始化翻页控件布局 多少行 多少列
    //type =0 样式1 居中带有边框 ；type =1 样式2 居左带有索引
    public void initView(int row, int column, final String type) {
        //只初始化一次，不然会重复创建
        if (pageBuilder != null) {
            return;
        }
        pageBuilder = new PageBuilder.Builder()
                .setGrid(row, column)//设置网格
                .setPageMargin(10)//页面边距
                .setIndicatorMargins(5, 10, 5, 10)//设置指示器间隔
                .setIndicatorSize(10)//设置指示器大小
                .setIndicatorRes(android.R.drawable.presence_invisible,
                        android.R.drawable.presence_online)//设置指示器图片资源
                .setIndicatorGravity(Gravity.CENTER)//设置指示器位置
                .setSwipePercent(40)//设置翻页滑动距离百分比（1-100）
                .setShowIndicator(false)//设置显示指示器
                .setSpace(2)//设置间距
                .setItemHeight("0".equals(type) ? ScreenUtils.dip2px(mContext, 42) : ScreenUtils.dip2px(mContext, 36))
                .build();

        adapter = new PageGridAdapter<>(new PageCallBack() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(ResourceUtils.getResLayoutId(parent.getContext(), "sobot_chat_msg_item_template2_item_l"), parent, false);
                return new Template2ViewHolder(view, parent.getContext());
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                //注意：因为data经过转换，所以此处不能使用data.get(position)而要使用adapter.getData().get(position)
                SobotLablesViewModel lablesViewModel = (SobotLablesViewModel) adapter.getData().get(position);
                ((Template2ViewHolder) holder).sobotTitle.setText(lablesViewModel.getTitle());
                SobotMultiDiaRespInfo multiDiaRespInfo = adapter.getZhiChiMessageBaseData().getAnswer().getMultiDiaRespInfo();
                if ("1".equals(type)) {
                    ((Template2ViewHolder) holder).sobotTemplateItemLL.setBackground(null);
                    ((Template2ViewHolder) holder).sobotTitle.setText((position + 1) + "、 " + lablesViewModel.getTitle());
                    ((Template2ViewHolder) holder).sobotTitle.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                    ((Template2ViewHolder) holder).sobotTitle.setMaxLines(2);
                    ((Template2ViewHolder) holder).sobotTitle.setPadding(0, 0, 0, 0);
                    if (adapter.getZhiChiMessageBaseData().getSugguestionsFontColor() == 0) {
                        ((Template2ViewHolder) holder).sobotTitle.setTextColor(ContextCompat.getColor(mContext, ResourceUtils.getResColorId(mContext, "sobot_color_link")));
                    }else{
                        ((Template2ViewHolder) holder).sobotTitle.setTextColor(ContextCompat.getColor(mContext, ResourceUtils.getResColorId(mContext, "sobot_common_gray1")));
                    }
                }
            }

            @Override
            public void onItemClickListener(View view, int position) {
                if (message == null || message.getAnswer() == null) {
                    return;
                }
                String lastCid = SharedPreferencesUtil.getStringData(mContext, "lastCid", "");
                //当前cid相同相同才能重复点;ClickFlag 是否允许多次点击 0:只点击一次 1:允许重复点击
                //ClickFlag=0 时  ClickCount=0可点击，大于0 不可点击
                if (adapter.getZhiChiMessageBaseData().getSugguestionsFontColor() == 0) {
                    if (!TextUtils.isEmpty(adapter.getZhiChiMessageBaseData().getCid()) && lastCid.equals(adapter.getZhiChiMessageBaseData().getCid())) {
                        if (adapter.getZhiChiMessageBaseData().getAnswer().getMultiDiaRespInfo().getClickFlag() == 0 && adapter.getZhiChiMessageBaseData().getClickCount() > 0) {
                            return;
                        }
                        adapter.getZhiChiMessageBaseData().addClickCount();
                    } else {
                        return;
                    }
                } else {
                    return;
                }
                SobotMultiDiaRespInfo multiDiaRespInfo = message.getAnswer().getMultiDiaRespInfo();
                SobotLablesViewModel lablesViewModel = (SobotLablesViewModel) adapter.getData().get(position);
                if (multiDiaRespInfo != null && multiDiaRespInfo.getEndFlag() && !TextUtils.isEmpty(lablesViewModel.getAnchor())) {
                    if (SobotOption.newHyperlinkListener != null) {
                        //如果返回true,拦截;false 不拦截
                        boolean isIntercept = SobotOption.newHyperlinkListener.onUrlClick(mContext,lablesViewModel.getAnchor());
                        if (isIntercept) {
                            return;
                        }
                    }
                    Intent intent = new Intent(mContext, WebViewActivity.class);
                    intent.putExtra("url", lablesViewModel.getAnchor());
                    mContext.startActivity(intent);
                } else {
                    sendMultiRoundQuestions(lablesViewModel, multiDiaRespInfo, position);
                }
            }

            @Override
            public void onItemLongClickListener(View view, int position) {

            }
        });
        pageView.init(pageBuilder, message.getCurrentPageNum());
        adapter.init(pageBuilder);
        pageView.setAdapter(adapter, message);

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
                        initView(10, 1, "0");
                        ll_sobot_template2_item_page.setVisibility(View.VISIBLE);
                    } else {
                        initView(label.size(), (int) Math.ceil(label.size() / 10.0f), "0");
                        ll_sobot_template2_item_page.setVisibility(View.GONE);
                    }
                    adapter.setData(label);
                    adapter.setZhiChiMessageBaseData(message);
                } else if (inputContent != null && inputContent.length > 0) {
                    for (int i = 0; i < getDisplayNum(multiDiaRespInfo, inputContent.length); i++) {
                        SobotLablesViewModel lablesViewModel = new SobotLablesViewModel();
                        lablesViewModel.setTitle(inputContent[i]);
                        label.add(lablesViewModel);
                    }
                    // 显示更多
                    if (label.size() >= 10) {
                        initView(10, 1, multiDiaRespInfo.getTemplate());
                        ll_sobot_template2_item_page.setVisibility(View.VISIBLE);
                    } else {
                        initView(label.size(), (int) Math.ceil(label.size() / 10.0f), multiDiaRespInfo.getTemplate());
                        ll_sobot_template2_item_page.setVisibility(View.GONE);
                    }
                    adapter.setData(label);
                    adapter.setZhiChiMessageBaseData(message);
                } else {
                    pageView.setVisibility(View.GONE);
                }
            } else {
                pageView.setVisibility(View.GONE);
            }
        }
        pageView.setPageListener(new PagerGridLayoutManager.PageListener() {
            @Override
            public void onPageSizeChanged(int pageSize) {
            }

            @Override
            public void onPageSelect(int pageIndex) {
                if (pageView.isFirstPage()) {
                    sobot_template2_item_previous_page.setTextColor(ContextCompat.getColor(context, ResourceUtils.getResColorId(context, "sobot_common_gray3")));
                    Drawable img = mContext.getResources().getDrawable(ResourceUtils.getDrawableId(mContext, "sobot_no_pre_page"));
                    if (img != null) {
                        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                        sobot_template2_item_previous_page.setCompoundDrawables(null, null, img, null);
                    }
                } else {
                    sobot_template2_item_previous_page.setTextColor(ContextCompat.getColor(context, ResourceUtils.getResColorId(context, "sobot_common_gray2")));
                    Drawable img = mContext.getResources().getDrawable(ResourceUtils.getDrawableId(mContext, "sobot_pre_page"));
                    if (img != null) {
                        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                        sobot_template2_item_previous_page.setCompoundDrawables(null, null, img, null);
                    }
                }

                if (pageView.isLastPage()) {
                    sobot_template2_item_last_page.setTextColor(ContextCompat.getColor(context, ResourceUtils.getResColorId(context, "sobot_common_gray3")));
                    Drawable img = mContext.getResources().getDrawable(ResourceUtils.getDrawableId(mContext, "sobot_no_last_page"));
                    if (img != null) {
                        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                        sobot_template2_item_last_page.setCompoundDrawables(null, null, img, null);
                    }
                } else {
                    sobot_template2_item_last_page.setTextColor(ContextCompat.getColor(context, ResourceUtils.getResColorId(context, "sobot_common_gray2")));
                    Drawable img = mContext.getResources().getDrawable(ResourceUtils.getDrawableId(mContext, "sobot_last_page"));
                    if (img != null) {
                        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                        sobot_template2_item_last_page.setCompoundDrawables(null, null, img, null);
                    }
                }
            }
        });

        sobot_template2_item_previous_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageView.selectPreviousPage();
                updatePreBtn(context);
            }
        });
        sobot_template2_item_last_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageView.selectLastPage();
                updateLastBtn(context);
            }
        });
        refreshRevaluateItem();//左侧消息刷新顶和踩布局
        pageView.selectCurrentItem();
    }

    public void updatePreBtn(Context context) {
        sobot_template2_item_last_page.setTextColor(ContextCompat.getColor(context, ResourceUtils.getResColorId(context, "sobot_common_gray2")));
        Drawable lastImg = mContext.getResources().getDrawable(ResourceUtils.getDrawableId(mContext, "sobot_last_page"));
        lastImg.setBounds(0, 0, lastImg.getMinimumWidth(), lastImg.getMinimumHeight());
        sobot_template2_item_last_page.setCompoundDrawables(null, null, lastImg, null);

        Drawable img = null;
        img = mContext.getResources().getDrawable(ResourceUtils.getDrawableId(mContext, "sobot_pre_page"));
        sobot_template2_item_previous_page.setTextColor(ContextCompat.getColor(context, ResourceUtils.getResColorId(context, "sobot_common_gray2")));
        if (pageView.isFirstPage()) {
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
        if (pageView.isLastPage()) {
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


    private void sendMultiRoundQuestions(SobotLablesViewModel data, SobotMultiDiaRespInfo multiDiaRespInfo, int clickPosition) {
        if (multiDiaRespInfo == null) {
            return;
        }
        String labelText = data.getTitle();
        String[] outputParam = multiDiaRespInfo.getOutPutParamList();
        if (msgCallBack != null && message != null) {
            ZhiChiMessageBase msgObj = new ZhiChiMessageBase();
            Map<String, String> map = new HashMap<>();
            map.put("level", multiDiaRespInfo.getLevel());
            map.put("conversationId", multiDiaRespInfo.getConversationId());
            if (outputParam != null) {
                if (outputParam.length == 1) {
                    map.put(outputParam[0], data.getTitle());
                } else {
                    if (multiDiaRespInfo.getInterfaceRetList() != null && multiDiaRespInfo.getInterfaceRetList().size() > 0) {
                        for (String anOutputParam : outputParam) {
                            map.put(anOutputParam, multiDiaRespInfo.getInterfaceRetList().get(clickPosition).get(anOutputParam));
                        }
                    }
                }
            }
            msgObj.setContent(GsonUtil.map2Str(map));
            msgObj.setId(System.currentTimeMillis() + "");
            msgCallBack.sendMessageToRobot(msgObj, 4, 2, labelText, labelText);
        }
    }


    /**
     * 自定义ViewHolder来更新item，这里这是演示更新选中项的背景
     */
    class Template2ViewHolder extends RecyclerView.ViewHolder {

        TextView sobotTitle;
        LinearLayout sobotTemplateItemLL;

        public Template2ViewHolder(View convertView, Context context) {
            super(convertView);
            sobotTemplateItemLL = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template_item_ll"));
            sobotTitle = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template_item_title"));
        }
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
                    msgCallBack.doClickTransferBtn();
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
        rightEmptyRL.setVisibility(View.VISIBLE);
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
        if (msgCallBack != null && message != null) {
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
        sobot_ll_dislikeBtn.setVisibility(View.GONE);
        rightEmptyRL.setVisibility(View.GONE);
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
        sobot_ll_dislikeBtn.setVisibility(View.GONE);
        rightEmptyRL.setVisibility(View.VISIBLE);
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
        sobot_ll_likeBtn.setVisibility(View.GONE);
        sobot_ll_dislikeBtn.setVisibility(View.VISIBLE);
        rightEmptyRL.setVisibility(View.VISIBLE);
    }
}