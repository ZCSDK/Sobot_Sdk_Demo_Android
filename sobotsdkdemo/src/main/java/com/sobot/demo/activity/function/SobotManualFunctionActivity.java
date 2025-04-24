package com.sobot.demo.activity.function;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.api.enumtype.SobotAutoSendMsgMode;
import com.sobot.chat.api.model.ConsultingContent;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.OrderCardContentModel;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.demo.R;
import com.sobot.demo.SobotSPUtil;
import com.sobot.demo.model.SobotDemoOtherModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SobotManualFunctionActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText sobot_et_groupid, sobot_et_choose_adminid, sobot_et_tranReceptionistFlag, sobot_et_customer_fields, sobot_et_autoSendMsgMode, sobot_et_autoSendMsgcontent, sobot_et_autoSendMsgtype,
            sobot_et_queue_First, sobot_et_summary_params, sobot_et_multi_params, sobot_et_vip_level, sobot_et_user_label, sobot_et_autoSendMsg_count;
    private RelativeLayout sobot_tv_left, sobot_rl_4_2_8, sobot_rl_4_2_9, sobot_rl_4_2_11, sobot_rl_4_2_13_1, sobot_rl_4_2_13_2, sobot_rl_4_2_13_3, sobot_rl_4_2_13_4, sobot_rl_4_2_13_5, sobot_rl_4_2_13_6, sobot_rl_4_2_13_7;
    private ImageView sobotImage428, sobotImage429, sobotImage4211, sobotImage42131, sobotImage42132, sobotImage42133, sobotImage42134, sobotImage42135, sobotImage42136, sobotImage42137;
    private boolean status428, status429, status4211, status42131, status42132, status42133, status42134, status42135, status42136, status42137;
    private TextView tv_manual_fun_4_2_1, tv_manual_fun_4_2_2, tv_manual_fun_4_2_8, tv_manual_fun_4_2_9, tv_manual_fun_4_2_13, sobot_tv_save;
    private Information information;
    private SobotDemoOtherModel otherModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.sobot_demo_manual_func_activity);
        information = (Information) SobotSPUtil.getObject(getContext(), "sobot_demo_infomation");
        otherModel = (SobotDemoOtherModel) SobotSPUtil.getObject(getContext(), "sobot_demo_otherModel");
        findvViews();
    }

    private void findvViews() {
        sobot_tv_left = (RelativeLayout) findViewById(R.id.sobot_demo_tv_left);
        TextView sobot_text_title = (TextView) findViewById(R.id.sobot_demo_tv_title);
        sobot_text_title.setText("人工客服");
        sobot_tv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sobot_tv_save = findViewById(R.id.sobot_tv_save);
        sobot_tv_save.setOnClickListener(this);
        sobot_tv_save.setVisibility(View.VISIBLE);

        sobot_rl_4_2_8 = (RelativeLayout) findViewById(R.id.sobot_rl_4_2_8);
        sobot_rl_4_2_8.setOnClickListener(this);
        sobotImage428 = (ImageView) findViewById(R.id.sobot_image_4_2_8);

        sobot_rl_4_2_9 = (RelativeLayout) findViewById(R.id.sobot_rl_4_2_9);
        sobot_rl_4_2_9.setOnClickListener(this);
        sobotImage429 = (ImageView) findViewById(R.id.sobot_image_4_2_9);

        sobot_rl_4_2_11 = (RelativeLayout) findViewById(R.id.sobot_rl_4_2_11);
        sobot_rl_4_2_11.setOnClickListener(this);
        sobotImage4211 = (ImageView) findViewById(R.id.sobot_image_4_2_11);

        sobot_rl_4_2_13_1 = (RelativeLayout) findViewById(R.id.sobot_rl_4_2_13_1);
        sobot_rl_4_2_13_1.setOnClickListener(this);
        sobotImage42131 = (ImageView) findViewById(R.id.sobot_image_4_2_13_1);
        sobot_rl_4_2_13_2 = (RelativeLayout) findViewById(R.id.sobot_rl_4_2_13_2);
        sobot_rl_4_2_13_2.setOnClickListener(this);
        sobotImage42132 = (ImageView) findViewById(R.id.sobot_image_4_2_13_2);
        sobot_rl_4_2_13_3 = (RelativeLayout) findViewById(R.id.sobot_rl_4_2_13_3);
        sobot_rl_4_2_13_3.setOnClickListener(this);
        sobotImage42133 = (ImageView) findViewById(R.id.sobot_image_4_2_13_3);
        sobot_rl_4_2_13_4 = (RelativeLayout) findViewById(R.id.sobot_rl_4_2_13_4);
        sobot_rl_4_2_13_4.setOnClickListener(this);
        sobotImage42134 = (ImageView) findViewById(R.id.sobot_image_4_2_13_4);
        sobot_rl_4_2_13_5 = (RelativeLayout) findViewById(R.id.sobot_rl_4_2_13_5);
        sobot_rl_4_2_13_5.setOnClickListener(this);
        sobotImage42135 = (ImageView) findViewById(R.id.sobot_image_4_2_13_5);
        sobot_rl_4_2_13_6 = (RelativeLayout) findViewById(R.id.sobot_rl_4_2_13_6);
        sobot_rl_4_2_13_6.setOnClickListener(this);
        sobotImage42136 = (ImageView) findViewById(R.id.sobot_image_4_2_13_6);
        sobot_rl_4_2_13_7 = (RelativeLayout) findViewById(R.id.sobot_rl_4_2_13_7);
        sobot_rl_4_2_13_7.setOnClickListener(this);
        sobotImage42137 = (ImageView) findViewById(R.id.sobot_image_4_2_13_7);


        sobot_et_groupid = findViewById(R.id.sobot_et_groupid);
        sobot_et_choose_adminid = findViewById(R.id.sobot_et_choose_adminid);
        sobot_et_tranReceptionistFlag = findViewById(R.id.sobot_et_tranReceptionistFlag);
        sobot_et_customer_fields = findViewById(R.id.sobot_et_customer_fields);
        sobot_et_autoSendMsgMode = findViewById(R.id.sobot_et_autoSendMsgMode);
        sobot_et_autoSendMsgcontent = findViewById(R.id.sobot_et_autoSendMsgcontent);
        sobot_et_autoSendMsgtype = findViewById(R.id.sobot_et_autoSendMsgtype);
        sobot_et_autoSendMsg_count = findViewById(R.id.sobot_et_autoSendMsg_count);
        sobot_et_queue_First = findViewById(R.id.sobot_et_queue_First);
        sobot_et_summary_params = findViewById(R.id.sobot_et_summary_params);
        sobot_et_multi_params = findViewById(R.id.sobot_et_multi_params);
        sobot_et_vip_level = findViewById(R.id.sobot_et_vip_level);
        sobot_et_user_label = findViewById(R.id.sobot_et_user_label);

        if (information != null) {
            sobot_et_groupid.setText(TextUtils.isEmpty(information.getGroupid()) ? "" : information.getGroupid());
            sobot_et_choose_adminid.setText(TextUtils.isEmpty(information.getChoose_adminid()) ? "" : information.getChoose_adminid());
            sobot_et_tranReceptionistFlag.setText(information.getTranReceptionistFlag() + "");
            sobot_et_customer_fields.setText(TextUtils.isEmpty(information.getCustomer_fields()) ? "" : information.getCustomer_fields());
            if (information.getAutoSendMsgMode() != null) {
                sobot_et_autoSendMsgMode.setText(information.getAutoSendMsgMode().getValue() + "");
                sobot_et_autoSendMsgcontent.setText(TextUtils.isEmpty(information.getAutoSendMsgMode().getContent()) ? "" : information.getAutoSendMsgMode().getContent() + "");
                sobot_et_autoSendMsgtype.setText(information.getAutoSendMsgMode().getAuto_send_msgtype() + "");
                sobot_et_autoSendMsg_count.setText(information.getAutoSendMsgMode().geIsEveryTimeAutoSend() ? "0" : "1");
            }
            sobot_et_queue_First.setText(information.is_queue_first() ? "1" : "0");
            sobot_et_summary_params.setText(TextUtils.isEmpty(information.getSummary_params()) ? "" : information.getSummary_params());
            sobot_et_multi_params.setText(TextUtils.isEmpty(information.getMulti_params()) ? "" : information.getMulti_params());
            if (!TextUtils.isEmpty(information.getIsVip())) {
                setImageShowStatus("1".equals(information.getIsVip()) ? true : false, sobotImage4211);
            }
            sobot_et_vip_level.setText(TextUtils.isEmpty(information.getVip_level()) ? "" : information.getVip_level());
            sobot_et_user_label.setText(TextUtils.isEmpty(information.getUser_label()) ? "" : information.getUser_label());
            status42131 = information.isHideMenuSatisfaction();
            setImageShowStatus(status42131, sobotImage42131);
            status42132 = information.isHideMenuLeave();
            setImageShowStatus(status42132, sobotImage42132);
            status42133 = information.isHideMenuPicture();
            setImageShowStatus(status42133, sobotImage42133);
            status42134 = information.isHideMenuVedio();
            setImageShowStatus(status42134, sobotImage42134);
            status42135 = information.isHideMenuCamera();
            setImageShowStatus(status42135, sobotImage42135);
            status42136 = information.isHideMenuFile();
            setImageShowStatus(status42136, sobotImage42136);
            status42137 = information.isHideMenuManualLeave();
            setImageShowStatus(status42137, sobotImage42137);


        }
        if (otherModel != null) {
            status428 = otherModel.isUserConsultingContentDemo();
            setImageShowStatus(status428, sobotImage428);
            status429 = otherModel.isUserOrderCardContentModelDemo();
            setImageShowStatus(status429, sobotImage429);
        }


        tv_manual_fun_4_2_1 = findViewById(R.id.tv_manual_fun_4_2_1);
        tv_manual_fun_4_2_1.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-2-1-对接指定技能组");
        setOnClick(tv_manual_fun_4_2_1, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-2-1-%E5%AF%B9%E6%8E%A5%E6%8C%87%E5%AE%9A%E6%8A%80%E8%83%BD%E7%BB%84");
        tv_manual_fun_4_2_2 = findViewById(R.id.tv_manual_fun_4_2_2);
        tv_manual_fun_4_2_2.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-2-2-对接指定客服");
        setOnClick(tv_manual_fun_4_2_2, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-2-2-%E5%AF%B9%E6%8E%A5%E6%8C%87%E5%AE%9A%E5%AE%A2%E6%9C%8D");
        tv_manual_fun_4_2_8 = findViewById(R.id.tv_manual_fun_4_2_8);
        tv_manual_fun_4_2_8.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-2-8-商品的咨询信息并支持直接发送消息卡片，仅人工模式下支持");
        setOnClick(tv_manual_fun_4_2_8, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-2-8-%E5%95%86%E5%93%81%E7%9A%84%E5%92%A8%E8%AF%A2%E4%BF%A1%E6%81%AF%E5%B9%B6%E6%94%AF%E6%8C%81%E7%9B%B4%E6%8E%A5%E5%8F%91%E9%80%81%E6%B6%88%E6%81%AF%E5%8D%A1%E7%89%87%EF%BC%8C%E4%BB%85%E4%BA%BA%E5%B7%A5%E6%A8%A1%E5%BC%8F%E4%B8%8B%E6%94%AF%E6%8C%81");
        tv_manual_fun_4_2_9 = findViewById(R.id.tv_manual_fun_4_2_9);
        tv_manual_fun_4_2_9.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-2-9-发送订单卡片，仅人工模式下支持,订单卡片点击事件可拦截");
        setOnClick(tv_manual_fun_4_2_9, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-2-9-%E5%8F%91%E9%80%81%E8%AE%A2%E5%8D%95%E5%8D%A1%E7%89%87%EF%BC%8C%E4%BB%85%E4%BA%BA%E5%B7%A5%E6%A8%A1%E5%BC%8F%E4%B8%8B%E6%94%AF%E6%8C%81-%E8%AE%A2%E5%8D%95%E5%8D%A1%E7%89%87%E7%82%B9%E5%87%BB%E4%BA%8B%E4%BB%B6%E5%8F%AF%E6%8B%A6%E6%88%AA");
        tv_manual_fun_4_2_13 = findViewById(R.id.tv_manual_fun_4_2_13);
        tv_manual_fun_4_2_13.setText("https://www.sobot.com/developerdocs/app_sdk/android.html#_4-2-13-转人工后可隐藏“+”号菜单栏中的按钮");
        setOnClick(tv_manual_fun_4_2_13, "https://www.sobot.com/developerdocs/app_sdk/android.html#_4-2-13-%E8%BD%AC%E4%BA%BA%E5%B7%A5%E5%90%8E%E5%8F%AF%E9%9A%90%E8%97%8F%E2%80%9C-%E2%80%9D%E5%8F%B7%E8%8F%9C%E5%8D%95%E6%A0%8F%E4%B8%AD%E7%9A%84%E6%8C%89%E9%92%AE");
    }

    public void setOnClick(TextView view, final String url) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("url", url);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sobot_tv_save) {
            if (information != null) {
                String groupid = sobot_et_groupid.getText().toString().trim();
                information.setGroupid(groupid);
                String choose_adminid = sobot_et_choose_adminid.getText().toString().trim();
                information.setChoose_adminid(choose_adminid);
                String tranReceptionistFlag = sobot_et_tranReceptionistFlag.getText().toString().trim();
                information.setTranReceptionistFlag(Integer.parseInt(tranReceptionistFlag));
                String customer_fields = sobot_et_customer_fields.getText().toString().trim();
                information.setCustomer_fields(customer_fields);

                String autoSendMsgMode = sobot_et_autoSendMsgMode.getText().toString().trim();
                String autoSendMsgcontent = sobot_et_autoSendMsgcontent.getText().toString().trim();
                String autoSendMsgtype = sobot_et_autoSendMsgtype.getText().toString().trim();
                String autoSendMsgCount = sobot_et_autoSendMsg_count.getText().toString().trim();
                if (!TextUtils.isEmpty(autoSendMsgMode) && !TextUtils.isEmpty(autoSendMsgcontent) && !"0".equals(autoSendMsgMode)) {
                    SobotAutoSendMsgMode sobotAutoSendMsgMode = null;
                    if ("1".equals(autoSendMsgMode)) {
                        sobotAutoSendMsgMode = SobotAutoSendMsgMode.SendToRobot;
                    } else if ("2".equals(autoSendMsgMode)) {
                        sobotAutoSendMsgMode = SobotAutoSendMsgMode.SendToOperator;
                    } else if ("3".equals(autoSendMsgMode)) {
                        sobotAutoSendMsgMode = SobotAutoSendMsgMode.SendToAll;
                    }
                    if (sobotAutoSendMsgMode != null) {
                        sobotAutoSendMsgMode.setIsEveryTimeAutoSend("0".equals(autoSendMsgCount));
                        if (TextUtils.isEmpty(autoSendMsgtype)) {
                            information.setAutoSendMsgMode(sobotAutoSendMsgMode.setContent(autoSendMsgcontent));
                        } else {
                            if ("1".equals(autoSendMsgtype) || "12".equals(autoSendMsgtype) || "23".equals(autoSendMsgtype)) {
                                information.setAutoSendMsgMode(sobotAutoSendMsgMode.setContent(CommonUtils.getSDCardRootPath(SobotManualFunctionActivity.this) + File.separator + autoSendMsgcontent).setAuto_send_msgtype(Integer.parseInt(autoSendMsgtype)));
                            } else {
                                information.setAutoSendMsgMode(sobotAutoSendMsgMode.setContent(autoSendMsgcontent).setAuto_send_msgtype(0));
                            }
                        }
                    }
                } else {
                    information.setAutoSendMsgMode(SobotAutoSendMsgMode.Default.setContent(autoSendMsgcontent).setAuto_send_msgtype(0).setIsEveryTimeAutoSend("0".equals(autoSendMsgCount)));
                }

                String queue_First = sobot_et_queue_First.getText().toString().trim();
                information.setIs_Queue_First("1".equals(queue_First) ? true : false);
                String summary_params = sobot_et_summary_params.getText().toString().trim();
                information.setSummary_params(summary_params);
                String multi_params = sobot_et_multi_params.getText().toString().trim();
                information.setMulti_params(multi_params);
                if (status428) {
                    //咨询内容
                    ConsultingContent consultingContent = new ConsultingContent();
                    //咨询内容标题，必填
                    consultingContent.setSobotGoodsTitle("XXX超级电视50英寸2D智能LED黑色");
                    //咨询内容图片，选填 但必须是图片地址
                    consultingContent.setSobotGoodsImgUrl("http://www.li7.jpg");
                    //咨询来源页，必填
                    consultingContent.setSobotGoodsFromUrl("www.sobot.com");
                    //描述，选填
                    consultingContent.setSobotGoodsDescribe("XXX超级电视 S5");
                    //标签，选填
                    consultingContent.setSobotGoodsLable("￥2150");
                    //转人工后是否自动发送
                    consultingContent.setAutoSend(true);
                    //启动智齿客服页面 在Information 添加,转人工发送卡片消息
                    information.setConsultingContent(consultingContent);
                } else {
                    information.setConsultingContent(null);
                }
                if (status429) {
                    List<OrderCardContentModel.Goods> goodsList = new ArrayList<>();
                    goodsList.add(new OrderCardContentModel.Goods("苹果", "https://img.sobot.com/chatres/66a522ea3ef944a98af45bac09220861/msg/20190930/7d938872592345caa77eb261b4581509.png"));
                    goodsList.add(new OrderCardContentModel.Goods("苹果1111111", "https://img.sobot.com/chatres/66a522ea3ef944a98af45bac09220861/msg/20190930/7d938872592345caa77eb261b4581509.png"));
                    goodsList.add(new OrderCardContentModel.Goods("苹果2222", "https://img.sobot.com/chatres/66a522ea3ef944a98af45bac09220861/msg/20190930/7d938872592345caa77eb261b4581509.png"));
                    goodsList.add(new OrderCardContentModel.Goods("苹果33333333", "https://img.sobot.com/chatres/66a522ea3ef944a98af45bac09220861/msg/20190930/7d938872592345caa77eb261b4581509.png"));
                    OrderCardContentModel orderCardContent = new OrderCardContentModel();
                    //订单编号（必填）
                    orderCardContent.setOrderCode("zc32525235425");
                    //订单状态
                    //待付款:1 待发货:2 运输中:3  派送中:4  已完成:5  待评价:6 已取消:7
                    orderCardContent.setOrderStatus(1);
                    //订单总金额(单位 分)
                    orderCardContent.setTotalFee(1234);
                    //订单商品总数
                    orderCardContent.setGoodsCount("4");
                    //订单链接
                    orderCardContent.setOrderUrl("https://item.jd.com/1765513297.html");
                    //订单创建时间
                    orderCardContent.setCreateTime(System.currentTimeMillis() + "");
                    //转人工后是否自动发送
                    orderCardContent.setAutoSend(true);
                    //订单商品集合
                    orderCardContent.setGoods(goodsList);
                    //订单卡片内容
                    information.setOrderGoodsInfo(orderCardContent);
                } else {
                    information.setOrderGoodsInfo(null);
                }
                information.setIsVip(status4211 ? "1" : "0");

                String vip_level = sobot_et_vip_level.getText().toString().trim();
                information.setVip_level(vip_level);
                String user_label = sobot_et_user_label.getText().toString().trim();
                information.setUser_label(user_label);
                information.setHideMenuSatisfaction(status42131);
                information.setHideMenuLeave(status42132);
                information.setHideMenuPicture(status42133);
                information.setHideMenuVedio(status42134);
                information.setHideMenuCamera(status42135);
                information.setHideMenuFile(status42136);
                information.setHideMenuManualLeave(status42137);
                SobotSPUtil.saveObject(this, "sobot_demo_infomation", information);
            }
            ToastUtil.showToast(getContext(), "已保存");
            finish();
        } else if (v.getId() == R.id.sobot_rl_4_2_8) {
            status428 = !status428;
            setImageShowStatus(status428, sobotImage428);
            if (otherModel != null) {
                otherModel.setUserConsultingContentDemo(status428);
                SobotSPUtil.saveObject(this, "sobot_demo_otherModel", otherModel);
            }
        } else if (v.getId() == R.id.sobot_rl_4_2_9) {
            status429 = !status429;
            setImageShowStatus(status429, sobotImage429);
            if (otherModel != null) {
                otherModel.setUserOrderCardContentModelDemo(status429);
                SobotSPUtil.saveObject(this, "sobot_demo_otherModel", otherModel);
            }
        } else if (v.getId() == R.id.sobot_rl_4_2_11) {
            status4211 = !status4211;
            setImageShowStatus(status4211, sobotImage4211);
        } else if (v.getId() == R.id.sobot_rl_4_2_13_1) {
            status42131 = !status42131;
            setImageShowStatus(status42131, sobotImage42131);
        } else if (v.getId() == R.id.sobot_rl_4_2_13_2) {
            status42132 = !status42132;
            setImageShowStatus(status42132, sobotImage42132);
        } else if (v.getId() == R.id.sobot_rl_4_2_13_3) {
            status42133 = !status42133;
            setImageShowStatus(status42133, sobotImage42133);
        } else if (v.getId() == R.id.sobot_rl_4_2_13_4) {
            status42134 = !status42134;
            setImageShowStatus(status42134, sobotImage42134);
        } else if (v.getId() == R.id.sobot_rl_4_2_13_5) {
            status42135 = !status42135;
            setImageShowStatus(status42135, sobotImage42135);
        } else if (v.getId() == R.id.sobot_rl_4_2_13_6) {
            status42136 = !status42136;
            setImageShowStatus(status42136, sobotImage42136);
        } else if (v.getId() == R.id.sobot_rl_4_2_13_7) {
            status42137 = !status42137;
            setImageShowStatus(status42137, sobotImage42137);
        }
    }


    private void setImageShowStatus(boolean status, ImageView imageView) {
        if (status) {
            imageView.setBackgroundResource(R.drawable.sobot_demo_icon_open);
        } else {
            imageView.setBackgroundResource(R.drawable.sobot_demo_icon_close);
        }
    }

    public Context getContext() {
        return this;
    }
}