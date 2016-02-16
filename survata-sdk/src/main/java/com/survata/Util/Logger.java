package com.survata.Util;

import android.util.Log;

import com.survata.BuildConfig;

public class Logger {

    private static SurveyDebugLog mSurveyDebugLog;

    public static void setSurveyDebugLog(SurveyDebugLog mSurveyDebugLog) {
        Logger.mSurveyDebugLog = mSurveyDebugLog;
    }

    public interface SurveyDebugLog {
        void surveyLogV(String tag, String msg);

        void surveyLogV(String tag, String msg, Throwable tr);

        void surveyLogD(String tag, String msg);

        void surveyLogD(String tag, String msg, Throwable tr);

        void surveyLogI(String tag, String msg);

        void surveyLogI(String tag, String msg, Throwable tr);

        void surveyLogW(String tag, String msg);

        void surveyLogW(String tag, String msg, Throwable tr);

        void surveyLogE(String tag, String msg);

        void surveyLogE(String tag, String msg, Throwable tr);
    }

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            if (mSurveyDebugLog != null) {
                mSurveyDebugLog.surveyLogV(tag, msg);
            }
            Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            if (mSurveyDebugLog != null) {
                mSurveyDebugLog.surveyLogV(tag, msg, tr);
            }
            Log.v(tag, msg, tr);
        }
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            if (mSurveyDebugLog != null) {
                mSurveyDebugLog.surveyLogD(tag, msg);
            }
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            if (mSurveyDebugLog != null) {
                mSurveyDebugLog.surveyLogD(tag, msg, tr);
            }
            Log.d(tag, msg, tr);
        }
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            if (mSurveyDebugLog != null) {
                mSurveyDebugLog.surveyLogI(tag, msg);
            }
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            if (mSurveyDebugLog != null) {
                mSurveyDebugLog.surveyLogI(tag, msg, tr);
            }
            Log.i(tag, msg, tr);
        }
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            if (mSurveyDebugLog != null) {
                mSurveyDebugLog.surveyLogW(tag, msg);
            }
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            if (mSurveyDebugLog != null) {
                mSurveyDebugLog.surveyLogW(tag, msg, tr);
            }
            Log.w(tag, msg, tr);
        }
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            if (mSurveyDebugLog != null) {
                mSurveyDebugLog.surveyLogE(tag, msg);
            }
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            if (mSurveyDebugLog != null) {
                mSurveyDebugLog.surveyLogE(tag, msg, tr);
            }
            Log.e(tag, msg, tr);
        }
    }

}
