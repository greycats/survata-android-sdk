package com.survata.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.survata.utils.Utils;

public class Networking {

    private static Networking mNetworking = new Networking();

    private volatile static RequestQueue sRequestQueue;

    public static Networking getInstance() {
        return mNetworking;
    }

    public RequestQueue getRequestQueue(Context context) {
        RequestQueue requestQueue = sRequestQueue;
        if (requestQueue == null) {
            synchronized (Networking.class) {
                requestQueue = sRequestQueue;
                if (requestQueue == null) {
                    requestQueue = Volley.newRequestQueue(context);
                    sRequestQueue = requestQueue;
                    requestQueue.start();
                }
            }
        }
        return requestQueue;
    }

    public void request(final Context context,
                        final String url,
                        final String requestBody,
                        final SurveyRequest.SurveyListener surveyListener) {
        RequestManager requestManager = new RequestManager() {

            @Override
            public Request createRequest() {
                return new SurveyRequest(url, requestBody, Utils.getUserAgent(context), surveyListener);
            }
        };
        requestManager.makeRequest(context);
    }

}
