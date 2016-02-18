package com.survata.ui;


import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.survata.R;
import com.survata.Survey;
import com.survata.SurveyOption;
import com.survata.utils.Logger;
import com.survata.utils.Utils;

public class SurveyDialogFragment extends DialogFragment {

    public static final String TAG = "SurveyDialogFragment";
    private static final String PUBLISHER = "publisher";
    private static final String SURVEY_OPTION = "SurveyOption";
    private static final String JS_INTERFACE_NAME = "Android";

    private WebView mWebView;
    private ImageView mCloseImage;

    private String mPublisher;
    private SurveyOption mSurveyOption;

    private Survey.SurveyStatusListener mSurveyStatusListener;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public void setSurveyStatusListener(Survey.SurveyStatusListener surveyStatusListener) {
        mSurveyStatusListener = surveyStatusListener;
    }

    public static SurveyDialogFragment newInstance(String publisher, SurveyOption surveyOption) {
        SurveyDialogFragment dialogFragment = new SurveyDialogFragment();
        dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        Bundle bundle = new Bundle();
        bundle.putString(PUBLISHER, publisher);
        bundle.putSerializable(SURVEY_OPTION, surveyOption);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mPublisher = bundle.getString(PUBLISHER);
            mSurveyOption = (SurveyOption) bundle.getSerializable(SURVEY_OPTION);
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

        // start create survey wall
        createSurveyWall();
    }

    private class SurveyJavaScriptInterface {

        @JavascriptInterface
        public void onSurveyLoaded(String data) {
            Logger.d(TAG, "survey loaded");

            if (!TextUtils.isEmpty(data) && data.equals("monetizable")) {

            } else {
                updateResult(Survey.SurveyResult.CREDIT_EARNED);
            }
        }

        @JavascriptInterface
        public void onSurveyReady() {
            Logger.d(TAG, "survey ready");

            updateResult(Survey.SurveyResult.READY);
        }

        @JavascriptInterface
        public void onInterviewStart() {
            Logger.d(TAG, "The interview is start.");

            updateResult(Survey.SurveyResult.STARTED);
        }

        @JavascriptInterface
        public void onInterviewSkip() {
            Logger.d(TAG, "The interview is skip.");

            updateResult(Survey.SurveyResult.SKIPPED);
        }

        @JavascriptInterface
        public void onInterviewComplete() {
            Logger.d(TAG, "The interview is complete");

            updateResult(Survey.SurveyResult.COMPLETED);
        }

        @JavascriptInterface
        public void onFail() {
            Logger.d(TAG, "onFail");

            updateResult(Survey.SurveyResult.FAILED);
        }
    }

    private void updateResult(final Survey.SurveyResult surveyResult) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {

                if (surveyResult == Survey.SurveyResult.COMPLETED) {
                    dismissSurveyDialog();
                }

                if (mSurveyStatusListener != null) {
                    mSurveyStatusListener.onResult(surveyResult);
                }
            }
        });
    }

    private void createSurveyWall() {
        Logger.d(TAG, "loading survey...");

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowUniversalAccessFromFileURLs(true);
            webSettings.setAllowFileAccessFromFileURLs(true);
        }

        mWebView.addJavascriptInterface(new SurveyJavaScriptInterface(), JS_INTERFACE_NAME);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        String html = Utils.getFromAssets("template.html", getActivity());

        String data = html.replace("[PUBLISHER_ID]", mPublisher)
                .replace("[OPTION]", mSurveyOption.description())
                .replace("[LOADER_BASE64]", Utils.encodeImage(getActivity(), "circles_large.gif"));

        mWebView.loadDataWithBaseURL("https://www.survata.com", data, "text/html", "utf-8", null);
    }

    public void dismissSurveyDialog() {

        Activity activity = getActivity();

        if (activity == null) {
            Logger.d(TAG, "activity is null");
            return;
        }

        FragmentManager fragmentManager = activity.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(SurveyDialogFragment.TAG);
        if (fragment != null) {
            fragmentTransaction.remove(fragment).commit();
        }
    }
}
