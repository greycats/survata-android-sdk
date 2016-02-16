package com.survata;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.survata.utils.Logger;
import com.survata.network.RequestManager;
import com.survata.network.SurveyRequest;
import com.survata.ui.SurveyActivity;
import com.survata.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class Survey {
    private static final String TAG = "Survey";

    public static final int REQUEST_SHOW_SURVEY = 2016;

    public interface SurveyCheckCallBack {
        void onCheckValid(boolean valid);
    }

    public void createSurveyWall(Activity activity, String publisher, String brand, String explainer) {
        SurveyActivity.start(activity, publisher, brand, explainer);
    }

    public void create(final Context context,
                       final String contentName,
                       final String publisherUuid,
                       final String postalCode,
                       final SurveyCheckCallBack surveyCheckCallBack) {


        RequestManager requestManager = new RequestManager() {

            @Override
            public Request createRequest() {

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("contentName", contentName);
                    jsonObject.put("publisherUuid", publisherUuid);
                    jsonObject.put("postalCode", postalCode);

                    return new SurveyRequest(Api.CREATE_SURVEY_URL,
                            jsonObject.toString(),
                            Utils.getUserAgent(context),
                            new SurveyRequest.SurveyListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    boolean valid = false;
                                    try {
                                        valid = response.getBoolean("valid");


                                    } catch (JSONException e) {
                                        Logger.d(TAG, "JSONException", e);
                                    }

                                    if (valid) {
                                        Logger.d(TAG, "has available survey");
                                    } else {
                                        Logger.d(TAG, "no survey available");
                                    }

                                    if (surveyCheckCallBack != null) {
                                        surveyCheckCallBack.onCheckValid(valid);
                                    }
                                }

                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Logger.d(TAG, "error", error);

                                    if (surveyCheckCallBack != null) {
                                        surveyCheckCallBack.onCheckValid(false);
                                    }
                                }
                            });
                } catch (JSONException e) {
                    Logger.d(TAG, "JSONException", e);
                }
                return null;
            }
        };

        requestManager.makeRequest(context);
    }


    public void setSurveyDebugLog(Logger.SurveyDebugLog surveyDebugLog) {
        Logger.setSurveyDebugLog(surveyDebugLog);
    }

}
