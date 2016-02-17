package com.survata.demo.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.squareup.seismic.ShakeDetector;
import com.survata.Survey;
import com.survata.demo.R;
import com.survata.demo.util.HockeyHelper;
import com.survata.demo.util.LocationTracker;
import com.survata.utils.Logger;

public class MainActivity extends AppCompatActivity implements ShakeDetector.Listener {

    private static final String TAG = "MainActivity";
    private Button mCreateSurvey;
    private ProgressBar mProgressBar;
    private ViewGroup mContainer;
    private Survey mSurvey = new Survey();
    private ShakeDetector mShakeDetector;
    private AlertDialog mAlertDialog;
    private LocationTracker mLocationTracker;

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

        mSurvey.setSurveyDebugLog(new Logger.SurveyDebugLog() {
            @Override
            public void surveyLogV(String tag, String msg) {

            }

            @Override
            public void surveyLogV(String tag, String msg, Throwable tr) {

            }

            @Override
            public void surveyLogD(String tag, String msg) {

            }

            @Override
            public void surveyLogD(String tag, String msg, Throwable tr) {

            }

            @Override
            public void surveyLogI(String tag, String msg) {

            }

            @Override
            public void surveyLogI(String tag, String msg, Throwable tr) {

            }

            @Override
            public void surveyLogW(String tag, String msg) {

            }

            @Override
            public void surveyLogW(String tag, String msg, Throwable tr) {

            }

            @Override
            public void surveyLogE(String tag, String msg) {

            }

            @Override
            public void surveyLogE(String tag, String msg, Throwable tr) {

            }
        });

        startShakeDetector();

        // check survey with location
        checkSurveyWithLocation();

        HockeyHelper.checkForUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HockeyHelper.checkForCrashes(this);
    }

    @Override
    protected void onPause() {
        HockeyHelper.unregisterUpdate();
        super.onPause();
    }

    private void checkSurveyWithLocation() {
        // show loading default
        showLoadingSurveyView();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // You need to ask the user to enable the permissions
            Log.e(TAG, "need permission");
            checkSurvey(null);
        } else {
            mLocationTracker = new LocationTracker(this) {
                @Override
                public void onLocationFound(Location location) {
                    Log.e(TAG, "onLocationFound " + location);
                    mLocationTracker.stopListening();

                    checkSurvey(location);
                }

                @Override
                public void onTimeout() {
                    Log.e(TAG, "onTimeout");

                    checkSurvey(null);
                }
            };
            mLocationTracker.startListening();
        }
    }

    @Override
    protected void onDestroy() {
        stopShakeDetector();

        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }

        super.onDestroy();
    }

    private void startShakeDetector() {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mShakeDetector = new ShakeDetector(this);
        mShakeDetector.start(sensorManager);
    }

    private void stopShakeDetector() {
        if (null != mShakeDetector) {
            mShakeDetector.stop();
        }
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

    private void showLoadingSurveyView() {
        mProgressBar.setVisibility(View.VISIBLE);
        mCreateSurvey.setVisibility(View.GONE);
        mContainer.setVisibility(View.VISIBLE);
    }

    private void checkSurvey(Location location) {
        String postalCode = "";

        if (location != null) {
            postalCode = mLocationTracker.getPostalCode(this, location);
        }

        mSurvey.create(this,
                "https://www.survata.com/publisher-demos/internal/",
                "survata-test",
                postalCode,
                new Survey.SurveyCheckCallBack() {
                    @Override
                    public void onCheckValid(boolean valid) {

                        Log.d(TAG, "check survey result: " + valid);
                        if (valid) {
                            showCreateSurveyWallButton();
                        } else {
                            showFullView();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Survey.REQUEST_SHOW_SURVEY && resultCode == RESULT_OK) {
            showFullView();
        }
    }

    @Override
    public void hearShake() {
        if (null == mAlertDialog || null != mAlertDialog && !mAlertDialog.isShowing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage(R.string.reset_data)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    checkSurveyWithLocation();
                                }
                            })
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

            mAlertDialog = builder.create();
            mAlertDialog.show();
        }
    }
}
