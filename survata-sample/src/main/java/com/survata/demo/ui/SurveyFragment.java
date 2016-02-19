package com.survata.demo.ui;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.survata.Survey;
import com.survata.SurveyOption;
import com.survata.demo.R;
import com.survata.demo.util.LocationTracker;
import com.survata.demo.util.SurveyUtils;
import com.survata.utils.Logger;

import jp.wasabeef.blurry.Blurry;

public class SurveyFragment extends Fragment {

    private static final String TAG = "DemoFragment";
    public static final int INDEX = 0;
    private Button mCreateSurvey;
    private ProgressBar mProgressBar;
    private ViewGroup mContainer;
    private LocationTracker mLocationTracker;
    private Survey mSurvey = new Survey();
    private boolean mBlurred = false;

    private Logger.SurveyDebugLog mSurveyDebugLog = new Logger.SurveyDebugLog() {
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

        mSurvey.setSurveyDebugLog(mSurveyDebugLog);
    }

    public void checkSurvey() {
        // show loading default
        showLoadingSurveyView();

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // You need to ask the user to enable the permissions
            Log.e(TAG, "need permission");
            startCheckSurvey();
        } else {
            mLocationTracker = new LocationTracker(getActivity()) {
                @Override
                public void onLocationFound(Location location) {
                    Log.e(TAG, "onLocationFound " + location);
                    mLocationTracker.stopListening();

                    startCheckSurvey(location);
                }

                @Override
                public void onTimeout() {
                    Log.e(TAG, "onTimeout");

                    startCheckSurvey();
                }
            };
            mLocationTracker.startListening();
        }
    }

    private void showSurvey() {

        blur();

        String previewId = SurveyUtils.getPreviewId(getContext());
        String contentName = SurveyUtils.getContentName(getContext());
        SurveyOption surveyOption = new SurveyOption("", "", previewId, contentName);

        String publisherId = getString(R.string.default_publisher_id);
        mSurvey.createSurveyWall(getActivity(), publisherId, surveyOption, new Survey.SurveyStatusListener() {
            @Override
            public void onResult(Survey.SurveyResult surveyResult) {
                Log.d(TAG, "surveyResult: " + surveyResult);

                if (surveyResult == Survey.SurveyResult.COMPLETED) {
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

    private void startCheckSurvey() {
        startCheckSurvey(null);
    }

    private void startCheckSurvey(Location location) {
        String postalCode = "";

        if (location != null) {
            postalCode = mLocationTracker.getPostalCode(getActivity(), location);
        }

        Activity activity = getActivity();

        if (activity == null) {
            Log.e(TAG, "activity is null");
            return;
        }


        String publisherId = SurveyUtils.getPublisherId(activity);
        mSurvey.setPublisherUuid(publisherId);

        boolean zipCodeEnable = SurveyUtils.getZipCodeEnable(activity);
        if (zipCodeEnable) {
            String zipCode = SurveyUtils.getZipCode(activity);

            if (TextUtils.isEmpty(zipCode)) {
                zipCode = postalCode;
            }
            mSurvey.setPostalCode(zipCode);
        }

        boolean contentNameEnable = SurveyUtils.getContentNameEnable(activity);

        if (contentNameEnable) {
            String contentName = SurveyUtils.getContentName(activity);
            mSurvey.setContentName(contentName);
        }

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
