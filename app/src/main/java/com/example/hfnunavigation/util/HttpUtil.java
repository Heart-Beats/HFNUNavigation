package com.example.hfnunavigation.util;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {

    public static void sendHttpRequest(String addressURL, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(addressURL).build();
        client.newCall(request).enqueue(callback);
    }
}
