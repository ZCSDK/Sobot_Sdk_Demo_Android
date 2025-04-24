package com.sobot.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.sobot.chat.R;
import com.sobot.chat.SobotApi;
import com.sobot.chat.activity.base.SobotBaseHelpCenterActivity;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.api.model.Information;
import com.sobot.chat.api.model.StDocModel;
import com.sobot.chat.api.model.StHelpDocModel;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.listener.SobotFunctionType;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SobotOption;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.network.http.callback.StringResultCallBack;

/**
 * 帮助中心问题详情
 */
public class SobotProblemDetailActivity extends SobotBaseHelpCenterActivity implements View.OnClickListener {
    public static final String EXTRA_KEY_DOC = "extra_key_doc";

    private StDocModel mDoc;
    private WebView mWebView;
    private View mBottomBtn;
    private TextView tv_sobot_layout_online_service;
    private TextView tv_sobot_layout_online_tel;

    private TextView mProblemTitle;
    private TextView tvOnlineService;

    @Override
    protected int getContentViewResId() {
        return getResLayoutId("sobot_activity_problem_detail");
    }

    public static Intent newIntent(Context context, Information information, StDocModel data) {
        Intent intent = new Intent(context, SobotProblemDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ZhiChiConstant.SOBOT_BUNDLE_INFO, information);
        intent.putExtra(ZhiChiConstant.SOBOT_BUNDLE_INFORMATION, bundle);
        intent.putExtra(EXTRA_KEY_DOC, data);
        return intent;
    }

    @Override
    protected void initBundleData(Bundle savedInstanceState) {
        super.initBundleData(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            mDoc = (StDocModel) intent.getSerializableExtra(EXTRA_KEY_DOC);
        }
    }

    @Override
    protected void initView() {
        showLeftMenu(getResDrawableId("sobot_btn_back_grey_selector"), "", true);
        setTitle(getResString("sobot_problem_detail_title"));
        mBottomBtn = findViewById(getResId("ll_bottom"));
        tv_sobot_layout_online_service = findViewById(getResId("tv_sobot_layout_online_service"));
        tv_sobot_layout_online_tel = findViewById(getResId("tv_sobot_layout_online_tel"));
        mProblemTitle = findViewById(getResId("sobot_text_problem_title"));
        mWebView = (WebView) findViewById(getResId("sobot_webView"));
        tvOnlineService = findViewById(getResId("tv_sobot_layout_online_service"));
        tvOnlineService.setText(ResourceUtils.getResString(this, "sobot_help_center_online_service"));
        tv_sobot_layout_online_service.setOnClickListener(this);
        tv_sobot_layout_online_tel.setOnClickListener(this);
        if (mInfo != null && !TextUtils.isEmpty(mInfo.getHelpCenterTelTitle()) && !TextUtils.isEmpty(mInfo.getHelpCenterTel())) {
            tv_sobot_layout_online_tel.setVisibility(View.VISIBLE);
            tv_sobot_layout_online_tel.setText(mInfo.getHelpCenterTelTitle());
        } else {
            tv_sobot_layout_online_tel.setVisibility(View.GONE);
        }
        initWebView();
        displayInNotch(mWebView);
        displayInNotch(mProblemTitle);
        displayInNotch(mBottomBtn);
    }

