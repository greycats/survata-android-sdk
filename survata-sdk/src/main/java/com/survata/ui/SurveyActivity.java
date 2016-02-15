package com.survata.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.survata.R;

public class SurveyActivity extends Activity {

    private static final String TAG = "SurveyView";
    private static final String PUBLISHER = "publisher";
    private static final String BRAND = "brand";
    private static final String EXPLAINER = "explainer";

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private WebView mWebView;
    private ProgressBar mLoadingProgressBar;
    private ImageView mCloseImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_view);

        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.setVerticalScrollBarEnabled(false);
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.loading);
        mCloseImage = (ImageView) findViewById(R.id.close);

        mCloseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SurveyActivity.this.finish();
            }
        });

        createSurveyWall();
    }

    private class SurveyJavaScriptInterface {

        private Context mContext;

        public SurveyJavaScriptInterface(Context context) {
            mContext = context;
        }

        @JavascriptInterface
        public void onLoad(Object data) {
            Log.d(TAG, "onLoad: " + data);

            mCloseImage.setVisibility(View.VISIBLE);

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
            SurveyActivity.this.finish();
        }
    }

    public static void start(Activity activity, String publisher, String brand, String explainer) {
        Intent intent = new Intent(activity, SurveyActivity.class);
        intent.putExtra(PUBLISHER, publisher);
        intent.putExtra(BRAND, brand);
        intent.putExtra(EXPLAINER, explainer);

        activity.startActivity(intent);
    }


    public void createSurveyWall() {
        Log.d(TAG, "loading survey...");

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

        mWebView.addJavascriptInterface(new SurveyJavaScriptInterface(this), "survey");

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
