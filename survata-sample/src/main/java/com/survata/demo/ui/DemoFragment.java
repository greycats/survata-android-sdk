package com.survata.demo.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.survata.Survey;
import com.survata.SurveyOption;
import com.survata.demo.R;
import com.survata.demo.util.Settings;

import java.util.Map;

import jp.wasabeef.blurry.Blurry;

public class DemoFragment extends Fragment {

    private static final String TAG = "DemoFragment";
    public static final int INDEX = 0;
    private Button mCreateSurvey;
    private ProgressBar mProgressBar;
    private ViewGroup mContainer;
    private Survey mSurvey;
    private boolean mBlurred = false;

    private Survey.SurvataLogger mSurvataLogger = new Survey.SurvataLogger() {
        @Override
        public void surveyLogV(String tag, String msg) {

        }

        @Override
        public void surveyLogV(String tag, String msg, Throwable tr) {

        }

        @Override
        public void surveyLogD(String tag, String msg) {

        }

        @Override
        public void surveyLogD(String tag, String msg, Throwable tr) {

        }

        @Override
        public void surveyLogI(String tag, String msg) {

        }

        @Override
        public void surveyLogI(String tag, String msg, Throwable tr) {

        }

        @Override
        public void surveyLogW(String tag, String msg) {

        }

        @Override
        public void surveyLogW(String tag, String msg, Throwable tr) {

        }

        @Override
        public void surveyLogE(String tag, String msg) {

        }

        @Override
        public void surveyLogE(String tag, String msg, Throwable tr) {

        }
    };

    public int getTitleResId() {
        return R.string.survey_demo;
    }


    public class SurveyDebugOption extends SurveyOption implements Survey.SurveyDebugOptionInterface {

        public String preview;
        public String zipcode;
        public boolean sendZipcode = true;

        public SurveyDebugOption(String publisher) {
            super(publisher);
        }

        @Override
        public Map<String, String> getParams() {
            Map<String, String> params = super.getParams();
            params.put("preview", preview);
            return params;
        }

        @Override
        public String getPreview() {
            return preview;
        }

        @Override
        public String getZipcode() {
            return zipcode;
        }

        @Override
        public boolean getSendZipcode() {
            return sendZipcode;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.demo, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCreateSurvey = (Button) view.findViewById(R.id.create_survey);
        mCreateSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSurvey();
            }
        });

        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mContainer = (ViewGroup) view.findViewById(R.id.container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // check survey with location
        checkSurvey();
    }

    public void checkSurvey() {
        // show loading default
        showLoadingSurveyView();

        Context context = getContext();
        String publisherId = Settings.getPublisherId(context);
        SurveyDebugOption option = new SurveyDebugOption(publisherId);
        option.preview = Settings.getPreviewId(context);
        option.zipcode = Settings.getZipCode(context);
        option.sendZipcode = Settings.getZipCodeEnable(context);
        option.contentName = Settings.getContentName(context);

        mSurvey = new Survey(option);
        mSurvey.setSurvataLogger(mSurvataLogger);
        mSurvey.create(getActivity(),
                new Survey.SurveyAvailabilityListener() {
                    @Override
                    public void onSurveyAvailable(Survey.SurveyAvailability surveyAvailability) {
                        Log.d(TAG, "check survey result: " + surveyAvailability);
                        if (surveyAvailability == Survey.SurveyAvailability.AVAILABILITY) {
                            showCreateSurveyWallButton();
                        } else {
                            showFullView();
                        }
                    }
                });
    }

    private void showSurvey() {
        blur();

        mSurvey.createSurveyWall(getActivity(), new Survey.SurveyStatusListener() {
            @Override
            public void onEvent(Survey.SurveyEvents surveyEvents) {
                Log.d(TAG, "surveyEvents: " + surveyEvents);

                if (surveyEvents == Survey.SurveyEvents.COMPLETED) {
                    showFullView();
                }
            }
        });
    }

    private void showFullView() {
        mProgressBar.setVisibility(View.GONE);
        mCreateSurvey.setVisibility(View.GONE);
        mContainer.setVisibility(View.GONE);
    }

    private void showCreateSurveyWallButton() {
        mProgressBar.setVisibility(View.GONE);
        mCreateSurvey.setVisibility(View.VISIBLE);
        mContainer.setVisibility(View.VISIBLE);
    }

    private void showLoadingSurveyView() {
        mProgressBar.setVisibility(View.VISIBLE);
        mCreateSurvey.setVisibility(View.GONE);
        mContainer.setVisibility(View.VISIBLE);
    }

    public void blur() {
        Activity activity = getActivity();

        if (activity == null) {
            Log.d(TAG, "activity is null");
            return;
        }

        if (!mBlurred) {
            ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
            Blurry.with(activity).sampling(12).onto(viewGroup);
            mBlurred = true;
        }
    }

    public void unBlur() {
        Activity activity = getActivity();

        if (activity == null) {
            Log.d(TAG, "activity is null");
            return;
        }

        if (mBlurred) {
            ViewGroup viewGroup = (ViewGroup) getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
            Blurry.delete(viewGroup);
            mBlurred = false;
        }
    }

}