    @Override
    protected void initData() {
        ZhiChiApi api = SobotMsgManager.getInstance(getApplicationContext()).getZhiChiApi();
        api.getHelpDocByDocId(SobotProblemDetailActivity.this, mInfo.getApp_key(), mDoc.getDocId(), new StringResultCallBack<StHelpDocModel>() {

            @Override
            public void onSuccess(StHelpDocModel data) {
                mProblemTitle.setText(data.getQuestionTitle());
                String answerDesc = data.getAnswerDesc();
                if (!TextUtils.isEmpty(answerDesc)) {
                    int zinyanColor = getResources().getColor(R.color.sobot_common_wenzi_black);
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("#");
                    stringBuffer.append(Integer.toHexString(Color.red(zinyanColor)));
                    stringBuffer.append(Integer.toHexString(Color.green(zinyanColor)));
                    stringBuffer.append(Integer.toHexString(Color.blue(zinyanColor)));
                    //修改图片高度为自适应宽度
                    answerDesc = "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "    <head>\n" +
                            "        <meta charset=\"utf-8\">\n" +
                            "        <title></title>\n" +
                            "        <style>\n body{color:" + (stringBuffer != null ? stringBuffer.toString() : "") +
                            ";}\n" +
                            "            img{\n" +
                            "                width: auto;\n" +
                            "                height:auto;\n" +
                            "                max-height: 100%;\n" +
                            "                max-width: 100%;\n" +
                            "            }" +
                            "            video{\n" +
                            "                width: auto;\n" +
                            "                height:auto;\n" +
                            "                max-height: 100%;\n" +
                            "                max-width: 100%;\n" +
                            "            }" +
                            "        </style>\n" +
                            "    </head>\n" +
                            "    <body>" + answerDesc + "  </body>\n" +
                            "</html>";
                    //显示文本内容
                    String html = answerDesc.replace("<p>", "").replace("</p>", "<br/>").replace("<P>", "").replace("</P>", "<br/>");
                    mWebView.loadDataWithBaseURL("about:blank", html, "text/html", "utf-8", null);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                ToastUtil.showToast(getApplicationContext(), des);
            }
        });
    }


    private void initWebView() {
        if (Build.VERSION.SDK_INT >= 11) {
            try {
                mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
            } catch (Exception e) {
                //ignor
            }
        }
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                //检测到下载文件就打开系统浏览器
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri content = Uri.parse(url);
                intent.setData(content);
                startActivity(intent);
            }
        });
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        mWebView.getSettings().setDefaultFontSize(14);
        mWebView.getSettings().setTextZoom(100);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(false);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.setBackgroundColor(0);

        // 设置可以使用localStorage
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setBlockNetworkImage(false);
        mWebView.getSettings().setSavePassword(false);
        // mWebView.getSettings().setUserAgentString(mWebView.getSettings().getUserAgentString() + " sobot");

        //关于webview的http和https的混合请求的，从Android5.0开始，WebView默认不支持同时加载Https和Http混合模式。
        // 在API>=21的版本上面默认是关闭的，在21以下就是默认开启的，直接导致了在高版本上面http请求不能正确跳转。
        if (Build.VERSION.SDK_INT >= 21) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

        //Android 4.4 以下的系统中存在一共三个有远程代码执行漏洞的隐藏接口
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        mWebView.removeJavascriptInterface("accessibility");
        mWebView.removeJavascriptInterface("accessibilityTraversal");

        // 应用可以有数据库
        mWebView.getSettings().setDatabaseEnabled(true);

        //把html中的内容放大webview等宽的一列中
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                imgReset();
            }

            @Override
            // 在点击请求的是链接是才会调用，重写此方法返回true表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边。
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (SobotOption.hyperlinkListener != null) {
                    SobotOption.hyperlinkListener.onUrlClick(url);
                    return true;
                }
                if (SobotOption.newHyperlinkListener != null) {
                    //如果返回true,拦截;false 不拦截
                    boolean isIntercept = SobotOption.newHyperlinkListener.onUrlClick(getSobotBaseActivity(), url);
                    if (isIntercept) {
                        return true;
                    }
                }
                return false;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);

            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
//                if (newProgress > 0 && newProgress < 100) {
//                    mProgressBar.setVisibility(View.VISIBLE);
//                    mProgressBar.setProgress(newProgress);
//                } else if (newProgress == 100) {
//                    mProgressBar.setVisibility(View.GONE);
//                }
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                chooseAlbumPic();
                return true;
            }

        });
    }

    private static final int REQUEST_CODE_ALBUM = 0x0111;

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;

    /**
     * 选择相册照片
     */
    private void chooseAlbumPic() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), REQUEST_CODE_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ALBUM) {
            if (uploadMessage == null && uploadMessageAboveL == null) {
                return;
            }
            if (resultCode != RESULT_OK) {
                //一定要返回null,否则<input file> 就是没有反应
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }
                if (uploadMessageAboveL != null) {
                    uploadMessageAboveL.onReceiveValue(null);
                    uploadMessageAboveL = null;

                }
            }

            if (resultCode == RESULT_OK) {
                Uri imageUri = null;
                switch (requestCode) {
                    case REQUEST_CODE_ALBUM:

                        if (data != null) {
                            imageUri = data.getData();
                        }
                        break;
                }

                //上传文件
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(imageUri);
                    uploadMessage = null;
                }
                if (uploadMessageAboveL != null) {
                    uploadMessageAboveL.onReceiveValue(new Uri[]{imageUri});
                    uploadMessageAboveL = null;
                }
            }
        }
    }

    /**
     * 对图片进行重置大小，宽度就是手机屏幕宽度，高度根据宽度比便自动缩放
     **/
    private void imgReset() {
        mWebView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName('img'); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "var img = objs[i];   " +
                "    img.style.maxWidth = '100%'; img.style.height = 'auto';  " +
                "}" +
                "})()");
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (mWebView != null) {
            mWebView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.removeAllViews();
            final ViewGroup viewGroup = (ViewGroup) mWebView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(mWebView);
            }
            mWebView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v == tv_sobot_layout_online_service) {
            if (SobotOption.openChatListener != null) {
                boolean isIntercept = SobotOption.openChatListener.onOpenChatClick(getSobotBaseActivity(), mInfo);
                if (isIntercept) {
                    return;
                }
            }
            SobotApi.startSobotChat(getApplicationContext(), mInfo);
        }
        if (v == tv_sobot_layout_online_tel) {
            if (!TextUtils.isEmpty(mInfo.getHelpCenterTel())) {
                if (SobotOption.functionClickListener != null) {
                    SobotOption.functionClickListener.onClickFunction(getSobotBaseActivity(), SobotFunctionType.ZC_PhoneCustomerService);
                }
                if (SobotOption.newHyperlinkListener != null) {
                    boolean isIntercept = SobotOption.newHyperlinkListener.onPhoneClick(getSobotBaseActivity(), "tel:" + mInfo.getHelpCenterTel());
                    if (isIntercept) {
                        return;
                    }
                }
                ChatUtils.callUp(mInfo.getHelpCenterTel(), getSobotBaseActivity());
            }
        }
    }
}