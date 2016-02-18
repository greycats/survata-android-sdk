package com.survata;

import android.text.TextUtils;

import java.io.Serializable;

public class SurveyOption implements Serializable {
    private String brand;
    private String explainer;
    private String preview;

    public SurveyOption(String brand, String explainer, String preview) {
        this.brand = brand;
        this.explainer = explainer;
        this.preview = preview;
    }

    public String description() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if (!TextUtils.isEmpty(brand)) {
            append(builder, "brand", brand);
        }
        if (!TextUtils.isEmpty(explainer)) {
            append(builder, "explainer", explainer);
        }
        if (!TextUtils.isEmpty(preview)) {
            append(builder, "preview", preview);
        }
        builder.append("}");
        return builder.toString();
    }

    private void append(StringBuilder builder, String key, String value){
        builder.append(key).append(":").append("\"").append(preview).append("\"");
    }
}
