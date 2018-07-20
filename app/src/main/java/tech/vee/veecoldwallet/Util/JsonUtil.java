package tech.vee.veecoldwallet.Util;

import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;

public class JsonUtil {

    public static HashMap<String,Object> getJsonAsMap(String str){
        if (isJsonString(str)){
            try{
                HashMap<String,Object> g = new Gson().fromJson(str, new TypeToken<HashMap<String, Object>>(){}.getType());
                Log.d("Winston",g.toString());
                return g;
            }
            catch(Exception e){
                return null;
            }
        }
        return null;
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
