package tech.vee.veecoldwallet.Util;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class JsonUtil {
    private final static String TAG = "Winston";

    public static HashMap<String,Object> getJsonAsMap(String str){
        if (isJsonString(str)){
            try{
                HashMap<String,Object> gson = new Gson().fromJson(str, new TypeToken<HashMap<String, Object>>(){}.getType());
                Log.d(TAG, gson.toString());
                return gson;
            }
            catch(Exception e){
                return null;
            }
        }
        return null;
    }

    public static boolean containsKeys(HashMap<String,Object> jsonMap, String[] keys){
        for (String key:keys){
            if (!jsonMap.containsKey(key)) return false;
        }
        return true;
    }

    private static boolean isJsonString(String str){
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(str);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
