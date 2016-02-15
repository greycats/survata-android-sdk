package com.survata.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.survata.R;
import com.survata.Survey;
import com.survata.Util.Logger;

public class SurveyActivity extends Activity {

    private static final String TAG = "SurveyView";
    private static final String PUBLISHER = "publisher";
    private static final String BRAND = "brand";
    private static final String EXPLAINER = "explainer";
    private static final String JS_INTERFACE_NAME = "Android";

    private WebView mWebView;
    private ProgressBar mLoadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_view);

        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.setVerticalScrollBarEnabled(false);
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.loading);

        createSurveyWall();
    }

    private class SurveyJavaScriptInterface {

        private Context mContext;

        public SurveyJavaScriptInterface(Context context) {
            mContext = context;
        }

        @JavascriptInterface
        public void onInterviewComplete() {
            Logger.d(TAG, "The interview is complete.  Here is your premium content.");
            setResult(RESULT_OK);
            SurveyActivity.this.finish();
        }

        @JavascriptInterface
        public void onFail() {
            Logger.d(TAG, "onFail");
            SurveyActivity.this.finish();
        }
    }

    public static void start(Activity activity, String publisher, String brand, String explainer) {
        Intent intent = new Intent(activity, SurveyActivity.class);
        intent.putExtra(PUBLISHER, publisher);
        intent.putExtra(BRAND, brand);
        intent.putExtra(EXPLAINER, explainer);

        activity.startActivityForResult(intent, Survey.REQUEST_SHOW_SURVEY);
    }

    public void createSurveyWall() {
        Logger.d(TAG, "loading survey...");

        String publisher = getIntent().getStringExtra(PUBLISHER);
        String brand = getIntent().getStringExtra(BRAND);
        String explainer = getIntent().getStringExtra(EXPLAINER);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowUniversalAccessFromFileURLs(true);
            webSettings.setAllowFileAccessFromFileURLs(true);
        }

        mWebView.addJavascriptInterface(new SurveyJavaScriptInterface(this), JS_INTERFACE_NAME);

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
                    mLoadingProgressBar.setVisibility(View.GONE);
                } else {
                    mLoadingProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        mWebView.loadUrl("file:///android_asset/template.html");
    }
}
