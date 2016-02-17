package com.survata.ui;


import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.survata.R;
import com.survata.Survey;
import com.survata.utils.Logger;

public class SurveyDialogFragment extends DialogFragment {

    public static final String TAG = "SurveyDialogFragment";
    private static final String PUBLISHER = "publisher";
    private static final String BRAND = "brand";
    private static final String EXPLAINER = "explainer";
    private static final String JS_INTERFACE_NAME = "Android";

    private WebView mWebView;
    private ImageView mCloseImage;

    private String mPublisher;
    private String mBrand;
    private String mExplainer;

    private Survey.SurveyStatusListener mSurveyStatusListener;

    public void setSurveyStatusListener(Survey.SurveyStatusListener surveyStatusListener) {
        mSurveyStatusListener = surveyStatusListener;
    }

    public static SurveyDialogFragment newInstance(String publisher, String brand, String explainer) {
        SurveyDialogFragment dialogFragment = new SurveyDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PUBLISHER, publisher);
        bundle.putString(BRAND, brand);
        bundle.putString(EXPLAINER, explainer);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public void dismissSurveyDialog() {

        Activity activity = getActivity();

        if (activity == null) {
            Logger.d(TAG, "activity is null");
            return;
        }


        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        Fragment fragment = activity.getFragmentManager().findFragmentByTag(SurveyDialogFragment.TAG);
        if (null != fragment) {
            ft.remove(fragment).commit();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mPublisher = bundle.getString(PUBLISHER);
            mBrand = bundle.getString(BRAND);
            mExplainer = bundle.getString(EXPLAINER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.survey_view, container, false);

        mWebView = (WebView) view.findViewById(R.id.web_view);
        mWebView.setVerticalScrollBarEnabled(false);
        mCloseImage = (ImageView) view.findViewById(R.id.close);

        mCloseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissSurveyDialog();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        createSurveyWall();
    }

    private class SurveyJavaScriptInterface {

        private Context mContext;

        public SurveyJavaScriptInterface(Context context) {
            mContext = context;
        }

        @JavascriptInterface
        public void onSurveyLoaded(String data) {
            Logger.d(TAG, "survey loaded");

            if (!TextUtils.isEmpty(data) && data.equals("monetizable")) {

            } else {

                if (mSurveyStatusListener != null) {
                    mSurveyStatusListener.onResult(Survey.SurveyResult.CREDIT_EARNED);
                }
            }
        }

        @JavascriptInterface
        public void onSurveyReady() {
            Logger.d(TAG, "survey ready");

            if (mSurveyStatusListener != null) {
                mSurveyStatusListener.onResult(Survey.SurveyResult.READY);
            }
        }

        @JavascriptInterface
        public void onInterviewStart() {
            Logger.d(TAG, "The interview is start.");

            if (mSurveyStatusListener != null) {
                mSurveyStatusListener.onResult(Survey.SurveyResult.STARTED);
            }
        }

        @JavascriptInterface
        public void onInterviewSkip() {
            Logger.d(TAG, "The interview is skip.");

            if (mSurveyStatusListener != null) {
                mSurveyStatusListener.onResult(Survey.SurveyResult.SKIPPED);
            }
        }

        @JavascriptInterface
        public void onInterviewComplete() {
            Logger.d(TAG, "The interview is complete");

            if (mSurveyStatusListener != null) {
                mSurveyStatusListener.onResult(Survey.SurveyResult.COMPLETED);
            }
        }

        @JavascriptInterface
        public void onFail() {
            Logger.d(TAG, "onFail");

            if (mSurveyStatusListener != null) {
                mSurveyStatusListener.onResult(Survey.SurveyResult.FAILED);
            }
        }
    }

    public void createSurveyWall() {
        Logger.d(TAG, "loading survey...");

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowUniversalAccessFromFileURLs(true);
            webSettings.setAllowFileAccessFromFileURLs(true);
        }

        mWebView.addJavascriptInterface(new SurveyJavaScriptInterface(getActivity()), JS_INTERFACE_NAME);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        mWebView.loadUrl("file:///android_asset/template.html");
    }
}
