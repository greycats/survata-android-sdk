package com.survata.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.survata.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;

public class Utils {

    private static final String TAG = "Utils";

    private static final String USER_AGENT = "%s Survata/Android/%s";

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private static final int EOF = -1;

    public static String parseParamMap(Map<String, String> params) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String value = entry.getValue();

            if (!TextUtils.isEmpty(value)) {

                String key = entry.getKey();
                try {
                    jsonObject.put(key, value);
                } catch (JSONException e) {
                    Logger.d(TAG, "parse param failed", e);
                }
            }
        }
        return jsonObject.toString();
    }

    public static String getUserAgent(Context context) {
        String packageName = "Unknown";
        String versionName = "Unknown";
        try {
            packageName = context.getPackageName();
            versionName = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (Exception e) {
            Logger.e(TAG, "get user agent failed", e);
        }

        String currentUserAgent = context.getString(R.string.app_name) + "/" + packageName;
        return String.format(USER_AGENT, currentUserAgent, versionName);
    }

    public static String encodeImage(Context context, String imageName) {
        String encodeString = "";
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(imageName);
            byte[] bytes = toByteArray(inputStream);
            //base64 encode
            byte[] encode = Base64.encode(bytes, Base64.NO_WRAP);
            encodeString = new String(encode);
        } catch (IOException e) {
            Logger.e(TAG, "encode image failed", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                Logger.e(TAG, "close InputStream failed", e);
            }
        }

        return encodeString;
    }

    public static String getFromAssets(String fileName, Context context) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(fileName), "UTF-8"));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            Logger.e(TAG, "get from assets failed", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Logger.e(TAG, "close BufferedReader failed", e);
            }
        }

        return sb.toString();
    }

    private static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    private static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    private static long copyLarge(InputStream input, OutputStream output)
            throws IOException {
        return copyLarge(input, output, new byte[DEFAULT_BUFFER_SIZE]);
    }

    private static long copyLarge(InputStream input, OutputStream output, byte[] buffer)
            throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
