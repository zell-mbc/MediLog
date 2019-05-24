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

public class WeightFragment extends Fragment {

    String DELIMITER = ";";
    String SEPARATOR = " - ";
    SQLiteDatabase healthDB;
    String dbName = "healthDB";
    String tableName = "weight";

    public String weightUnit;
    boolean verbose;

    public ArrayList<String> weightArray = new ArrayList<String>();
    ListView weightList = null;
    ImageButton button_addWeight = null;
    EditText weight;
    SharedPreferences DB = null;

    private String formatRow(String timeStamp, String value) {

        return timeStamp.substring(0, 16) + SEPARATOR + value + " " + weightUnit;
    }


    public void addRow() {
        Context c = getContext();
        if (c == null) {
            Log.d("--------------- Debug", "Empty Context");
            return;
        }

        // Check empty variables
        String value = weight.getText().toString();
        if (value.length() == 0) {
            Toast.makeText(c, getString(R.string.weightMissing), Toast.LENGTH_SHORT).show();
            return;
        }

        //      Log.d("--------------- Debug", c.toString());

        // Get date
        Long lTmp = new Date().getTime();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(lTmp);

        // Update DB
        try {
            healthDB = c.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            ContentValues values = new ContentValues();
            values.put("timestamp", timeStamp);
            values.put("weight", value);

            Long result = healthDB.insert(tableName, null, values);
            Log.d("--------------- Debug", " " + result);

            healthDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        weight.setText("");

        // Add to array
        weightArray.add(0, formatRow(timeStamp, value));
        weightList.invalidateViews();
    }


    public void deleteRow(int index) {
        Context c = getContext();
        if (c == null) {
            Log.d("--------------- Debug", "Empty Context");
            return;
        }

        String key = weightArray.get(index);
        String timeStamp = key.substring(0, 16);

        // Update DB
        try {
            healthDB = c.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            int result = healthDB.delete("weight", "timestamp='" + timeStamp + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Remove from array
        weightArray.remove(index);
        weightList.invalidateViews();
    }


    public void editRow(int index) {
        Context c = getContext();
        if (c == null) {
            Log.d("--------------- Debug", "Empty Context");
            return;
        }

        String key = weightArray.get(index);
        String timeStamp = key.substring(0, 16);

        // Show two fields, date and value
        // Sanity checks
        String newWeight="999";

        // Update DB
        try {
            healthDB = c.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            ContentValues values = new ContentValues();
            values.put("timestamp", timeStamp);
            values.put("weight", newWeight);

            Long result = healthDB.insert(tableName, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }



        // Remove from array
//        weightArray.remove(index);
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
        try {
            healthDB = c.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            int result = healthDB.delete("weight", null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        weightList.invalidateViews();
        Toast.makeText(getContext(), records + " " + getString(R.string.recordsDeleted), Toast.LENGTH_SHORT).show();
    }


    public void send(String sep) {
/*        Context c = getContext();
        if (c == null) {
            Log.d("--------------- Debug", "Empty Context");
            return;
        }
*/
        // CSV header
        String sTmp;
        String data = getString(R.string.date) + sep + getString(R.string.weight) + System.getProperty("line.separator");
        for (String row : weightArray) {
            sTmp = row.replace(" " + weightUnit,"");
            sTmp = sTmp.replace(SEPARATOR,DELIMITER);
            data = data + sTmp + System.getProperty("line.separator");
//            Log.d("--------------- Debug", data);
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, data);
        sendIntent.setType("text/plain");

        startActivity(Intent.createChooser(sendIntent, getString(R.string.sendWMessage)));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context c = getContext();
        if (c == null) {
            Log.d("Debug onViewCreated: ", "Empty Context");
            return inflater.inflate(R.layout.weight_tab, container, false);
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        weightUnit = " " + sharedPref.getString(SettingsActivity.KEY_PREF_WEIGHTUNIT, "kg");
        verbose = sharedPref.getBoolean(SettingsActivity.KEY_PREF_VERBOSE, false);

        try {
            healthDB = c.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            healthDB.execSQL("CREATE TABLE IF NOT EXISTS weight (timestamp VARCHAR, weight VARCHAR)");
//            String [] columns = { "timestamp", "weight" };
            Cursor weightCursor = healthDB.query("weight", null,null, null,null,null, "timestamp DESC",null);
//            healthDB.execSQL("CREATE TABLE IF NOT EXISTS bloodpressure (date LONG, sys INT(3), dia INT(3), pulse INT(3)) ");

//            Cursor weightCursor        = healthDB.rawQuery("SELECT * FROM weight", null);
//            Cursor bloodPressureCursor = healthDB.rawQuery("SELECT * FROM bloodpressure", null);

//            Cursor weightCursor = healthDB.rawQuery("SELECT * FROM weight ORDER BY timestamp DESC", null);

            int dateIndex = weightCursor.getColumnIndex("timestamp");
            int weightIndex = weightCursor.getColumnIndex("weight");

            while (weightCursor.moveToNext()) {
                Log.d("Debug Cursor: ", weightCursor.getString(dateIndex) + " " + weightCursor.getString(weightIndex));
                weightArray.add(formatRow(weightCursor.getString(dateIndex), weightCursor.getString(weightIndex)));
//                Toast.makeText(getContext(), "Debug: " + weightCursor.getString(weightIndex), Toast.LENGTH_SHORT).show();
            }
            weightCursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return inflater.inflate(R.layout.weight_tab, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final Context c = getContext();
        if (c == null) {
            Log.d("Debug onViewCreated: ", "Empty Context");
            return;
        }

        // Map array to list
        weightList = view.findViewById(R.id.weightList);
        weight = view.findViewById(R.id.editWeight);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(c, android.R.layout.simple_list_item_1, weightArray);
        weightList.setAdapter(arrayAdapter);

        if (verbose) {
            int records = weightArray.size();
            Toast.makeText(getContext(), records + " " + getString(R.string.weight) + " " + getString(R.string.recordsLoaded), Toast.LENGTH_SHORT).show();
        }

        // Respond to list click events
        button_addWeight = view.findViewById(R.id.button_addWeight);
        button_addWeight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addRow();
            }

            ;
        });

        weightList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final int pos = position;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
                alertDialogBuilder.setTitle(getString(R.string.ItemClicked) + " " + weightArray.get(position));
                // set dialog message
                alertDialogBuilder
                        .setMessage("What do you want to do?")
                        .setCancelable(false)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteRow(position);
                                dialog.cancel();
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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
//        Log.d("------------- Debug: weightFragment Weight", this.toString());

    }

    /*
    @Override
    public void onDestroy() {

        try {
            healthDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }
*/
}
