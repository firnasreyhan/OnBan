package com.andorid.go_bengkel.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.andorid.go_bengkel.model.UserAppModel;
import com.google.gson.Gson;

public class AppPreference {
    static final String PREF = "PREF";
    static final String USER_PREF = "USER_PREF";

    public static void saveUser(Context context, UserAppModel user){
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit().putString(USER_PREF, new Gson().toJson(user)).apply();
    }

    public static UserAppModel getUser(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        if(pref.contains(USER_PREF)){
            Gson gson = new Gson();

            return gson.fromJson(pref.getString(USER_PREF, ""), UserAppModel.class);
        }

        return null;
    }

    public static void removeUser(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        if(pref.contains(USER_PREF)){
            pref.edit().remove(USER_PREF).apply();
        }
    }
}
