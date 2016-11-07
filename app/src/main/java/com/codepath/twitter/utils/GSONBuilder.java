package com.codepath.twitter.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by sdevired on 11/6/16.
 */
public class GSONBuilder {

    public static Gson getGsonWithDate(){
        GsonBuilder gsonBuilder = new GsonBuilder();

        //register date adapter to convert string to date
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            SimpleDateFormat s = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
            @Override
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                try{
                    s.setTimeZone(TimeZone.getTimeZone("UTC"));
                    return s.parse(json.getAsString());

                }
                catch(ParseException ex){
                    return null;
                }
            }
        });
        Gson dateGson = gsonBuilder.create();
        return dateGson;
    }
}
