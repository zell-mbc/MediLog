package com.zell_mbc.medilog;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;


public class BloodPressureFragment extends Fragment {

    String DELIMITER = ";";
    String SEPARATOR = " - ";
    SQLiteDatabase healthDB;
    String dbName = "healthDB";
    String tableName = "bloodPressureTB";

    ImageButton button_addBloodPressure = null;
    boolean verbose;

    final public ArrayList<String>bloodPressureArray = new ArrayList<>();
    ListView pressureList = null;
    EditText sys;
    EditText dia;
    EditText pulse;
    EditText comment;

    // Classes
    private String formatRow(String key, String value) {
        return key + SEPARATOR + value.replaceAll(";", " ");
    }


    public void addRow() {
        Context c = getContext();
        if (c == null) {
            Log.d("--------------- Debug", "Empty Context");
            return;
        }

        // ###########################
        // Checks
        // ###########################
        // Check empty variables
        if (sys.getText().toString().length() == 0) {
            Toast.makeText(c, getString(R.string.sysMissing), Toast.LENGTH_SHORT).show();
            return;
        }
        if (dia.getText().toString().length() == 0) {
            Toast.makeText(c, getString(R.string.diaMissing), Toast.LENGTH_SHORT).show();
            return;
        }
        if (pulse.getText().toString().length() == 0) {
            Toast.makeText(c, getString(R.string.pulseMissing), Toast.LENGTH_SHORT).show();
            return;
        }

        // Check sanity
        String sTmp = sys.getText().toString();
        int iTmp = Integer.parseInt(sTmp);
        if(iTmp > 200 ){
            Toast.makeText(c, "Systolic value too high to be plausible!", Toast.LENGTH_SHORT).show();
            return;
        }

        sTmp = dia.getText().toString();
        iTmp = Integer.parseInt(sTmp);
        if(iTmp > 300 ){
            Toast.makeText(c, "Diastolic value too high to be plausible!", Toast.LENGTH_SHORT).show();
            return;
        }

        sTmp = pulse.getText().toString();
        iTmp = Integer.parseInt(sTmp);
        if(iTmp > 200 ){
            Toast.makeText(c, "Pulse value too high to be plausible!", Toast.LENGTH_SHORT).show();
            return;
        }

        // ###########################
        // Save values
        // ###########################
        String value = sys.getText().toString() + DELIMITER + dia.getText().toString() + DELIMITER + pulse.getText().toString() + DELIMITER + comment.getText().toString() ;


        // Get date
        Long lTmp = new Date().getTime();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(lTmp);

        // Update DB
        try {
            healthDB = c.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            ContentValues values = new ContentValues();
            values.put("timestamp", timeStamp);
            values.put("sys", Integer.parseInt(sys.getText().toString()));
            values.put("dia", Integer.parseInt(dia.getText().toString()));
            values.put("pulse", Integer.parseInt(pulse.getText().toString()));
            values.put("comment", comment.getText().toString());

            Long result = healthDB.insert(tableName, null, values);
            Log.d("--------------- Debug", " " + result);

            healthDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        bloodPressureArray.add(0, formatRow(timeStamp, value)); // Add to array
        Log.d("--------------- Debug", bloodPressureArray.get(0));
        pressureList.invalidateViews();

        sys.setText("");
        dia.setText("");
        pulse.setText("");
        comment.setText("");
        sys.bringToFront();

    }


    public void deleteRow(int index) {
        Context c = getContext();
        if (c == null) {
            Log.d("--------------- Debug", "Empty Context");
            return;
        }

        String key = bloodPressureArray.get(index);
        String timeStamp = key.substring(0, 16);
        Log.d("--------------- Debug", key);

        // Update DB
        try {
            healthDB = c.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            int result = healthDB.delete(tableName, "timestamp='" + timeStamp + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Remove from array
        bloodPressureArray.remove(index);
        pressureList.invalidateViews();
    }


    public void editRow(int index) {
        Context c = getContext();
        if (c == null) {
            Log.d("--------------- Debug", "Empty Context");
            return;
        }

        String key = bloodPressureArray.get(index);
        String timeStamp = key.substring(0, 16);

        // Show two fields, date and value
        // Sanity checks
        String newWeight="999";

        // Update DB
        try {
            healthDB = c.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            ContentValues values = new ContentValues();

            Long result = healthDB.insert(tableName, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Remove from array
//        weightArray.remove(index);
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
         try {
             healthDB = c.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
             int result = healthDB.delete(tableName, null, null);
         } catch (Exception e) {
             e.printStackTrace();
         }

        pressureList.invalidateViews();

        Toast.makeText(getContext(), records + " " + getString(R.string.recordsDeleted), Toast.LENGTH_SHORT).show();
    }


    public void send(String sep) {
        Context c = getContext();
        if (c == null) {
            Log.d("--------------- Debug", "Empty Context");
            return;
        }

        // CSV header
        String sTmp;
        String data = getString(R.string.date) + DELIMITER + getString(R.string.systolic) + DELIMITER + getString(R.string.diatolic) + DELIMITER + getString(R.string.pulse) + DELIMITER + getString(R.string.comment) +  System.getProperty ("line.separator");
        for (String row : bloodPressureArray) {
            sTmp = row.replace(SEPARATOR,DELIMITER);
            data = data + sTmp + System.getProperty("line.separator");
        }
            Log.d("--------------- Debug", data);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,data);
        sendIntent.setType("text/plain");

        startActivity(Intent.createChooser(sendIntent, getString(R.string.sendBPMessage)));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final Context c = getContext();
        if (c == null) {
            Log.d("Debug onViewCreated: ", "Empty Context");
            return inflater.inflate(R.layout.weight_tab, container, false);
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        verbose = sharedPref.getBoolean(SettingsActivity.KEY_PREF_VERBOSE,false);

        try {
            healthDB = c.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            String command = "CREATE TABLE IF NOT EXISTS " + tableName + " (timestamp VARCHAR, sys INT(3), dia INT(3), pulse INT(3), comment VARCHAR)";
            healthDB.execSQL(command);
            Cursor bloodPressureCursor = healthDB.query(tableName, null,null, null,null,null, "timestamp DESC",null);

            int dateIndex    = bloodPressureCursor.getColumnIndex("timestamp");
            int sysIndex     = bloodPressureCursor.getColumnIndex("sys");
            int diaIndex     = bloodPressureCursor.getColumnIndex("dia");
            int pulseIndex   = bloodPressureCursor.getColumnIndex("pulse");
            int commentIndex = bloodPressureCursor.getColumnIndex("comment");

            String sTmp;
            while (bloodPressureCursor.moveToNext()) {
                Log.d("Debug Cursor: ", bloodPressureCursor.getString(dateIndex) + " " + bloodPressureCursor.getString(sysIndex));
                sTmp = bloodPressureCursor.getString(dateIndex) + SEPARATOR + bloodPressureCursor.getString(sysIndex) + " " + bloodPressureCursor.getString(diaIndex) + " " + bloodPressureCursor.getString(pulseIndex) + " " + bloodPressureCursor.getString(commentIndex);
                bloodPressureArray.add(sTmp);
            }
            bloodPressureCursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return inflater.inflate(R.layout.bloodpressure_tab, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final Context c = getContext();
        if (c == null) {
            Log.d("Debug onViewCreated: ", "Empty Context");
            return;
        }

        // Map array to list
        pressureList = view.findViewById(R.id.pressureList);
        sys     = view.findViewById(R.id.editSys);
        dia     = view.findViewById(R.id.editDia);
        pulse   = view.findViewById(R.id.editPulse);
        comment = view.findViewById(R.id.editComment);

//                Toast.makeText(c, "Debug: " + bloodPressureArray.size(), Toast.LENGTH_SHORT).show();

        ArrayAdapter<String> bloodPressureArrayAdapter = new ArrayAdapter<String>(c, android.R.layout.simple_list_item_1, bloodPressureArray);
        pressureList.setAdapter(bloodPressureArrayAdapter);

        if (verbose) {
            int records = bloodPressureArray.size();
            Toast.makeText(getContext(), records + " " + getString(R.string.blood_pressure)+ " " +getString(R.string.recordsLoaded), Toast.LENGTH_SHORT).show();
        }

        // Respond to list click events
        button_addBloodPressure = view.findViewById(R.id.button_addBloodPressure);
        button_addBloodPressure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { addRow(); };
        });

        pressureList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final int pos = position;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
                alertDialogBuilder.setTitle(getString(R.string.ItemClicked)+ " " + bloodPressureArray.get(position));
                // set dialog message
                alertDialogBuilder
                        .setMessage("What do you want to do?")
                        .setCancelable(false)
                        .setPositiveButton("Delete",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                deleteRow(position);
                                dialog.cancel();
                            }
                        })
                        .setNeutralButton("Cancel",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
/*                        })
                        .setNegativeButton("Edit",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            } */
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    };
}

/*
    @Override
    public void onDestroy() {

        try {
            //           healthDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

}
*/