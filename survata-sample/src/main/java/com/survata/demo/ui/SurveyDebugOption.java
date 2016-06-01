package com.survata.demo.ui;

import com.survata.Survey;
import com.survata.SurveyOption;

import java.io.Serializable;
import java.util.Map;

public class SurveyDebugOption extends SurveyOption implements Survey.SurveyDebugOptionInterface, Serializable {

    public String preview;
    public String zipcode;
    public boolean sendZipcode = true;

    public SurveyDebugOption(String publisher) {
        super(publisher);
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = super.getParams();
        params.put("preview", preview);
        return params;
    }

    @Override
    public String getPreview() {
        return preview;
    }

    @Override
    public String getZipcode() {
        return zipcode;
    }

    @Override
    public boolean getSendZipcode() {
        return sendZipcode;
    }
}
