package com.survata.view;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.survata.R;

public class SurveyView extends RelativeLayout {

    private static final String TAG = "SurveyView";

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private WebView mWebView;
    private ProgressBar mLoadingProgressBar;

    public SurveyView(Context context) {
        this(context, null);
    }

    public SurveyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SurveyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.survey_view, this);

        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.setVerticalScrollBarEnabled(false);
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.loading);
    }

    private class SurveyJavaScriptInterface {

        private Context mContext;

        public SurveyJavaScriptInterface(Context context) {
            mContext = context;
        }

        @JavascriptInterface
        public void onLoad(Object data) {
            Log.d(TAG, "onLoad: " + data);

//            if ("monetizable" === data.status) {
//                alert("Survata has a monetizable interview for the current user.");
//            }
//            else {
//                alert("Survata does not have a monetizable interview for the current user.");
//            }
        }

        @JavascriptInterface
        public void onInterviewComplete() {
            Log.d(TAG, "The interview is complete.  Here is your premium content.");
        }

        @JavascriptInterface
        public void onInterviewStart() {
            Log.d(TAG, "The interview has started.");
        }

        @JavascriptInterface
        public void onInterviewSkip() {
            Log.d(TAG, "You skipped the interview.  Enjoy the content anyway.");
        }

        @JavascriptInterface
        public void onReady() {
            Log.d(TAG, "onReady");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:startInterview()");
                }
            });
        }

        @JavascriptInterface
        public void onFail() {
            Log.d(TAG, "onFail");
        }
    }

    public void createSurveyWall(Context context, String publisher, String brand, String explainer) {
        Log.d(TAG, "loading survey...");

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowUniversalAccessFromFileURLs(true);
            webSettings.setAllowFileAccessFromFileURLs(true);
        }

        mWebView.addJavascriptInterface(new SurveyJavaScriptInterface(context), "survey");

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mLoadingProgressBar.setVisibility(GONE);
                } else {
                    mLoadingProgressBar.setVisibility(VISIBLE);
                }
            }
        });

        mWebView.loadUrl("file:///android_asset/template.html");
    }
}
