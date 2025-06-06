package com.sobot.chat.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.R;
import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.api.apiUtils.GsonUtil;
import com.sobot.chat.api.model.SobotMultiDiaRespInfo;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.SharedPreferencesUtil;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.widget.lablesview.SobotLablesViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//机器人 消息多轮模板2 PagerAdapter
public class SobotRobotTemplatePageAdater extends PagerAdapter {
    private ArrayList<View> mViewList = new ArrayList<>();

    public SobotRobotTemplatePageAdater(final Context context, String type, final ArrayList<SobotLablesViewModel> label, final ZhiChiMessageBase messageBase, final SobotMsgAdapter.SobotMsgCallBack msgCallBack) {
        if (context != null && label != null && label.size() > 0 && messageBase != null) {
            ArrayList<View> tempArr = new ArrayList<>();
            for (int i = 0; i < label.size(); i++) {
                View llRoot = LayoutInflater.from(context).inflate(R.layout.sobot_chat_msg_item_template2_item_l, null);
                TextView textView = llRoot.findViewById(R.id.sobot_template_item_title);
                LinearLayout templateItemLL = llRoot.findViewById(R.id.sobot_template_item_ll);
                if ("1".equals(type)) {
                    textView.setText((i + 1) + "、 " + label.get(i).getTitle());
                    textView.setBackground(null);
                    textView.setGravity(Gravity.LEFT);
                    textView.setPadding(0, ScreenUtils.dip2px(context, 10), 0, 0);
                } else {
                    textView.setText(label.get(i).getTitle());
                }

                if (messageBase.getSugguestionsFontColor() == 0) {
                    if ("1".equals(type)) {
                        textView.setTextColor(ContextCompat.getColor(context, R.color.sobot_color_link));
                    } else {
                        textView.setTextColor(ContextCompat.getColor(context, R.color.sobot_color));
                    }
                } else {
                    textView.setTextColor(ContextCompat.getColor(context, R.color.sobot_common_gray1));
                }
                final int finalI = i;
                llRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (messageBase == null || messageBase.getAnswer() == null) {
                            return;
                        }
                        String lastCid = SharedPreferencesUtil.getStringData(context, "lastCid", "");
                        //当前cid相同相同才能重复点;ClickFlag 是否允许多次点击 0:只点击一次 1:允许重复点击
                        //ClickFlag=0 时  ClickCount=0可点击，大于0 不可点击
                        if (messageBase.getSugguestionsFontColor() == 0) {
                            if (!TextUtils.isEmpty(messageBase.getCid()) && lastCid.equals(messageBase.getCid())) {
                                if (messageBase.getAnswer().getMultiDiaRespInfo().getClickFlag() == 0 && messageBase.getClickCount() > 0) {
                                    return;
                                }
                                messageBase.addClickCount();
                            } else {
                                return;
                            }
                        } else {
                            return;
                        }
                        SobotMultiDiaRespInfo multiDiaRespInfo = messageBase.getAnswer().getMultiDiaRespInfo();
                        SobotLablesViewModel lablesViewModel = label.get(finalI);
                        if (multiDiaRespInfo != null && multiDiaRespInfo.getEndFlag() && !TextUtils.isEmpty(lablesViewModel.getAnchor())) {
                            if (SobotOption.newHyperlinkListener != null) {
                                //如果返回true,拦截;false 不拦截
                                boolean isIntercept = SobotOption.newHyperlinkListener.onUrlClick(context, lablesViewModel.getAnchor());
                                if (isIntercept) {
                                    return;
                                }
                            }
                            Intent intent = new Intent(context, WebViewActivity.class);
                            intent.putExtra("url", lablesViewModel.getAnchor());
                            context.startActivity(intent);
                        } else {
                            sendMultiRoundQuestions(lablesViewModel, multiDiaRespInfo, finalI, messageBase, msgCallBack);
                        }
                    }
                });
                tempArr.add(llRoot);
            }
            List<List<View>> groups = new ArrayList<>(); // 存放分好组的结果
            int groupSize = 10; // 设置每组的大小
            for (int startIndex = 0; startIndex < tempArr.size(); startIndex += groupSize) {
                int endIndex = Math.min(startIndex + groupSize, tempArr.size()); // 计算当前组的结尾索引
                List<View> group = tempArr.subList(startIndex, endIndex); // 获取当前组的子列表
                groups.add(group); // 将当前组添加到结果列表中
            }
            for (int j = 0; j < groups.size(); j++) {
                LinearLayout pagell = new LinearLayout(context);
                pagell.setOrientation(LinearLayout.VERTICAL);
                pagell.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                List<View> textViewList = groups.get(j);
                for (int m = 0; m < textViewList.size(); m++) {
                    pagell.addView(textViewList.get(m));
                }
                mViewList.add(pagell);
            }
        }

    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    // 添加界面，一般会添加当前页和左右两边的页面
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position));
        return mViewList.get(position);
    }

    // 去除页面
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));
    }

    private void sendMultiRoundQuestions(SobotLablesViewModel data, SobotMultiDiaRespInfo multiDiaRespInfo, int clickPosition, ZhiChiMessageBase messageBase, SobotMsgAdapter.SobotMsgCallBack msgCallBack) {
        if (multiDiaRespInfo == null) {
            return;
        }
        String labelText = data.getTitle();
        String[] outputParam = multiDiaRespInfo.getOutPutParamList();
        if (msgCallBack != null && messageBase != null) {
            ZhiChiMessageBase msgObj = new ZhiChiMessageBase();
            Map<String, String> map = new HashMap<>();
            map.put("level", multiDiaRespInfo.getLevel() + "");
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
}
