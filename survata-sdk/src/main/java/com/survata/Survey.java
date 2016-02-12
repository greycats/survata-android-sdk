package com.survata;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.survata.view.SurveyView;

public class Survey {
    private static final String TAG = "Survey";

//    public void createSurveyWall(publisher: String, brand: String, explainer: String) {
//        let bundle = NSBundle(forClass: classForCoder)
//        if let templateFile = bundle.URLForResource("template", withExtension: "html"),
//        let template = try? String(contentsOfURL: templateFile, encoding: NSUTF8StringEncoding) {
//            let html = template
//                    .stringByReplacingOccurrencesOfString("[PUBLISHER_ID]", withString: publisher)
//            .stringByReplacingOccurrencesOfString("[BRAND]", withString: brand)
//            .stringByReplacingOccurrencesOfString("[EXPLAINER]", withString: explainer)
//            print("\(NSDate()) loading survata...")
//            webView.loadHTMLString(html, baseURL: NSURL(string: "https://www.survata.com"))
//        }
//    }

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

        surveyView.createSurveyWall(publisher, brand, explainer);
    }



}
