package com.focosee.qingshow.httpapi.gson.deserializer;

import com.focosee.qingshow.util.TimeUtil;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.GregorianCalendar;

/**
 * Created by i068020 on 2/28/15.
 * <p/>
 * Ref:
 * http://stackoverflow.com/questions/26044881/java-date-to-utc-using-gson
 * https://code.google.com/p/google-gson/issues/detail?id=281
 */
public class UTCDeserializer implements JsonDeserializer<GregorianCalendar> {


    public UTCDeserializer() {
    }

    @Override
    public GregorianCalendar deserialize(JsonElement jsonElement, Type type,
                                         JsonDeserializationContext jsonDeserializationContext) {
        try {
            return TimeUtil.parseUTC(getNullAsEmptyString(jsonElement));
        } catch (Exception e) {
            return null;
        }
    }

    private String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }
}
