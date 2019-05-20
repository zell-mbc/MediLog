package com.zell_mbc.medilog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
//import com.zell_mbc.medilog.MainActivity.DELIMITER;

public class WeightFragment extends Fragment {

    String DELIMITER = ";";
    public static final String DATA = "Weight";
    public String weightUnit;
    boolean verbose;

    public ArrayList<String> weightArray = new ArrayList<String>();
    ListView weightList = null;
    SharedPreferences DB = null;

    private String formatRow(String value) {

        return value.replace(";", " - ") + weightUnit;
    }


     public void addRow(String value) {
        Context c = getContext();
        if (c == null) {
            Log.d("--------------- Debug", "Empty Context");
            return;
        }
        // Get date
        Long timeStamp = (long) new Date().getTime();

        // Update DB
       DB = c.getSharedPreferences(DATA, MODE_PRIVATE);
       SharedPreferences.Editor editor = DB.edit();
       value = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timeStamp) + "; " + value;

       editor.putString(timeStamp.toString(), value);
       editor.commit();

        // Add to array
        weightArray.add(0, formatRow(value));
        weightList.invalidateViews();
    }


    public void deleteDB() {
        Context c = getContext().getApplicationContext();
        if (c == null) {
            Log.d("--------------- Debug", "Empty Context");
            return;
        }

        // Clear array
        int records = weightArray.size();
        weightArray.clear();

        // Clear Database
        DB = c.getSharedPreferences(DATA, MODE_PRIVATE);
        DB.edit().clear().apply();
        weightList.invalidateViews();

        Toast.makeText(getContext(), records + " " + getString(R.string.recordsDeleted), Toast.LENGTH_SHORT).show();
    }


    public void send(String sep) {
        Context c = getContext();
        if (c == null) {
            Log.d("--------------- Debug", "Empty Context");
            return;
        }

//        DecimalFormatSymbols locale = new DecimalFormatSymbols();
//        char separator = locale.getDecimalSeparator();
        // CSV header
        String data = getString(R.string.date) + sep + getString(R.string.weight) + System.getProperty("line.separator");

        DB = c.getSharedPreferences(DATA, MODE_PRIVATE);
        Map<String, ?> allEntries = DB.getAll();
        for(Map.Entry<String, ?> entry :allEntries.entrySet()) {
            data = data + entry.getValue().toString() + System.getProperty("line.separator");
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
        if (c == null) {
            Log.d("Debug onViewCreated: ", "Empty Context");
            return;
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        weightUnit  = " " + sharedPref.getString(SettingsActivity.KEY_PREF_WEIGHTUNIT,"kg");
        verbose = sharedPref.getBoolean(SettingsActivity.KEY_PREF_VERBOSE,false);

        // Fill array
        DB = c.getSharedPreferences(DATA, MODE_PRIVATE);
//        Toast.makeText(c.getApplicationContext(), "Debug: DB?" + DB, Toast.LENGTH_SHORT).show();

        //      DB.edit().clear().apply();  // Clear Database
        Map<String, ?> allEntries = DB.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//            Toast.makeText(getContext(), "Debug: " + entry.getKey().toString() + " " + entry.getValue().toString(), Toast.LENGTH_SHORT).show();
            weightArray.add(formatRow(entry.getValue().toString()));
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
//        Toast.makeText(c, "Weight: OnViewCreate" + weightArray.toString(), Toast.LENGTH_SHORT).show();

        // Respond to list click event
        weightList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
                alertDialogBuilder.setTitle(getString(R.string.action_deleteDB));
                // set dialog message
                alertDialogBuilder
                        .setMessage("What do you want to do?")
                        .setCancelable(false)
                        .setPositiveButton("Delete",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                Toast.makeText(c, "Delete", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        })
                        .setNeutralButton("Cancel",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        Toast.makeText(c, "Cancel", Toast.LENGTH_SHORT).show();

                                dialog.cancel();
                                    }
                        })
                        .setNegativeButton("Edit",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                Toast.makeText(c, "Edit", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });
//        Log.d("------------- Debug: weightFragment Weight", this.toString());

    }
}