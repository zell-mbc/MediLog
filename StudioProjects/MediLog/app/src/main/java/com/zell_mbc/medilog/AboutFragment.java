package com.zell_mbc.medilog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment {

    public static final String VERSION = "v 0.9.0.3";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_about, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView v = view.findViewById(R.id.text_version);
        v.setText(VERSION);

        v = view.findViewById(R.id.appDescription);
        v.setText(getString(R.string.appDescription));
    }
}