package com.fig.util;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;

/**
 * Helper class for Data binding purposes
 * User: Fizal
 * Date: 11/21/13
 * Time: 1:03 PM
 */
public final class BindingUtil {
    private static final GsonBuilder gsonBuilder = new GsonBuilder();
    private static final GsonBuilder prettyGsonBuilder = new GsonBuilder().setPrettyPrinting();

    public static <T> String toJson(T object){
        if(object instanceof JsonSerializer){
            gsonBuilder.registerTypeAdapter(object.getClass(), object);
        }
        return gsonBuilder.create().toJson(object);
    }

    public static <T> String toPrettyJson(T object){
        if(object instanceof JsonSerializer){
            prettyGsonBuilder.registerTypeAdapter(object.getClass(), object);
        }
        return prettyGsonBuilder.create().toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> clazz){
        return gsonBuilder.create().fromJson(json, clazz);
    }

    public static <T> T[] fromJsonArray(String json, Class<T[]> clazz){
        return gsonBuilder.create().fromJson(json, clazz);
    }
}
