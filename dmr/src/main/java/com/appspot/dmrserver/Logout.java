package com.appspot.dmrserver;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by Taranveer on 28/11/13.
 */
public class Logout extends Activity{
    public static final String PREFS_NAME = "DMRPrefsFile";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SharedPreferences for Credential Storage
        loginPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        loginPrefsEditor.clear();
        loginPrefsEditor.commit();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.package.ACTION_LOGOUT");
        sendBroadcast(broadcastIntent);

        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
