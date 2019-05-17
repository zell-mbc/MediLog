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
//import static com.zell_mbc.medilog.MainActivity.DELIMITER;

public class WeightFragment extends Fragment {

    public static final String DATA = "Weight";
    public String weightUnit;
    boolean verbose;

    final public ArrayList<String> weightArray = new ArrayList<String>();
    ListView weightList = null;
    SharedPreferences DB = null;

    private String formatRow(String date, String value) {
        return date + " - " + value + weightUnit;
    }


    public void addRow(String value) {
        Context c = getContext().getApplicationContext();
        Log.d("--------------- Debug: ", c.toString());
//        Log.i("Debug: ", getActivity().toString());
//        Toast.makeText(c, "Debug: " + c, Toast.LENGTH_SHORT).show();
        // Get date
        String currentDateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());

        // Update DB
        DB = c.getSharedPreferences(DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = DB.edit();
        editor.putString(currentDateAndTime, value);
        editor.commit();

        // Add to array
        weightArray.add(0, formatRow(currentDateAndTime, value));
        weightList.invalidateViews();
    }


    public void deleteDB() {
        Context c = getContext().getApplicationContext();

        // Clear array
        int records = weightArray.size();
        weightArray.clear();

        // Clear Database
        DB = c.getSharedPreferences(DATA, MODE_PRIVATE);
        DB.edit().clear().apply();
        weightList.invalidateViews();

        Toast.makeText(getContext(), records + getString(R.string.recordsDeleted), Toast.LENGTH_SHORT).show();
    }


    public void send(String sep) {
        Context c = getContext().getApplicationContext();

//        DecimalFormatSymbols locale = new DecimalFormatSymbols();
//        char separator = locale.getDecimalSeparator();
        // CSV header
        String data = getString(R.string.date) + sep + getString(R.string.weight) + System.getProperty("line.separator");

        Log.d("--------------- Debug: ", "" + sep);

        DB = c.getSharedPreferences(DATA, MODE_PRIVATE);
        Map<String, ?> allEntries = DB.getAll();
        for(Map.Entry<String, ?> entry :allEntries.entrySet()) {
            data = data + entry.getKey().toString() + sep + entry.getValue().toString() + System.getProperty("line.separator");
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,data);
        sendIntent.setType("text/plain");

        startActivity(Intent.createChooser(sendIntent, getString(R.string.sendWMessage)));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.weight_tab, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final Context c = getContext();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        weightUnit  = " " + sharedPref.getString(SettingsActivity.KEY_PREF_WEIGHTUNIT,"kg");
        verbose = sharedPref.getBoolean(SettingsActivity.KEY_PREF_VERBOSE,false);

        // Fill array
        weightArray.clear(); // Clear array because this method gets called often
        DB = c.getSharedPreferences(DATA, MODE_PRIVATE);
//        Toast.makeText(c.getApplicationContext(), "Debug: :" + DB, Toast.LENGTH_SHORT).show();

        //      DB.edit().clear().apply();  // Clear Database
        Map<String, ?> allEntries = DB.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//            Toast.makeText(getContext(), "Debug: " + entry.getKey().toString() + " " + entry.getValue().toString(), Toast.LENGTH_SHORT).show();
            weightArray.add(formatRow(entry.getKey().toString(), entry.getValue().toString()));
        }

        // Should not be necessary but well...
        Collections.sort(weightArray, Collections.reverseOrder());

        // Map array to list
        weightList = view.findViewById(R.id.weightList);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(c, android.R.layout.simple_list_item_1, weightArray);
        weightList.setAdapter(arrayAdapter);

        if (verbose) {
            int records = weightArray.size();
            Toast.makeText(getContext(), records + " " + getString(R.string.weight)+ " " +getString(R.string.recordsLoaded), Toast.LENGTH_SHORT).show();
        }

        // Respond to list click event
        weightList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(c.getApplicationContext(), "Clicked:" + weightArray.get(position), Toast.LENGTH_SHORT).show();
            }
        });

    }
}