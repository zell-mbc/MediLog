package com.zell_mbc.medilog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WeightChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new WeightChartFragment())
                .commit();

    }
}
