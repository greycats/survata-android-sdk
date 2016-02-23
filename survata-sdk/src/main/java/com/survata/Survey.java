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
import com.survata.utils.Geocode;
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

    private String mZipCode;

    public Survey(SurveyOption surveyOption) {
        mSurveyOption = surveyOption;
    }

    /**
     * log to client
     * @param survataLogger
     */
    public void setSurvataLogger(SurvataLogger survataLogger) {
        Logger.setmSurvataLogger(survataLogger);
    }

    public interface SurvataLogger {
        void surveyLogV(String tag, String msg);

        void surveyLogV(String tag, String msg, Throwable tr);

        void surveyLogD(String tag, String msg);

        void surveyLogD(String tag, String msg, Throwable tr);

        void surveyLogI(String tag, String msg);

        void surveyLogI(String tag, String msg, Throwable tr);

        void surveyLogW(String tag, String msg);

        void surveyLogW(String tag, String msg, Throwable tr);

        void surveyLogE(String tag, String msg);

        void surveyLogE(String tag, String msg, Throwable tr);
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
        void onEvent(SurveyEvents surveyEvents);
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
    public enum SurveyEvents {
        COMPLETED,
        SKIPPED,
        CANCELED,
        CREDIT_EARNED,
        NETWORK_NOT_AVAILABLE
    }

    public interface SurveyDebugOptionInterface {
        String getPreview();

        String getZipcode();

        boolean getSendZipcode();
    }

    /**
     * present survey in webview
     *
     * @param activity
     * @param surveyStatusListener
     */
    public void createSurveyWall(@NonNull final Activity activity,
                                 @Nullable final SurveyStatusListener surveyStatusListener) {

        if (!NetworkUtils.isNetworkConnected(activity)) {

            if (surveyStatusListener != null) {
                surveyStatusListener.onEvent(SurveyEvents.NETWORK_NOT_AVAILABLE);
            }
        }

        SurveyDialogFragment dialogFragment = SurveyDialogFragment.newInstance(mSurveyOption);
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
     */
    public void create(@NonNull final Context context,
                       @Nullable final SurveyAvailabilityListener surveyAvailabilityListener) {

        if (!NetworkUtils.isNetworkConnected(context)) {

            if (surveyAvailabilityListener != null) {
                surveyAvailabilityListener.onSurveyAvailable(SurveyAvailability.NETWORK_NOT_AVAILABLE);
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
                    new Geocode().get(context, new Geocode.GeocodeCallback() {
                        @Override
                        public void onZipcodeFind(String zipcode) {
                            if (!TextUtils.isEmpty(zipcode)) {
                                mZipCode = zipcode;
                            }
                            startCreate(context, surveyAvailabilityListener);
                        }
                    });
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
                            surveyAvailabilityListener.onSurveyAvailable(SurveyAvailability.SERVER_ERROR);
                        }
                    }
                });
    }

}
