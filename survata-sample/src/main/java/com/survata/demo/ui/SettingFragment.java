package com.survata.demo.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.view.View;

import com.survata.demo.R;
import com.survata.demo.util.Settings;

public class SettingFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final int INDEX = 1;

    public int getTitleResId() {
        return R.string.survey_setting;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        final Resources resources = getResources();
        final Context context = getActivity();

        String publisherIdString = resources.getString(R.string.publisher_id);
        Preference preference = findPreference(publisherIdString);
        preference.setSummary(Settings.getPublisherId(context));

        String previewIdString = resources.getString(R.string.preview_id);
        preference = findPreference(previewIdString);
        preference.setSummary(Settings.getPreviewId(context));

        String contentNameString = resources.getString(R.string.content_name);
        preference = findPreference(contentNameString);
        preference.setSummary(Settings.getContentName(context));

        String contentNameToggle = resources.getString(R.string.content_name_toggle);
        preference = findPreference(contentNameToggle);
        boolean contentNameEnable = Settings.getContentNameEnable(context);
        ((SwitchPreference) preference).setChecked(contentNameEnable);
        hideOrShowContentName(contentNameEnable);

        String zipCodeString = resources.getString(R.string.zip_code);
        preference = findPreference(zipCodeString);
        preference.setSummary(Settings.getZipCode(context));

        String zipCodeToggle = resources.getString(R.string.zip_code_toggle);
        preference = findPreference(zipCodeToggle);
        boolean zipCodeEnable = Settings.getZipCodeEnable(context);
        ((SwitchPreference) preference).setChecked(zipCodeEnable);
        hideOrShowZipCode(zipCodeEnable);

        String reset = resources.getString(R.string.reset);
        preference = findPreference(reset);

        if (preference instanceof ButtonPreference) {
            ((ButtonPreference) preference).setButtonClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reset(getContext());
                }
            });

        }
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Ignore
    }

    @Override
    public void onResume() {
        super.onResume();

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);

        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        String summary = "";
        boolean checked = false;
        if (key.equals(getString(R.string.publisher_id))) {
            summary = sharedPreferences.getString(key, getString(R.string.default_publisher_id));
        } else if (key.equals(getString(R.string.preview_id))) {
            summary = sharedPreferences.getString(key, getString(R.string.default_preview_id));
        } else if (key.equals(getString(R.string.content_name))) {
            summary = sharedPreferences.getString(key, getString(R.string.default_content_name));
        } else if (key.equals(getString(R.string.content_name_toggle))) {
            checked = sharedPreferences.getBoolean(key, false);
            hideOrShowContentName(checked);
        } else if (key.equals(getString(R.string.zip_code))) {
            summary = sharedPreferences.getString(key, getString(R.string.default_zip_code));
        } else if (key.equals(getString(R.string.zip_code_toggle))) {
            checked = sharedPreferences.getBoolean(key, false);
            hideOrShowZipCode(checked);
        }

        Preference preference = findPreference(key);

        if (preference instanceof EditTextPreference) {
            EditTextPreference editTextPreference = (EditTextPreference) preference;
            editTextPreference.setSummary(summary);

        } else if (preference instanceof SwitchPreference) {
            SwitchPreference switchPreference = (SwitchPreference) preference;
            switchPreference.setChecked(checked);
        }
    }

    private void reset(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String publisherIdString = context.getString(R.string.publisher_id);
        sharedPreferences.edit().putString(publisherIdString, context.getString(R.string.default_publisher_id)).apply();

        String previewIdString = context.getString(R.string.preview_id);
        sharedPreferences.edit().putString(previewIdString, context.getString(R.string.default_preview_id)).apply();

        String contentNameString = context.getString(R.string.content_name);
        sharedPreferences.edit().putString(contentNameString, context.getString(R.string.default_content_name)).apply();

        String zipCodeString = context.getString(R.string.zip_code);
        sharedPreferences.edit().putString(zipCodeString, context.getString(R.string.default_zip_code)).apply();

        String contentNameToggle = context.getString(R.string.content_name_toggle);
        sharedPreferences.edit().putBoolean(contentNameToggle, true).apply();

        String zipCodeToggle = context.getString(R.string.zip_code_toggle);
        sharedPreferences.edit().putBoolean(zipCodeToggle, true).apply();
    }

    private void hideOrShowContentName(boolean result) {
        String contentNameToggle = getActivity().getString(R.string.content_name);
        findPreference(contentNameToggle).setVisible(result);
    }

    private void hideOrShowZipCode(boolean result) {
        String contentNameToggle = getActivity().getString(R.string.zip_code);
        findPreference(contentNameToggle).setVisible(result);
    }

}
