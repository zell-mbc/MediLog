package com.zell_mbc.medilog;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

// Chart manual
//https://github.com/PhilJay/MPAndroidChart

public class WeightChartFragment extends Fragment {
    SQLiteDatabase healthDB;
    String dbName = "healthDB";
    String tableName = "weightTB";

    private XYPlot plot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.weight_chart, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Context c = getContext();
        if (c == null) {
            Log.d("--------------- Debug", "AddROw Empty Context");
            return;
        }


        // create a couple arrays of y-values to plot:
        final ArrayList <String> labels = new ArrayList<String>();
        ArrayList <Float>weights = new ArrayList<Float>();


        try {
            healthDB = c.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            Cursor weightCursor = healthDB.query(tableName, null,null, null,null,null, null,null);

            int dateIndex = weightCursor.getColumnIndex("timestamp");
            int weightIndex = weightCursor.getColumnIndex("weight");
            String sTmp;
            Long lTmp;

            int i = 0;
            while (weightCursor.moveToNext()) {
                lTmp = weightCursor.getLong(dateIndex);
                sTmp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(lTmp);

                sTmp = sTmp.substring(5,10);
                labels.add(sTmp);
                sTmp = weightCursor.getString(weightIndex);
                weights.add(Float.valueOf(sTmp));
//                Log.d("Debug Labels: ", " " + labels.get(i));
                i++;
            }

            weightCursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (weights.size() == 0) {
            return;
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        String weightUnit = " " + sharedPref.getString(SettingsActivity.KEY_PREF_WEIGHTUNIT, "kg");

        // initialize our XYPlot reference:
        plot = (XYPlot) view.findViewById(R.id.weightPlot);
        plot.getLegend().setVisible(false);

        XYSeries series1 = new SimpleXYSeries(weights, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Weight");

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.BLUE, Color.GREEN, Color.BLUE, null);

        // add a new series' to the xyplot:
        plot.addSeries(series1, series1Format);

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(labels.get(i));
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });
    }
}