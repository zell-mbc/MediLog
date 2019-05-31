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
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
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
    String tableName = "weightTB";

    public String weightUnit;
    boolean verbose;
    String editField = "";

    public ArrayList<String> weightArray = new ArrayList<String>();
    public ArrayList<Long> weightArrayHelper = new ArrayList<Long>();
    ListView weightList = null;
    ImageButton button_addWeight = null;
    ImageButton button_showWeightChart = null;
    EditText weight;
    SharedPreferences DB = null;

    private String formatRow(Long timeStamp, String value) {
        String ts = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timeStamp);
        return ts.substring(0, 16) + SEPARATOR + value + " " + weightUnit;
    }


    public void addRow() {
        Context c = getContext();
        if (c == null) {
            Log.d("--------------- Debug", "AddROw Empty Context");
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
        Long timeStamp = new Date().getTime();
       // String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(lDate);

        // Update DB
        try {
            healthDB = c.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            ContentValues values = new ContentValues();
            values.put("timestamp", timeStamp );
            values.put("weight", Double.parseDouble(value));

            Long result = healthDB.insert(tableName, null, values);
            Log.d("--------------- Debug", " " + result);


/*
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date;
            date = format.parse("2019-05-26 09:00");
            Double w = 99.1;
            values.put("timestamp", date.getTime() );
            values.put("weight", w);
            result = healthDB.insert(tableName, null, values);
            Log.d("--------------- Debug", " " + result);

            date = format.parse("2019-05-27 07:36");
            w = 99.3;
            values.put("timestamp", date.getTime() );
            values.put("weight", w);
            result = healthDB.insert(tableName, null, values);

            date = format.parse("2019-05-27 22:27");
            w = 100.0;
            values.put("timestamp", date.getTime() );
            values.put("weight", w);
            result = healthDB.insert(tableName, null, values);

            date = format.parse("2019-05-28 07:32");
            w = 98.6;
            values.put("timestamp", date.getTime() );
            values.put("weight", w);
            result = healthDB.insert(tableName, null, values);

            date = format.parse("2019-05-29 08:12");
            w = 98.3;
            values.put("timestamp", date.getTime() );
            values.put("weight", w);
            result = healthDB.insert(tableName, null, values);

            date = format.parse("2019-05-31 08:13");
            w = 97.9;
            values.put("timestamp", date.getTime() );
            values.put("weight", w);
            result = healthDB.insert(tableName, null, values);
*/

            healthDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        weight.setText("");

        // Add to array
        weightArray.add(0, formatRow(timeStamp, value));
        weightArrayHelper.add(0, timeStamp);

        weightList.invalidateViews();
    }


    public void deleteRow(int index) {
        Context c = getContext();
        if (c == null) {
            Log.d("--------------- Debug", "DeleteRow Empty Context");
            return;
        }

        Long timeStamp = weightArrayHelper.get(index);
//        String timeStamp = key.substring(0, 16);

        // Update DB
        try {
            healthDB = c.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            int result = healthDB.delete(tableName, "timestamp=" + timeStamp, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Remove from array
        weightArray.remove(index);
        weightArrayHelper.remove(index);
        weightList.invalidateViews();
    }


    public void editRow(int index) {
        Context c = getContext();
        if (c == null) {
            Log.d("--------------- Debug", "editRow Empty Context");
            return;
        }

        String key = weightArray.get(index);
        String timeStamp = key.substring(0, 16);
        Log.d("Debug: ", timeStamp);

        // Show two fields, date and value
        // Sanity checks

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(timeStamp);

        final EditText input = new EditText(c);
        key = key.substring(18);
        key = key.replace(weightUnit, "");
        key = key.replaceAll(" ","");
        input.setText(key);

        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editField = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                return;
            }
        });

        builder.show();

        Log.d("----------- Debug", " " + editField);

        // Update DB
        try {
            healthDB = c.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            ContentValues values = new ContentValues();
            values.put("timestamp", timeStamp);
            values.put("weight", editField);

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
        weightArrayHelper.clear();

        // Clear Database
        try {
            healthDB = c.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            int result = healthDB.delete(tableName, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        weightList.invalidateViews();
        Toast.makeText(getContext(), records + " " + getString(R.string.recordsDeleted), Toast.LENGTH_SHORT).show();
    }


    public void send(String sep) {
        Context c = getContext();
        if (c == null) {
            Log.d("--------------- Debug", "Send Empty Context");
            return;
        }

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
 //           healthDB.execSQL("DROP TABLE IF EXISTS weightTB");
            String command = "CREATE TABLE IF NOT EXISTS " + tableName + " (timestamp DATE, weight REAL)";
            healthDB.execSQL(command);
            Cursor weightCursor = healthDB.query(tableName, null,null, null,null,null, "timestamp DESC",null);

            int dateIndex = weightCursor.getColumnIndex("timestamp");
            int weightIndex = weightCursor.getColumnIndex("weight");

            while (weightCursor.moveToNext()) {
                Log.d("Debug Cursor: ", weightCursor.getString(dateIndex) + " " + weightCursor.getString(weightIndex));
                weightArray.add(formatRow(weightCursor.getLong(dateIndex), weightCursor.getString(weightIndex)));
                weightArrayHelper.add(weightCursor.getLong(dateIndex));
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

        TextView unit = view.findViewById(R.id.text_unit);
        unit.setText(weightUnit);

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

        button_showWeightChart = view.findViewById(R.id.button_showWeightChart);
        button_showWeightChart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    Intent intent = new Intent(c, WeightChartActivity.class);
                    startActivity(intent);
                    // return true;
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
                            }
                        })
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

    /*

              // Table migration
            Cursor tmpCursor = healthDB.query("weight", null,null, null,null,null, null,null);
            String ts;
            String w;
            ContentValues values = new ContentValues();
             Long result = healthDB.insert(tableName, null, values);
            while (tmpCursor.moveToNext()) {
                ts = tmpCursor.getString(dateIndex);
                w = tmpCursor.getString(weightIndex);
                values.put("timestamp", ts);
                values.put("weight", w);
                Long r = healthDB.insert(tableName, null, values);
                Log.d("Tmp Cursor: ", ts + " " + w);
            }
            tmpCursor.close();
            // Temp end


     */
