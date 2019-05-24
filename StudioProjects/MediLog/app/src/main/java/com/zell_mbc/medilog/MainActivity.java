package com.zell_mbc.medilog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    BloodPressureFragment bloodPressureFragment;
    WeightFragment weightFragment;
    int bloodPressureFragment_tabID;
    int weightFragment_tabID;
    TabLayout tabLayout;
    private TabAdapter adapter;
    SQLiteDatabase healthDB;

    void deleteDB() {
        final int activeTab = tabLayout.getSelectedTabPosition();

        String sMessage = "";
        if (activeTab == bloodPressureFragment_tabID) { sMessage = getString(R.string.blood_pressure); }
        else { sMessage = getString(R.string.weight); }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.action_deleteDB));
        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.doYouReally1) + " " + sMessage + " " + getString(R.string.doYouReally2))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // Delete the Database
                        if (activeTab == bloodPressureFragment_tabID) {
                            bloodPressureFragment.deleteDB();
                        };
                        if (activeTab == weightFragment_tabID) {
                            weightFragment.deleteDB();
                        };
                        dialog.cancel();
                    }
                })
                .setNegativeButton(getString(R.string.no),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean showBloodPressureTab = sharedPref.getBoolean(SettingsActivity.KEY_PREF_showBloodPressureTab, true);
        Boolean showWeightTab = sharedPref.getBoolean(SettingsActivity.KEY_PREF_showWeightTab, true);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        adapter = new TabAdapter(getSupportFragmentManager());

  /*      try {
            SQLiteDatabase healthDB = this.openOrCreateDatabase("healthDB", MODE_PRIVATE, null);
        }
        catch (Exception e) { e.printStackTrace(); }
*/


        weightFragment = null;
        if (showWeightTab) {
            weightFragment = new WeightFragment();
            adapter.addFragment(weightFragment, getString(R.string.tab_weight));
            weightFragment_tabID = adapter.getCount() -1;
        }
        if (showBloodPressureTab) {
            bloodPressureFragment = new BloodPressureFragment();
            adapter.addFragment(bloodPressureFragment, getString(R.string.tab_bloodpressure));
            bloodPressureFragment_tabID = adapter.getCount() -1;
        }

        if (!(showWeightTab && showBloodPressureTab)) {
            Toast.makeText(this, "No Tab seleted?", Toast.LENGTH_SHORT).show();
        }
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }


    /*
    @Override
    protected void onDestroy () {

        try { healthDB.close(); }
        catch (Exception e) { e.printStackTrace(); }

        super.onDestroy();
    }
*/
        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        Log.d("--------------- Debug", " " + weightFragment.weightArray.size());

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
 //           Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
  //          Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }


    // Menue
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();
        final int activeTab = tabLayout.getSelectedTabPosition();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String sep = sharedPref.getString(SettingsActivity.KEY_PREF_DELIMITER,";");
            if (activeTab == bloodPressureFragment_tabID) {
                bloodPressureFragment.send(sep);
            };
            if (activeTab == weightFragment_tabID) {
                weightFragment.send(sep);
            };
            deleteDB();
            return true;
        }
/*        if (id == R.id.action_deleteDB) {
            deleteDB();
            return true;
        } */
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
/*
    <item
        android:id="@+id/action_demoData"
        android:orderInCategory="100"
        android:title="Create Demodata"
        app:showAsAction="never" />

        if (id == R.id.action_demoData) {
            int rec = 1000;

            if (activeTab == bloodPressureFragment_tabID) {
                //blood_pressure
                String DATA = "BloodPressure";
                SharedPreferences DB = getSharedPreferences(DATA, MODE_PRIVATE);
                SharedPreferences.Editor editor = DB.edit();
                int i;
                for (i=0; i< rec ; i++) {
                    String si = String.valueOf(i);
                    editor.putString(si, si + ";" + si + ";" + si + ";");
                    editor.commit();
                }
             }
             if (activeTab == weightFragment_tabID) {
                String DATA = "Weight";

                SharedPreferences DB = getSharedPreferences(DATA, MODE_PRIVATE);
                SharedPreferences.Editor editor = DB.edit();
                int i;
                for (i=0; i< rec ; i++) {
                    String si = String.valueOf(i);
                    editor.putString(si, si);
                    editor.commit();
                }
            }

            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }

}
