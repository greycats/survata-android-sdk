package com.survata.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.survata.R;

public class SurveyView extends RelativeLayout {

    private static final String TAG = "SurveyView";

    public interface OnCloseCallback {
        void onClose();
    }

    private OnCloseCallback mOnCloseCallback;

    public void setOnCloseCallback(OnCloseCallback onCloseCallback) {
        mOnCloseCallback = onCloseCallback;
    }

    private Button mClose;
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

        mClose = (Button) findViewById(R.id.close);


        mClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mOnCloseCallback != null) {
                    mOnCloseCallback.onClose();
                }
            }
        });

        mWebView = (WebView) findViewById(R.id.web_view);
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.loading);
    }

    private class SurveyJavaScriptInterface {


    }

    public void createSurveyWall(String publisher, String brand, String explainer) {
        Log.d(TAG, "loading survey...");

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new SurveyJavaScriptInterface(), "survey");

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
                    mLoadingProgressBar.setVisibility(VISIBLE);
                } else {
                    mLoadingProgressBar.setVisibility(GONE);
                }
            }
        });


        mWebView.loadUrl("file:///android_asset/template.html");
    }

}
