package com.zell_mbc.medilog;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    public static final String KEY_PREF_DELIMITER = "delimiter";
    public static final String KEY_PREF_WEIGHTUNIT = "weightUnit";
    public static final String KEY_PREF_VERBOSE = "verbose";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
