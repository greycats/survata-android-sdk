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
    private static final String SURVEY_OPTION = "SurveyOption";
    private static final String JS_INTERFACE_NAME = "Android";

    private WebView mWebView;
    private ImageView mCloseImage;

    private SurveyOption mSurveyOption;

    private Survey.SurveyStatusListener mSurveyStatusListener;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public void setSurveyStatusListener(Survey.SurveyStatusListener surveyStatusListener) {
        mSurveyStatusListener = surveyStatusListener;
    }

    public static SurveyDialogFragment newInstance(SurveyOption surveyOption) {
        SurveyDialogFragment dialogFragment = new SurveyDialogFragment();
        dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        Bundle bundle = new Bundle();
        bundle.putSerializable(SURVEY_OPTION, surveyOption);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
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
                updateResult(Survey.SurveyEvents.CANCELED);
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
                //continue
            } else {
                updateResult(Survey.SurveyEvents.CREDIT_EARNED);
            }
        }

        @JavascriptInterface
        public void onInterviewStart() {
            Logger.d(TAG, "The interview is start.");
        }

        @JavascriptInterface
        public void onInterviewSkip() {
            Logger.d(TAG, "The interview is skip.");

            updateResult(Survey.SurveyEvents.SKIPPED);
        }

        @JavascriptInterface
        public void onInterviewComplete() {
            Logger.d(TAG, "The interview is complete");

            updateResult(Survey.SurveyEvents.COMPLETED);
        }

        @JavascriptInterface
        public void onFail() {
            Logger.d(TAG, "onFail");
        }
    }

    private void updateResult(final Survey.SurveyEvents surveyEvents) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {

                if (surveyEvents == Survey.SurveyEvents.COMPLETED) {
                    dismissSurveyDialog();
                }

                if (mSurveyStatusListener != null) {
                    mSurveyStatusListener.onEvent(surveyEvents);
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

        String publisher = mSurveyOption.publisher;
        String option = Utils.parseParamMap(mSurveyOption.getParams());

        String data = html.replace("[PUBLISHER_ID]", publisher)
                .replace("[OPTION]", option)
                .replace("[LOADER_BASE64]", Utils.encodeImage(getActivity(), "survata-spinner.png"));

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
