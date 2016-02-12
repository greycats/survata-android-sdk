package com.survata;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.survata.view.SurveyView;

public class Survey {
    private static final String TAG = "Survey";

    public void createSurveyWall(Activity activity, String publisher, String brand, String explainer) {

        ViewGroup container = (ViewGroup) activity.getWindow().getDecorView();
        final ViewGroup root = (ViewGroup) container.findViewById(android.R.id.content);

        final SurveyView surveyView = new SurveyView(activity);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        root.addView(surveyView, params);

        surveyView.setOnCloseCallback(new SurveyView.OnCloseCallback() {
            @Override
            public void onClose() {
                root.removeView(surveyView);
            }
        });

        surveyView.createSurveyWall(activity, publisher, brand, explainer);
    }



}
