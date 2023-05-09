package com.voocoo.pet.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JSONHelper {
    private static final String TAG = "JSONHelper";

    private static final GsonBuilder sGsonBuilder;
    private static final Gson sGson;

    static {
        sGsonBuilder = new GsonBuilder();
        sGsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT,
                                    JsonDeserializationContext context)
                    throws JsonParseException {
                String date = json.getAsJsonPrimitive().getAsString();
                String JSONDateToMilliseconds = "\\/(Date\\((.*?)(\\+.*)?\\))\\/";
                Pattern pattern = Pattern.compile(JSONDateToMilliseconds);
                Matcher matcher = pattern.matcher(date);
                String result = matcher.replaceAll("$2");
                try {
                    return new Date(Long.valueOf(result));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        sGson = sGsonBuilder.create();
    }


    public static <T> List<T> getList(JsonElement jsonElement, String listName, Type listType) {
        JsonElement listElement = null;
        if (jsonElement.isJsonObject()) {
            JsonObject listObject = (JsonObject) jsonElement;
            if (listObject.has(listName)) {
                listElement = listObject.get(listName);
                if (!listElement.isJsonArray()) {
                    listElement = null;
                }
            }
        } else if (jsonElement.isJsonArray()) {
            listElement = jsonElement;
        }
        if (listElement != null) {
            return sGson.fromJson(listElement, listType);
        } else {
            LogUtil.e("Error: parse json array but not found the array element");
            return null;
        }
    }


    public static <T> T fromJson(String JSONString, Class<T> classOfT) {
        T entity = null;
        try {
            entity = sGson.fromJson(JSONString, classOfT);
        } catch (Exception e) {
            LogUtil.e("fromJson()==" + e);
        }
        return entity;
    }

    /**
     * 用于内部有json数组
     *
     * @param JSONString
     * @param classOfT
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String JSONString, Type classOfT) {
        T entity = null;
        try {
            entity = sGson.fromJson(JSONString, classOfT);
        } catch (Exception e) {
            LogUtil.e("fromJson() parse error ==" + e);
        }
        return entity;
    }

    public static <T> T fromJson(JsonElement jsonElement, Class<T> targetClass) {
        return sGson.fromJson(jsonElement, targetClass);
    }

    public static <T> T fromJson(JsonElement jsonElement, Type type) {
        return sGson.fromJson(jsonElement, type);
    }

    public static <T> T fromJson(JsonElement jsonElement, Type type, JsonDeserializer<T> deserializer) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(type, deserializer);
        Gson gson = gsonBuilder.create();
        return gson.fromJson(jsonElement, type);
    }

    public static JsonElement toJsonTree(Object obj) {
        return sGson.toJsonTree(obj);
    }

    public static JsonElement toJsonTree(Object obj, Type type) {
        return sGson.toJsonTree(obj, type);
    }

    public static String toJson(Object src) {
        return sGson.toJson(src);
    }

    public static String toJson(Object obj, Class targetClass) {
        return sGson.toJson(obj, targetClass);
    }

    public static void toJson(JsonElement jsonElement, JsonWriter writer) {
        sGson.toJson(jsonElement, writer);
    }

    /**
     * Load a json object rom filepath
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    public static JsonObject loadJson(File file) throws FileNotFoundException {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(new FileReader(file));
        return jsonElement.getAsJsonObject();
    }
}
