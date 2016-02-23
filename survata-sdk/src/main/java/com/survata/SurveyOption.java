package com.survata;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * SurveyOption for Survata.
 * do not modify it after sending to Survey.create
 */
public class SurveyOption implements Serializable {
    @Nullable
    public String brand;
    @Nullable
    public String explainer;
    @Nullable
    public String contentName;
    @NonNull
    public final String publisher;

    public SurveyOption(String publisher) {
        this.publisher = publisher;
    }

    public Map<String, String> getParams() {
        Map<String, String> map = new HashMap<>();
        map.put("brand", brand);
        map.put("explainer", explainer);
        map.put("contentName", contentName);
        return map;
    }
}
