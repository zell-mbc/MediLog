package com.zell_mbc.medilog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {



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
        String DELIMITER = "delimiter";

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Plumbing
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
        bloodPressureFragment = new BloodPressureFragment();
        weightFragment = new WeightFragment();

        adapter.addFragment(bloodPressureFragment, getString(R.string.tab_bloodpressure));
        adapter.addFragment(weightFragment, getString(R.string.tab_weight));

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
 //           Toast.makeText(this, "Data sent!", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_deleteDB) {
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

            return true;
        }
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

        return super.onOptionsItemSelected(item);
    }
}
