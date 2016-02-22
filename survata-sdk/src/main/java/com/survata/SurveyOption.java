package com.survata;

import android.text.TextUtils;

import java.io.Serializable;

public class SurveyOption implements Serializable {
    protected String brand;
    protected String explainer;
    protected String preview;
    protected String contentName;

    public SurveyOption(String brand, String explainer, String preview, String contentName) {
        this.brand = brand;
        this.explainer = explainer;
        this.preview = preview;
        this.contentName = contentName;
    }

    public String description() {
        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(brand)) {
            append(builder, "brand", brand);
        }
        if (!TextUtils.isEmpty(explainer)) {
            append(builder, "explainer", explainer);
        }
        if (!TextUtils.isEmpty(preview)) {
            append(builder, "preview", preview);
        }
        if (!TextUtils.isEmpty(contentName)) {
            append(builder, "contentName", contentName);
        }

        String option = "";
        if (builder.length() > 0) {
            option = builder.substring(0, builder.length() - 1);
        }

        return "{" + option + "}";
    }

    private void append(StringBuilder builder, String key, String value) {
        builder.append(key).append(":").append("\"").append(value).append("\"").append(",");
    }
}
