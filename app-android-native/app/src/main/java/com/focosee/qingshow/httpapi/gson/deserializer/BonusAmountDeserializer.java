package com.focosee.qingshow.httpapi.gson.deserializer;

import com.focosee.qingshow.model.vo.aggregation.BonusAmount;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/8.
 */
public class BonusAmountDeserializer implements JsonDeserializer<BonusAmount> {
    @Override
    public BonusAmount deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(json.isJsonObject()){
            JsonObject jsonObject = json.getAsJsonObject();
            BonusAmount bonusAmount = new BonusAmount();
            bonusAmount.bonuses = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                bonusAmount.bonuses.put(entry.getKey(), entry.getValue().getAsNumber());
            }
            return bonusAmount;
        }else {
            return null;
        }
    }
}
