package com.survata.demo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.survata.demo.R;

public class TestFragment extends Fragment {

    public static final int INDEX = 2;

    public int getTitleResId() {
        return R.string.survey_test;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.test, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WebView webView = (WebView) view.findViewById(R.id.test_webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBuiltInZoomControls(true);

        String data = "<html><body>Youtube video .. <br> <iframe webkit-playsinline width='320' height='315' src='https://www.youtube.com/embed/lY2H2ZP56K4?playsinline=1' frameborder='0'></iframe></body></html>";
        webView.loadDataWithBaseURL("https://www.survata.com", data, "text/html", "utf-8", null);
    }
}
