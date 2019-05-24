package com.zell_mbc.medilog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new AboutFragment())
                .commit();

    }
}

//            android:configChanges="orientation|screenSize|keyboardHidden"

