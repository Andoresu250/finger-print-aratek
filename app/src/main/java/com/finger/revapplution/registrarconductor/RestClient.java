package com.finger.revapplution.registrarconductor;

import android.content.Context;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by Revapplution on 13/03/2018.
 */

public class RestClient {

    static final String TAG = "RestClient";

    private static final String format = "application/json";

    private static final String BASE_URL = "https://muvit-develop-free.herokuapp.com/api/v2/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(Context context, String url, Object object, AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException {
        client.setTimeout(30);
        Gson gson = getGson();
        String jsonString = gson.toJson(object);
        StringEntity stringEntity = new StringEntity(jsonString);
        stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, format));
        client.post(context, getAbsoluteUrl(url), stringEntity, format, responseHandler);
    }

    public static void put(Context context, String url, Object object, AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException {
        Gson gson = getGson();
        String jsonString = gson.toJson(object);
        StringEntity stringEntity = new StringEntity(jsonString);
        stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, format));
        client.put(context, getAbsoluteUrl(url), stringEntity, format, responseHandler);
    }

    public static void put(Context context, String url, String root, Object object, AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException {
        Gson gson = getGson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(root, gson.toJsonTree(object));
        String jsonString = jsonObject.toString();
        Log.i(TAG, "put: jsonString " + jsonString );
        StringEntity stringEntity = new StringEntity(jsonString);
        stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, format));
        client.put(context, getAbsoluteUrl(url), stringEntity, format, responseHandler);
    }


    private static String getAbsoluteUrl(String relativeUrl) {
        Log.i(TAG, "getAbsoluteUrl: " + BASE_URL + relativeUrl);
        return BASE_URL + relativeUrl;
    }

    public static void addHeader(String key, String value){
        client.addHeader(key, value);
    }

    private static Gson getGson(){
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }



}
