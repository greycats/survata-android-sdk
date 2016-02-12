package com.survata.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.survata.Const;
import com.survata.Survey;
import com.survata.network.RequestManager;
import com.survata.network.SurveyRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button mCreateSurvey;
    private ProgressBar mProgressBar;

    public static final String TAG = "MainActivity";

    private Survey mSurvey = new Survey();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCreateSurvey = (Button) findViewById(R.id.create_survey);
        mCreateSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSurvey.createSurveyWall(MainActivity.this, "survata-test", "", "");
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);
        mCreateSurvey.setVisibility(View.GONE);

        checkSurvey();
    }


    private void checkSurvey() {

        RequestManager requestManager = new RequestManager() {

            @Override
            public Request createRequest() {

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("contentName", "https://www.survata.com/publisher-demos/internal/");
                    jsonObject.put("publisherUuid", "survata-test");

                    return new SurveyRequest(Const.CREATE_SURVEY_URL,
                            jsonObject.toString(),
                            new SurveyRequest.SurveyListener() {
                                @Override
                                public void onResponse(JSONObject response) {


                                    mProgressBar.setVisibility(View.GONE);
                                    mCreateSurvey.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });
                } catch (JSONException e) {
                    Log.d(TAG, "JSONException", e);
                }
                return null;
            }
        };

        requestManager.makeRequest(this);
    }

}
