package br.com.mobila.blipdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.google.gson.Gson;

/**
 * Created by Leonardo Saganski on 27/11/16.
 */
public class Globals {

    private static Globals instance;

    public Context applicationContext;
    public boolean locating;
    public Handler handlerHome;

    public String tokenType;
    public String accessToken;

    public String resp_ID;
    public String resp_name;
    public String resp_displayname;
    public String resp_thumbnailurl;
    public String resp_matchtypes;
    public String resp_score;
    public String resp_passparams;

    public byte[] selectedPhoto;

    public static SharedPreferences mPrefs;
    public static SharedPreferences.Editor prefsEditor;
    public static Gson gson;

    public static Globals getInstance() {
        if (instance == null)
            instance = new Globals();

        return instance;
    }

    public Globals() {

    }
}
