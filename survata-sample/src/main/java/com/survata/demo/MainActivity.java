package com.survata.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.survata.Const;
import com.survata.network.RequestManager;
import com.survata.network.SurveyRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button mCreateSurvey;

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCreateSurvey = (Button) findViewById(R.id.create_survey);
        mCreateSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createSurvey();

            }
        });
    }


    private void createSurvey() {

        RequestManager requestManager = new RequestManager() {

            @Override
            public Request createRequest() {

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("contentName", "");
                    jsonObject.put("publisherUuid", "survata-test");

                    return new SurveyRequest(Const.CREATE_SURVEY_URL, jsonObject.toString());
                } catch (JSONException e) {
                    Log.d(TAG, "JSONException", e);
                }
                return null;
            }
        };

        requestManager.makeRequest(this);
    }
}
