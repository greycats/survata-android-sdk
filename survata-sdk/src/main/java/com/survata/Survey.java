package com.survata;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

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

    private static final String CREATE_SURVEY_URL = "https://surveywall-api.survata.com/rest/interview-check/create";

    @NonNull
    private String mPublisherUuid;

    @Nullable
    private String mPostalCode;

    @Nullable
    private String mContentName;

    @NonNull
    public void setPublisherUuid(String publisherUuid) {
        mPublisherUuid = publisherUuid;
    }

    // TODO: postal code is not passed in.  Postal code is generated from location.  Remove this setter.
    // See: http://stackoverflow.com/questions/9409195/how-to-get-complete-address-from-latitude-and-longitude If location permission not granted,
    // 1) do not ask for permission 2) do not attempt to get location (lat/lon)

    @Nullable
    public void setPostalCode(String postalCode) {
        mPostalCode = postalCode;
    }

    @Nullable
    public void setContentName(String contentName) {
        mContentName = contentName;
    }

    /**
     * survey availability callback
     */
    public interface SurveyAvailabilityListener {
        void onSurveyAvailable(SurveyAvailability surveyAvailability);
    }

    /**
     * survey status callback
     */
    public interface SurveyStatusListener {
        // TODO: add a new method, called onEvent, these are events that happen during the survey but not at the survey end. Like Ready, Skipped, Started...
        void onResult(SurveyResult surveyResult);
    }

    /**
     * enum status returned in create api
     */
    public enum SurveyAvailability {
        AVAILABILITY,
        NOT_AVAILABLE,
        SERVER_ERROR,
        NETWORK_NOT_AVAILABLE
    }

    /**
     * enum status returned in present api
     */
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

    // TODO: create a new enum here "SurveyEvents", move "READY, SKIPPED, STARTED"

    // TODO: create a new constructor here that takes in
    // (Activity, String publisherId, String contentName) and a new constructor
    // (Activity, String publisherId), make the default constructor private


    /**
     * present survey in webview
     *
     * @param activity             activity
     * @param surveyOption         creation options
     * @param surveyStatusListener callbacks survey result
     * @throws SurveyException
     */
    public void createSurveyWall(@NonNull final Activity activity,
                                 @NonNull final SurveyOption surveyOption,
                                 @Nullable final SurveyStatusListener surveyStatusListener) throws SurveyException {

        if (TextUtils.isEmpty(mPublisherUuid)) {
            throw new SurveyException("publisher uuid should be empty, should initialize");
        }

        SurveyDialogFragment dialogFragment = SurveyDialogFragment.newInstance(mPublisherUuid, surveyOption);
        dialogFragment.dismissSurveyDialog();

        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        dialogFragment.show(ft, SurveyDialogFragment.TAG);

        if (surveyStatusListener != null) {
            dialogFragment.setSurveyStatusListener(surveyStatusListener);
        }
    }

    /**
     * call this function to initialize Survata
     * e.g. use this to determine wether to show the survata button and the button will trigger presentation
     *
     * @param context                    context
     * @param surveyAvailabilityListener callback availability
     * @throws SurveyException
     */
    public void create(@NonNull final Context context,
                       @Nullable final SurveyAvailabilityListener surveyAvailabilityListener) throws SurveyException {

        if (TextUtils.isEmpty(mPublisherUuid)) {
            throw new SurveyException("publisher uuid should be empty, should initialize");
        }

        // TODO: I don't really get why we need an abstract class here.  Just move this to createRequest in RequestManager
        // In fact, just combine it into Networking

        RequestManager requestManager = new RequestManager() {

            @Override
            public Request createRequest() {

                try {
                    JSONObject jsonObject = new JSONObject();

                    if (!TextUtils.isEmpty(mContentName)) {
                        jsonObject.put("contentName", mContentName);
                    }

                    jsonObject.put("publisherUuid", mPublisherUuid);

                    if (!TextUtils.isEmpty(mPostalCode)) {
                        jsonObject.put("postalCode", mPostalCode);
                    }

                    return new SurveyRequest(CREATE_SURVEY_URL,
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

    /**
     * handle all event logs
     * @param surveyDebugLog
     */
    public void setSurveyDebugLog(Logger.SurveyDebugLog surveyDebugLog) {
        Logger.setSurveyDebugLog(surveyDebugLog);
    }

}
