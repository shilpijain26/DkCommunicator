package com.dk.dkcommunicator;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by apple on 23/08/17.
 */

public class SharedData {
    public SharedPreferences sharedPreferences;
    private Context context;
    public SharedData(Context context){
        this.context=context;
        sharedPreferences = context.getSharedPreferences("ShareData", Context.MODE_APPEND);
    }

    public void AddData(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public boolean checkData(String key){
        return sharedPreferences.contains(key);
    }

    public String getValue(String key){
        String val = sharedPreferences.getString(key, "");
        return val;
    }
    public void removeData(String key){
        sharedPreferences.edit().remove(key).commit();
    }
}
