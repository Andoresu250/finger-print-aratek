package com.finger.revapplution.registrarconductor.models;

import android.annotation.SuppressLint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Revapplution on 14/03/2018.
 */

public class Utils {

    public static Gson gsonWithDate(){

        final GsonBuilder builder = gsonBuilderWithDate();

        return builder.create();
    }

    public static GsonBuilder gsonBuilderWithDate(){

        final GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {

            @SuppressLint("SimpleDateFormat")
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            @Override
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                try {
                    return df.parse(json.getAsString());
                } catch (final java.text.ParseException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });

        builder.registerTypeAdapter(DateTime.class, new JsonDeserializer<DateTime>() {

            @SuppressLint("SimpleDateFormat")
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            @Override
            public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                try {
                    return new DateTime(df.parse(json.getAsString()));
                } catch (final java.text.ParseException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });

        return builder;

    }

}
