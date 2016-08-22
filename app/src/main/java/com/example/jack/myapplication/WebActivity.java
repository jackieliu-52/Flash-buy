package com.example.jack.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jack on 2016/8/17.
 */
public class WebActivity extends AppCompatActivity {
    private String loadUrl;

    @BindView(R.id.webView)
    WebView m_webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        loadUrl = getIntent().getStringExtra("url");
        m_webView.loadUrl(loadUrl);

        //用内部的WebView去加载
        m_webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                return true;
            }
        });
        //更改默认的404页面
        m_webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                view.loadUrl("file:///android_assets/error.html");
                                       }
            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                switch(errorCode)
                {
                    case 404:
                        view.loadUrl("file:///android_assets/error.html");
                        break;
                }
            }
        });
        m_webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成

                } else {
                    // 加载中

                }

            }
        });
        //进行一些设置
        startSetting();
    }

    /**
     * 更改一些WebView的设置
     */
    private void startSetting(){
        WebSettings webSettings =   m_webView.getSettings();
        webSettings.setJavaScriptEnabled(true);  //支持js
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);  //支持缩放
        //关闭webView中缓存
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

    }

    @Override
    public void onDestroy() {
        loadUrl = "";
        m_webView.loadUrl(loadUrl);
        super.onDestroy();

    }
}
