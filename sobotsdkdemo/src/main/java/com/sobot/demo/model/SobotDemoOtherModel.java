package com.sobot.demo.model;

import java.io.Serializable;

public class SobotDemoOtherModel implements Serializable {
    // 域名
    private String api_host;
    // 平台标识 请联系对应的客服申请
    private String platformUnionCode;
    // 平台秘钥 请联系对应的客服申请
    private String platformSecretkey;
    //是否使用商品卡片demo
    private boolean isUserConsultingContentDemo;
    //是否使用订单卡片demo
    private boolean isUserOrderCardContentModelDemo;

    public String getPlatformUnionCode() {
        return platformUnionCode;
    }

    public void setPlatformUnionCode(String platformUnionCode) {
        this.platformUnionCode = platformUnionCode;
    }

    public String getPlatformSecretkey() {
        return platformSecretkey;
    }

    public void setPlatformSecretkey(String platformSecretkey) {
        this.platformSecretkey = platformSecretkey;
    }

    public boolean isUserConsultingContentDemo() {
        return isUserConsultingContentDemo;
    }

    public void setUserConsultingContentDemo(boolean userConsultingContentDemo) {
        isUserConsultingContentDemo = userConsultingContentDemo;
    }

    public boolean isUserOrderCardContentModelDemo() {
        return isUserOrderCardContentModelDemo;
    }

    public void setUserOrderCardContentModelDemo(boolean userOrderCardContentModelDemo) {
        isUserOrderCardContentModelDemo = userOrderCardContentModelDemo;
    }

    public String getApi_host() {
        return api_host;
    }

    public void setApi_host(String api_host) {
        this.api_host = api_host;
    }
}
