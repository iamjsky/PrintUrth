package com.printurth;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by steve on 2017. 11. 7..
 */

public class SessionManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "AndroidHivePref";



    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }

    public void setDB() {
        editor.putBoolean(KEY_SET_DB, true);
        editor.commit();
    }

//    public void setLoggedIn() {
//        editor.putBoolean(KEY_LOGGED_IN, true);
//        editor.commit();
//    }




    public boolean isSetDB() {
        return pref.getBoolean(KEY_SET_DB, false);
    }

//    public String getToken() {
//        return pref.getString(KEY_AUTH_TOKEN, null);
//    }
//
//    public void logout() {
//        editor.putString(KEY_AUTH_TOKEN, null);
//        editor.putBoolean(KEY_LOGGED_IN, false);
//        editor.commit();
//    }

    private static final String KEY_LOGGED_IN = "key_logged_in";
    private static final String KEY_SET_DB = "key_set_db";
    private static final String KEY_GUIDE_SEEN = "key_guide_seen";
    private static final String KEY_VIEWGUIDE_SEEN = "key_viewguide_seen";

    public boolean isFirstLogin() {
        return !pref.getBoolean(KEY_GUIDE_SEEN, false);
    }

}
