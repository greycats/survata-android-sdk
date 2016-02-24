package com.survata.utils;

import android.util.Log;

import com.survata.BuildConfig;
import com.survata.Survey;

public class Logger {

    private static Survey.SurvataLogger mSurvataLogger;

    public static void setmSurvataLogger(Survey.SurvataLogger mSurvataLogger) {
        Logger.mSurvataLogger = mSurvataLogger;
    }

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            if (mSurvataLogger != null) {
                mSurvataLogger.surveyLogVerbose(tag, msg);
            } else {
                Log.v(tag, msg);
            }
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            if (mSurvataLogger != null) {
                mSurvataLogger.surveyLogVerbose(tag, msg, tr);
            } else {
                Log.v(tag, msg, tr);
            }
        }
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            if (mSurvataLogger != null) {
                mSurvataLogger.surveyLogDebug(tag, msg);
            } else {
                Log.d(tag, msg);
            }
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            if (mSurvataLogger != null) {
                mSurvataLogger.surveyLogDebug(tag, msg, tr);
            } else {
                Log.d(tag, msg, tr);
            }
        }
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            if (mSurvataLogger != null) {
                mSurvataLogger.surveyLogInfo(tag, msg);
            } else {
                Log.i(tag, msg);
            }
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            if (mSurvataLogger != null) {
                mSurvataLogger.surveyLogInfo(tag, msg, tr);
            } else {
                Log.i(tag, msg, tr);
            }
        }
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            if (mSurvataLogger != null) {
                mSurvataLogger.surveyLogWarn(tag, msg);
            } else {
                Log.w(tag, msg);
            }
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            if (mSurvataLogger != null) {
                mSurvataLogger.surveyLogWarn(tag, msg, tr);
            } else {
                Log.w(tag, msg, tr);
            }
        }
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            if (mSurvataLogger != null) {
                mSurvataLogger.surveyLogError(tag, msg);
            } else {
                Log.e(tag, msg);
            }
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            if (mSurvataLogger != null) {
                mSurvataLogger.surveyLogError(tag, msg, tr);
            } else {
                Log.e(tag, msg, tr);
            }
        }
    }

}
