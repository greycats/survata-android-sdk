package com.survata;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.survata.network.Networking;
import com.survata.network.SurveyRequest;
import com.survata.ui.SurveyDialogFragment;
import com.survata.utils.LocationTracker;
import com.survata.utils.Logger;
import com.survata.utils.NetworkUtils;
import com.survata.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class Survey {
    private static final String TAG = "Survey";

    private static final String CREATE_SURVEY_URL = "https://surveywall-api.survata.com/rest/interview-check/create";

    private final SurveyOption mSurveyOption;

    private LocationTracker mLocationTracker;

    private String mZipCode;

    public interface SurvataLogger {
        void surveyLogVerbose(String tag, String msg);

        void surveyLogVerbose(String tag, String msg, Throwable tr);

        void surveyLogDebug(String tag, String msg);

        void surveyLogDebug(String tag, String msg, Throwable tr);

        void surveyLogInfo(String tag, String msg);

        void surveyLogInfo(String tag, String msg, Throwable tr);

        void surveyLogWarn(String tag, String msg);

        void surveyLogWarn(String tag, String msg, Throwable tr);

        void surveyLogError(String tag, String msg);

        void surveyLogError(String tag, String msg, Throwable tr);
    }

    /**
     * survey availability callback when create survey
     */
    public interface SurveyAvailabilityListener {
        void onSurveyAvailable(SurveyAvailability surveyAvailability);
    }

    /**
     * survey event callback when show survey
     */
    public interface SurveyStatusListener {
        void onEvent(SurveyEvents surveyEvents);
    }

    public interface SurveyDebugOptionInterface {
        String getPreview();

        String getZipcode();

        boolean getSendZipcode();
    }


    /**
     * Initialize survey
     * @param surveyOption creation options
     */
    public Survey(SurveyOption surveyOption) {
        mSurveyOption = surveyOption;
    }


    /**
     * if you need log to client, you can call this method.
     * @param survataLogger
     */
    public static void setSurvataLogger(SurvataLogger survataLogger) {
        Logger.setmSurvataLogger(survataLogger);
    }

    /**
     * enum status returned in create api
     */
    public enum SurveyAvailability {
        AVAILABILITY,                 // survey is available
        NOT_AVAILABLE,                // survey is not available
        ERROR,                        // create survey error
    }

    /**
     * enum status returned in present api
     */
    public enum SurveyEvents {
        COMPLETED,                    // survey is completed
        SKIPPED,                      // user skip the survey
        CANCELED,                     // user cancel the survey
        CREDIT_EARNED,                // survey loaded done
        NETWORK_NOT_AVAILABLE,        // network is not available
        NO_SURVEY_AVAILABLE           // no survey available
    }

    /**
     * To present survey over DialogFragment.
     *
     * @param activity activity
     * @param surveyStatusListener callbacks survey result
     *
     * Note: client code should hold this instance before callback
     */
    public void createSurveyWall(@NonNull final Activity activity,
                                 @Nullable final SurveyStatusListener surveyStatusListener) {

        if (!NetworkUtils.isNetworkConnected(activity)) {

            if (surveyStatusListener != null) {
                surveyStatusListener.onEvent(SurveyEvents.NETWORK_NOT_AVAILABLE);
            }
        }

        SurveyDialogFragment dialogFragment = SurveyDialogFragment.newInstance(mSurveyOption, mZipCode);
        dialogFragment.dismissSurveyDialog();

        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        dialogFragment.show(ft, SurveyDialogFragment.TAG);

        if (surveyStatusListener != null) {
            dialogFragment.setSurveyStatusListener(surveyStatusListener);
        }
    }

    /**
     * cause the availability can be changed from time to time, please use this method right before `createSurveyWall`. Results of presentation on availability other than `.Available` is not guaranteed.
     * e.g. use this to determine whether to show the survata button and the button will trigger presentation
     *
     * @param context                    context
     * @param surveyAvailabilityListener closure to callback availability
     */
    public void create(@NonNull final Context context,
                       @Nullable final SurveyAvailabilityListener surveyAvailabilityListener) {

        if (!NetworkUtils.isNetworkConnected(context)) {

            if (surveyAvailabilityListener != null) {
                surveyAvailabilityListener.onSurveyAvailable(SurveyAvailability.ERROR);
            }
        }

        if (mSurveyOption instanceof SurveyDebugOptionInterface) {

            boolean sendZipcode = ((SurveyDebugOptionInterface) mSurveyOption).getSendZipcode();

            if (sendZipcode) {

                String zipcode = ((SurveyDebugOptionInterface) mSurveyOption).getZipcode();
                if (!TextUtils.isEmpty(zipcode)) {
                    mZipCode = zipcode;
                    startCreate(context, surveyAvailabilityListener);
                } else {
                    new LocationTracker(context) {
                        @Override
                        public void onLocationFound(@NonNull String zipCode) {
                            Logger.e(TAG, "fetch zipCode success: " + zipCode);

                            if (!TextUtils.isEmpty(zipCode)) {
                                mZipCode = zipCode;
                            }

                            startCreate(context, surveyAvailabilityListener);
                        }

                        @Override
                        public void onLocationFoundFailed() {
                            Logger.e(TAG, "fetch zipCode failed");

                            startCreate(context, surveyAvailabilityListener);
                        }

                    }.start();
                }
            }
        } else {
            startCreate(context, surveyAvailabilityListener);
        }
    }

    private void startCreate(final Context context,
                             final SurveyAvailabilityListener surveyAvailabilityListener) {

        Map<String, String> params = mSurveyOption.getParams();

        String publisher = mSurveyOption.publisher;
        String contentName = mSurveyOption.contentName;

        params.put("publisherUuid", publisher);
        params.put("contentName", contentName);
        params.put("postalCode", mZipCode);

        Networking networking = Networking.getInstance();
        networking.request(context,
                CREATE_SURVEY_URL,
                Utils.parseParamMap(params),
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
                            surveyAvailabilityListener.onSurveyAvailable(SurveyAvailability.ERROR);
                        }
                    }
                });
    }

}
