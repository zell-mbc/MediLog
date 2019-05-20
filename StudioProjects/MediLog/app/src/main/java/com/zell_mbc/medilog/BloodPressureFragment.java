package com.zell_mbc.medilog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class BloodPressureFragment extends Fragment {

    public static final String DATA = "BloodPressure";
    boolean verbose;

    String DELIMITER = ";";

    final public ArrayList<String>bloodPressureArray = new ArrayList<String>();
    ListView pressureList = null;
    SharedPreferences DB = null;

    private String formatRow(String value) {
        return value.replaceAll(";", " ");
    }


    public void addRow(String value) {
        Context c = getContext();
        if (c == null) {
            Log.d("--------------- Debug", "Empty Context");
            return;
        }

        Long timeStamp = (long) new Date().getTime();
        value = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timeStamp) + DELIMITER + value;


        // Update DB
        DB = c.getSharedPreferences(DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = DB.edit();
        editor.putString(timeStamp.toString(), value);
        editor.commit();

        bloodPressureArray.add(0, formatRow(value)); // Add to array
        pressureList.invalidateViews();
    }


    public void deleteDB() {
        Context c = getContext();
        if (c == null) {
            Log.d("--------------- Debug", "Empty Context");
            return;
        }

        int records = bloodPressureArray.size();
        bloodPressureArray.clear(); // Clear array

        // Clear Database
        DB = c.getSharedPreferences(DATA, MODE_PRIVATE);
        DB.edit().clear().apply();
        pressureList.invalidateViews();

        Toast.makeText(getContext(), records + " " + getString(R.string.recordsDeleted), Toast.LENGTH_SHORT).show();
    }


    public void send(String sep) {
        Context c = getContext().getApplicationContext();

        String data = getString(R.string.date) + sep + getString(R.string.systolic) + sep + getString(R.string.diatolic) + sep + getString(R.string.pulse) + sep + getString(R.string.comment) +  System.getProperty ("line.separator");

//    SharedPreferences DB = getSharedPreferences(DATA, MODE_PRIVATE);
        DB = c.getSharedPreferences(DATA, MODE_PRIVATE);
        Map<String, ?> allEntries = DB.getAll();
        for(Map.Entry<String, ?> entry :allEntries.entrySet()) {
            data = data + entry.getValue().toString() + System.getProperty("line.separator");
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,data);
        sendIntent.setType("text/plain");

        startActivity(Intent.createChooser(sendIntent, getString(R.string.sendBPMessage)));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bloodpressure_tab, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final Context c = getContext();
        if (c == null) {
            Log.d("Debug onViewCreated: ", "Empty Context");
            return;
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        verbose = sharedPref.getBoolean(SettingsActivity.KEY_PREF_VERBOSE,false);

        // Fill array
        DB = c.getSharedPreferences(DATA, MODE_PRIVATE);
//        DB.edit().clear().apply();
        Map<String, ?> allEntries = DB.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            bloodPressureArray.add(formatRow(entry.getValue().toString()));
        }

        // Should not be necessary but well...
        Collections.sort(bloodPressureArray,Collections.reverseOrder());

        // Map array to list
        pressureList = view.findViewById(R.id.pressureList);

//                Toast.makeText(c, "Debug: " + bloodPressureArray.size(), Toast.LENGTH_SHORT).show();

        ArrayAdapter<String> bloodPressureArrayAdapter = new ArrayAdapter<String>(c, android.R.layout.simple_list_item_1, bloodPressureArray);
        pressureList.setAdapter(bloodPressureArrayAdapter);

        if (verbose) {
            int records = bloodPressureArray.size();
            Toast.makeText(getContext(), records + " " + getString(R.string.blood_pressure)+ " " +getString(R.string.recordsLoaded), Toast.LENGTH_SHORT).show();
        }

        // Respond to list click event
        pressureList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(c.getApplicationContext(), "Clicked:" + bloodPressureArray.get(position), Toast.LENGTH_SHORT).show();
            }
        });

    };
}