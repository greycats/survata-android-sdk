package com.survata.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.survata.Survey;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button mCreateSurvey;
    private ProgressBar mProgressBar;
    private ViewGroup mContainer;
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
        mContainer = (ViewGroup) findViewById(R.id.container);

        checkSurvey();
    }

    private void showFullView() {
        mProgressBar.setVisibility(View.GONE);
        mCreateSurvey.setVisibility(View.GONE);
        mContainer.setVisibility(View.GONE);
    }

    private void showCreateSurveyWallButton() {
        mProgressBar.setVisibility(View.GONE);
        mCreateSurvey.setVisibility(View.VISIBLE);
        mContainer.setVisibility(View.VISIBLE);
    }

    private void showLoadingSurveyView(){
        mProgressBar.setVisibility(View.VISIBLE);
        mCreateSurvey.setVisibility(View.GONE);
        mContainer.setVisibility(View.VISIBLE);
    }

    private void checkSurvey() {
        showLoadingSurveyView();

        mSurvey.create(this, "https://www.survata.com/publisher-demos/internal/", "survata-test", new Survey.SurveyCheckCallBack() {
            @Override
            public void onCheckValid(boolean valid) {
                if (valid) {
                    showCreateSurveyWallButton();
                } else {
                    showFullView();
                }
            }
        });
    }

}
