package com.survata.demo.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.survata.demo.R;

import java.util.List;

public class HomePagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "HomePagerAdapter";

    private final List<Fragment> mFragments;
    private final FragmentActivity mActivity;

    public HomePagerAdapter(FragmentActivity activity) {
        this(activity, ImmutableList.<Fragment>builder()
                .add(new DemoFragment())
                .add(new SettingFragment())
                .add(new TestFragment())
                .build());
    }

    private HomePagerAdapter(FragmentActivity activity, List<Fragment> fragments) {
        super(activity.getSupportFragmentManager());
        mActivity = activity;
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        final int titleResId;

        switch (position) {
            case DemoFragment.INDEX:
                titleResId = getSurveyFragment().getTitleResId();
                break;
            case SettingFragment.INDEX:
                titleResId = getSettingFragment().getTitleResId();
                break;
            case TestFragment.INDEX:
                titleResId = getTestFragment().getTitleResId();
                break;
            default:
                Log.d(TAG, "unknown position getPageTitle: " + position);
                return null;
        }

        return mActivity.getString(titleResId);
    }

    public DemoFragment getSurveyFragment() {
        return (DemoFragment) getItem(DemoFragment.INDEX);
    }

    public SettingFragment getSettingFragment() {
        return (SettingFragment) getItem(SettingFragment.INDEX);
    }

    public TestFragment getTestFragment() {
        return (TestFragment) getItem(TestFragment.INDEX);
    }

    public View getTabView(int position) {
        View view = View.inflate(mActivity, R.layout.tab, null);
        TextView tabText = (TextView) view.findViewById(R.id.tab_text);
        tabText.setText(getPageTitle(position));
        return view;
    }

    public int getPageIndexForTag(Object tag) {
        DemoFragment surveyFragment = getSurveyFragment();
        Integer otherTag = surveyFragment.getTitleResId();
        if (otherTag.equals(tag)) {
            return surveyFragment.INDEX;
        }

        SettingFragment settingFragment = getSettingFragment();
        otherTag = settingFragment.getTitleResId();
        if (otherTag.equals(tag)) {
            return settingFragment.INDEX;
        }

        TestFragment testFragment = getTestFragment();
        otherTag = testFragment.getTitleResId();
        if (otherTag.equals(tag)) {
            return testFragment.INDEX;
        }
        throw new IllegalArgumentException("Couldn't find tag: " + tag);
    }
}
