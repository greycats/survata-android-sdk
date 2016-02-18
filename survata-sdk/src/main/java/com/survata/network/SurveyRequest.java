package com.survata.network;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.survata.utils.Logger;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SurveyRequest extends JsonObjectRequest {

    private static final String TAG = "SurveyRequest";

    private String mUserAgent;

    public interface SurveyListener {
        void onResponse(JSONObject response);

        void onErrorResponse(VolleyError error);
    }

    public SurveyRequest(String url,
                         String requestBody,
                         String userAgent,
                         final SurveyListener surveyListener) {
        super(Method.POST, url,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.d(TAG, "onResponse: " + response);
                        surveyListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Logger.d(TAG, "createSurvey error: " + error);
                        surveyListener.onErrorResponse(error);
                    }
                });
        mUserAgent = userAgent;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/javascript");
        headers.put("User-Agent", mUserAgent);
        return headers;
    }
}
