package com.example.wuzhihan.finalwork;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Wuzhihan on 2017/4/12.
 */

public class Util {
    private static final String TAG = "Util";

    public static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = cm.getActiveNetworkInfo();

        return info != null && info.isConnected();
    }

    public static String getGetParams(List<NameValuePair> pairs)
            throws Exception {
        String urlParams = "";
        for (NameValuePair nv : pairs) {
            if (urlParams.equals("")) {
                urlParams += "?";
            } else {
                urlParams += "&";
            }
            String pname = nv.getName();
            String pvalue = nv.getValue();
            if (pname != null && pname.length() > 0) {
                if (pvalue != null && pvalue.length() > 0) {
                    urlParams += (pname + "=" + URLEncoder.encode(pvalue,
                            "UTF-8"));
                } else {
                    urlParams += (pname + "=");
                }
            }
        }
        return urlParams;
    }

    public static List<NameValuePair> mapToList(Map<String, String> params) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                pairs.add(new BasicNameValuePair(entry.getKey(), entry
                        .getValue()));
            }
        }
        return pairs;
    }
}
