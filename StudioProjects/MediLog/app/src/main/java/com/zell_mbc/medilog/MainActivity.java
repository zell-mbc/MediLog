package com.zell_mbc.medilog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Context keepContext;

    BloodPressureFragment bloodPressureFragment;
    WeightFragment weightFragment;

    private TabLayout tabLayout;


    public void addWeight(View view) {
        EditText weight = findViewById(R.id.editWeight);

        // Check empty variables
        String sTmp = weight.getText().toString();
        if (sTmp.length() == 0) {
            Toast.makeText(this, getString(R.string.weightMissing), Toast.LENGTH_SHORT).show();
            return;
        }

        weightFragment.addRow(sTmp);
        weight.setText("");
    }


    public void addPressure(View view) {
        EditText sys     = findViewById(R.id.editSys);
        EditText dia     = findViewById(R.id.editDia);
        EditText pulse   = findViewById(R.id.editPulse);
        EditText comment = findViewById(R.id.editComment);
        String DELIMITER = ";";

        // ###########################
        // Checks
        // ###########################
        // Check empty variables
        if (sys.getText().toString().length() == 0) {
            Toast.makeText(this, getString(R.string.sysMissing), Toast.LENGTH_SHORT).show();
            return;
        }
        if (dia.getText().toString().length() == 0) {
            Toast.makeText(this, getString(R.string.diaMissing), Toast.LENGTH_SHORT).show();
            return;
        }
        if (pulse.getText().toString().length() == 0) {
            Toast.makeText(this, getString(R.string.pulseMissing), Toast.LENGTH_SHORT).show();
            return;
        }

        // Check sanity
        String sTmp = sys.getText().toString();
        int iTmp = Integer.parseInt(sTmp);
        if(iTmp > 200 ){
            Toast.makeText(this, "Systolic value too high to be plausible!", Toast.LENGTH_SHORT).show();
            return;
        }

        sTmp = dia.getText().toString();
        iTmp = Integer.parseInt(sTmp);
        if(iTmp > 200 ){
            Toast.makeText(this, "Systolic value too high to be plausible!", Toast.LENGTH_SHORT).show();
            return;
        }

        sTmp = pulse.getText().toString();
        iTmp = Integer.parseInt(sTmp);
        if(iTmp > 200 ){
            Toast.makeText(this, "Pulse value too high to be plausible!", Toast.LENGTH_SHORT).show();
            return;
        }

        // ###########################
        // Save values
        // ###########################
        String values = sys.getText().toString() + DELIMITER + dia.getText().toString() + DELIMITER + pulse.getText().toString() + DELIMITER + comment.getText().toString() ;
        bloodPressureFragment.addRow(values);

        sys.setText("");
        dia.setText("");
        pulse.setText("");
        comment.setText("");
        sys.bringToFront();

    }


    void deleteDB() {
        final int activeTab = tabLayout.getSelectedTabPosition();

        String sMessage = "";
        if (activeTab == 0) { sMessage = getString(R.string.blood_pressure); }
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
                        if (activeTab == 0) {
                            bloodPressureFragment.deleteDB();
                        };
                        if (activeTab == 1) {
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

        /*
        DataHandler dh  = DataHandler.of(this).get(DataHandler.class);
        DataHandler.getData().observe(this, dbArray -> {
            // update UI
        });

        DataHandler dh = new DataHandler(getSharedPreferences("Weight", MODE_PRIVATE));

        Toast.makeText(this, "DataHandler Size: " + String.valueOf(dh.size()), Toast.LENGTH_SHORT).show();
*/

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
        bloodPressureFragment = new BloodPressureFragment();
        weightFragment = new WeightFragment();
        Log.d("Debug: weightFragment", weightFragment.toString());

        adapter.addFragment(bloodPressureFragment, getString(R.string.tab_bloodpressure));
        adapter.addFragment(weightFragment, getString(R.string.tab_weight));
        Toast.makeText(this, "Main Adapter: " + adapter.getCount(), Toast.LENGTH_SHORT).show();

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }


        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

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
            if (activeTab == 0) {
                bloodPressureFragment.send(sep);
            };
            if (activeTab == 1) {
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
        if (id == R.id.action_demoData) {
            int rec = 1000;

            if (activeTab == 0) {
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
             if (activeTab == 1) {
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

        return super.onOptionsItemSelected(item);
    }

}
