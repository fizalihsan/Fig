package com.fig.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Helper class for Data binding purposes
 * User: Fizal
 * Date: 11/21/13
 * Time: 1:03 PM
 */
public final class BindingUtil {

    private static final GsonBuilder gsonBuilder = new GsonBuilder();
    private static final GsonBuilder prettyGsonBuilder = new GsonBuilder().setPrettyPrinting();

    public synchronized static <T> String toJson(T object){
        if(object instanceof JsonSerializer){
            gsonBuilder.registerTypeAdapter(object.getClass(), object);
        }
        Gson gson = gsonBuilder.create();
        return gson.toJson(object);
    }

    public synchronized static <T> String toPrettyJson(T object){
        if(object instanceof JsonSerializer){
            prettyGsonBuilder.registerTypeAdapter(object.getClass(), object);
        }
        Gson gson = prettyGsonBuilder.create();
        return gson.toJson(object);
    }

    public synchronized static <T> T fromJson(String json, Class<T> clazz){
        return (T) gsonBuilder.create().fromJson(json, clazz);
    }

    public synchronized static <T> Collection<T> fromJsonArray(String json, Class<T> clazz){
        final Type type = new TypeToken<Collection<T>>() {}.getType();
        return (Collection<T>) gsonBuilder.create().fromJson(json, type);
    }
}
