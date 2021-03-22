package com.nand_project.www.smartabsensi.SessionManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


import java.util.HashMap;


public class SessionManager {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;

    private static final String PREF_NAME   = "LOGIN";
    private static final String LOGIN       = "IS_LOGIN";
    public static final String ID           = "ID";

    public SessionManager(Context context){
        this.context = context;
        sharedPreferences= context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String id){
        editor.putBoolean(LOGIN, true);
        editor.putString(ID, id);
        editor.apply();
    }

    public boolean isLogin(){
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLogin(){
        if (!this.isLogin()){
//            Intent intent = new Intent(context, LoginActivity.class);
//            context.startActivity(intent);
        }
    }

    public HashMap<String, String> getSessionData(){
        HashMap<String, String> user = new HashMap<>();
        user.put(ID, sharedPreferences.getString(ID, null));
        return user;
    }

    public void logout(){
        editor.clear();
        editor.commit();
//        Intent intent = new Intent(context, LoginActivity.class);
//        MainActivity.mainactivity.finish();
//        context.startActivity(intent);
    }
}
