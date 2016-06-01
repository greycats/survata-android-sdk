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

import java.util.Map;

public class SurveyDialogFragment extends DialogFragment {

    public static final String TAG = "SurveyDialogFragment";
    private static final String SURVEY_OPTION = "SurveyOption";
    private static final String ZIP_CODE = "zipcode";
    private static final String JS_INTERFACE_NAME = "Android";

    private WebView mWebView;
    private ImageView mCloseImage;

    private SurveyOption mSurveyOption;
    private String mZipCode;

    private Survey.SurveyStatusListener mSurveyStatusListener;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public void setSurveyStatusListener(Survey.SurveyStatusListener surveyStatusListener) {
        mSurveyStatusListener = surveyStatusListener;
    }

    public static SurveyDialogFragment newInstance(SurveyOption surveyOption, String zipCode) {
        SurveyDialogFragment dialogFragment = new SurveyDialogFragment();
        dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        Bundle bundle = new Bundle();
        bundle.putSerializable(SURVEY_OPTION, surveyOption);
        bundle.putSerializable(ZIP_CODE, zipCode);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mSurveyOption = (SurveyOption) bundle.getSerializable(SURVEY_OPTION);
            mZipCode = bundle.getString(ZIP_CODE);
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

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.stopLoading();

            mWebView.clearFormData();
            mWebView.clearAnimation();
            mWebView.clearDisappearingChildren();
            mWebView.clearHistory();
            mWebView.destroyDrawingCache();
            mWebView.destroy();

            mWebView = null;
        }

        super.onDestroy();
    }

    private class SurveyJavaScriptInterface {

        @JavascriptInterface
        public void onSurveyLoaded(Object data) {
            Logger.d(TAG, "survey loaded" + data);

            if (data != null && data instanceof Map) {
                Map map = (Map) data;
                if ("monetizable".equals(map.get("status"))) {
                    //continue
                } else {
                    updateResult(Survey.SurveyEvents.CREDIT_EARNED);
                }
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
        public void noSurveyAvailable() {
            Logger.d(TAG, "noSurveyAvailable");

            updateResult(Survey.SurveyEvents.NO_SURVEY_AVAILABLE);
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

                if (surveyEvents == Survey.SurveyEvents.COMPLETED
                        || surveyEvents == Survey.SurveyEvents.CREDIT_EARNED
                        || surveyEvents == Survey.SurveyEvents.NO_SURVEY_AVAILABLE
                        || surveyEvents == Survey.SurveyEvents.SKIPPED) {
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

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        mWebView.addJavascriptInterface(new SurveyJavaScriptInterface(), JS_INTERFACE_NAME);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (mWebView != null) {
                    mWebView.clearAnimation();
                    mWebView.clearDisappearingChildren();
                    mWebView.destroyDrawingCache();
                }
            }
        });

        String html = Utils.getFromAssets("template.html", getActivity());

        String publisher = mSurveyOption.publisher;

        Map<String, String> params = mSurveyOption.getParams();
        params.put("postalCode", mZipCode);
        String option = Utils.parseParamMap(params);

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
