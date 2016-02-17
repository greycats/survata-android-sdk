package com.survata;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.survata.network.RequestManager;
import com.survata.network.SurveyRequest;
import com.survata.ui.SurveyDialogFragment;
import com.survata.utils.Logger;
import com.survata.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class Survey {
    private static final String TAG = "Survey";

    public static final int REQUEST_SHOW_SURVEY = 2016;

    public interface SurveyAvailabilityListener {
        void onSurveyAvailable(SurveyAvailability surveyAvailability);
    }

    public interface SurveyStatusListener {
        void onResult(SurveyResult surveyResult);
    }

    public enum SurveyAvailability {
        AVAILABILITY,
        NOT_AVAILABLE,
        SERVER_ERROR,
        NETWORK_NOT_AVAILABLE
    }

    public enum SurveyResult {
        READY,
        STARTED,
        COMPLETED,
        SKIPPED,
        CANCELED,
        CREDIT_EARNED,
        FAILED,
        NETWORK_NOT_AVAILABLE
    }

    public void createSurveyWall(Activity activity, String publisher, String brand, String explainer, SurveyStatusListener surveyStatusListener) {
        SurveyDialogFragment dialogFragment = SurveyDialogFragment.newInstance(publisher, brand, explainer);
        dialogFragment.dismissSurveyDialog();

        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        dialogFragment.show(ft, SurveyDialogFragment.TAG);

        if (surveyStatusListener != null) {
            dialogFragment.setSurveyStatusListener(surveyStatusListener);
        }
    }

    public void create(final Context context,
                       final String contentName,
                       final String publisherUuid,
                       final String postalCode,
                       final SurveyAvailabilityListener surveyAvailabilityListener) {


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
                                        Logger.e(TAG, "parse json failed", e);
                                    }

                                    if (valid) {
                                        Logger.d(TAG, "has available survey");
                                    } else {
                                        Logger.d(TAG, "no survey available");
                                    }

                                    if (surveyAvailabilityListener != null) {
                                        SurveyAvailability surveyAvailability = valid ? SurveyAvailability.AVAILABILITY : SurveyAvailability.NOT_AVAILABLE;
                                        surveyAvailabilityListener.onSurveyAvailable(surveyAvailability);
                                    }
                                }

                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Logger.d(TAG, "check survey availability failed", error);

                                    if (surveyAvailabilityListener != null) {
                                        surveyAvailabilityListener.onSurveyAvailable(SurveyAvailability.SERVER_ERROR);
                                    }
                                }
                            });
                } catch (JSONException e) {
                    Logger.e(TAG, "put json object failed", e);
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
