package com.survata.demo.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.squareup.seismic.ShakeDetector;
import com.survata.demo.R;
import com.survata.demo.util.HockeyHelper;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity implements ShakeDetector.Listener, TabLayout.OnTabSelectedListener {

    private static final String TAG = "MainActivity";
    private ShakeDetector mShakeDetector;
    private AlertDialog mAlertDialog;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private HomePagerAdapter mHomePagerAdapter;
    private final List<TabLayout.Tab> mTabs = new ArrayList<>();
    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mHomePagerAdapter = new HomePagerAdapter(this);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(mHomePagerAdapter);

        initTabs();
        startShakeDetector();
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

    private void initTabs() {
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        DemoFragment demoFragment = mHomePagerAdapter.getSurveyFragment();
        SettingFragment settingFragment = mHomePagerAdapter.getSettingFragment();

        TabLayout.Tab surveyTab = createTabForFragment(demoFragment.getTitleResId());
        TabLayout.Tab settingTab = createTabForFragment(settingFragment.getTitleResId());

        mTabs.add(surveyTab);
        mTabs.add(settingTab);

        mTabLayout.addTab(surveyTab, DemoFragment.INDEX);
        mTabLayout.addTab(settingTab, SettingFragment.INDEX);

        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            View tabView = mHomePagerAdapter.getTabView(i);

            if (tab != null) {
                tab.setCustomView(tabView);
            }

            if (i == 0) {
                tabView.setSelected(true);
            } else {
                tabView.setSelected(false);
            }
        }

        mTabLayout.setOnTabSelectedListener(this);
    }

    @NonNull
    private TabLayout.Tab createTabForFragment(Object tag) {
        TabLayout.Tab tab = mTabLayout.newTab();
        tab.setTag(tag);
        return tab;
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

    @Override
    public void hearShake() {
        if (mAlertDialog == null || !mAlertDialog.isShowing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage(R.string.reset_data)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    DemoFragment surveyFragment = (DemoFragment) mHomePagerAdapter.getItem(DemoFragment.INDEX);
                                    surveyFragment.checkSurvey();
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            DemoFragment surveyFragment = (DemoFragment) mHomePagerAdapter.getItem(DemoFragment.INDEX);
            surveyFragment.unBlur();
        }
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Object tag = tab.getTag();

        mCurrentPage = mHomePagerAdapter.getPageIndexForTag(tag);
        mViewPager.setCurrentItem(mCurrentPage);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
