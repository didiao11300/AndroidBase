package com.maosong.component.activity;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.maosong.component.BaseActivity;
import com.maosong.component.R;
import com.maosong.component.view.BasePresenter;
import com.maosong.tools.LogUtil;

/***
 *  支持jsbridge的webview
 * edit by游飞
 *  js bridge 通信原理
 * js->java
 * 			1，注册// 原生方式，注册交互的类
 *         webview.addJavascriptInterface(new JsToJava(), "stub");
 *          @JavascriptInterface
 *          js 端通过调用注册的方法window.stub.jsMethod
 *          xxx方法
 *          2，通过调用webview的onJspromot的js方法调用Android确定webview窗口，Android确定窗口来传参数调用
 *          3，通过iframe发送url变化，java端拦截shouldOverrideUrlLoading yy://调用或者开始yy://return返回
 *
 * jsva->js
 *			方式，webview.loadUrl("function:()")
 *
 *	这里采用方式3，在原来的html加载完成之后，在window注册我们的js文件，在assert中
 * @See code
 *	<pre>
 *	     String jsContent = assetFile2Str(view.getContext(), path);
 *         view.loadUrl("javascript:" + jsContent);
 *	</pre>
 * 然后html中有了这个变量
 * 	  window.WebViewJavascriptBridge.send
 *
 * 	  JSBRIDEGE protocol @See
 * 	  BridgeUtil.java
 *
 * 	  note this;
 * 	  如果这里 onPageFinish 中加载jsBridge失败,请在html中手动加载，主要还是import js文件
 * */
//@RouterRule(SchemaConstants.PAGE.WEB)
public class WebviewActivity<P extends BasePresenter> extends BaseActivity<P> {
    public static final String TAG = WebviewActivity.class.getSimpleName();
    public static final String KEY_TITLE = "_key_title";
    public static final String KEY_URL = "_key_url";

    private BridgeWebView webView;

    @Override
    public int getContentViewRes() {
        return R.layout.activity_webview;
    }

    @Override
    public P createPresenter() {
        return null;
    }

    @Override
    public void initView() {
        webView = findViewById(R.id.webView);

        webView.setDefaultHandler(new DefaultHandler());
//        //页面加载相关控制
//        BridgeWebViewClient client = new BridgeWebViewClient(webView);
//        webView.setWebViewClient(client);
//        //页面调用UI之间的控制
        webView.setWebChromeClient(hookWebChromeClient());
//        webView.setWebViewClient(hookWebviewClient());
        //设置websettings
        setWebSetting(webView.getSettings());

        String title = getIntent().getStringExtra(KEY_TITLE);
        setTopBarTitle(title);

        String url = getIntent().getStringExtra(KEY_URL);
        if (!TextUtils.isEmpty(url)) {
            webView.loadUrl(url);
        }
    }

    public BridgeWebView getWebView() {
        return webView;
    }

    /**
     * 给继承的钩子webChromeclient
     **/
    protected WebChromeClient hookWebChromeClient() {
        return new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.i("web", "onConsoleMessage" + consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }
        };
    }

    /**
     * 给继承的钩子webviewClient
     **/
    protected WebViewClient hookWebviewClient() {
        return new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LogUtil.i(TAG, "webview start");
//                showLoading("loading...");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LogUtil.i(TAG, "webview finished");
//                dismissLoading();
            }
        };
    }

    protected void setWebSetting(WebSettings webSettings) {
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //设置可以访问文件,考虑跨域的攻击
        webSettings.setAllowFileAccess(true);
        //设置支持缩放
        webSettings.setBuiltInZoomControls(true);
        // 支持缩放(适配到当前屏幕)
        webSettings.setSupportZoom(true);
        // 设置可以被显示的屏幕控制
        webSettings.setDisplayZoomControls(true);
        // 设置缓存
        webSettings.setAppCacheEnabled(true);
//        // 设置缓存模式,一共有四种模式
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        // 设置缓存路径
//        webSettings.setAppCachePath("/storage/emulated/0/Android/data/com.easyar.buddha/files");

        webSettings.setDomStorageEnabled(true);
        webSettings.supportMultipleWindows();
        webSettings.setAllowContentAccess(true);
        // 支持内容重新布局,一共有四种方式
        // 默认的是NARROW_COLUMNS
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // 将图片调整到合适的大小
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        // 让JavaScript可以自动打开windows
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
    }

    @Override
    protected boolean needFullScreen() {
        return true;
    }
}
