package com.zell_mbc.medilog;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class BloodPressureChartFragment extends Fragment {

    SQLiteDatabase healthDB;
    String dbName = "healthDB";
    String tableName = "bloodPressureTB";

    private XYPlot plot;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.bloodpressure_chart, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Context c = getContext();
        if (c == null) {
            Log.d("--------------- Debug", " Chart Empty Context");
            return;
        }


        // create a couple arrays of y-values to plot:
        final ArrayList<String> labels = new ArrayList<String>();
        ArrayList <Float>sys = new ArrayList<Float>();
        ArrayList <Float>dia = new ArrayList<Float>();
        ArrayList <Float>pulse = new ArrayList<Float>();

        try {
            healthDB = c.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            healthDB.execSQL("CREATE TABLE IF NOT EXISTS weight (timestamp VARCHAR, weight VARCHAR)");
            Cursor bloodPressureCursor = healthDB.query(tableName, null,null, null,null,null, null,null);

            int dateIndex    = bloodPressureCursor.getColumnIndex("timestamp");
            int sysIndex     = bloodPressureCursor.getColumnIndex("sys");
            int diaIndex     = bloodPressureCursor.getColumnIndex("dia");
            int pulseIndex   = bloodPressureCursor.getColumnIndex("pulse");

            String sTmp = "";
            Long lTmp;

            int i = 0;
            while (bloodPressureCursor.moveToNext()) {
                lTmp = bloodPressureCursor.getLong(dateIndex);
                sTmp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(lTmp);
                sTmp = sTmp.substring(5,10);
                labels.add(sTmp);

                sTmp = bloodPressureCursor.getString(sysIndex);
                sys.add(Float.valueOf(sTmp));
                sTmp = bloodPressureCursor.getString(diaIndex);
                dia.add(Float.valueOf(sTmp));
                sTmp = bloodPressureCursor.getString(pulseIndex);
                pulse.add(Float.valueOf(sTmp));
//                Log.d("Debug Labels: ", " " + labels.get(i));
                i++;
            }

            bloodPressureCursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sys.size() == 0) {
            return;
        }

        // initialize our XYPlot reference:
        plot = (XYPlot) view.findViewById(R.id.bloodPressurePlot);
//        plot.getLegend().setVisible(false);

        XYSeries series1 = new SimpleXYSeries(sys, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Sys");
        XYSeries series2 = new SimpleXYSeries(dia, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Dia");
        XYSeries series3 = new SimpleXYSeries(pulse, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Pulse");

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.BLUE, null, null, null);
        LineAndPointFormatter series2Format = new LineAndPointFormatter(Color.RED, null, null, null);
        LineAndPointFormatter series3Format = new LineAndPointFormatter(Color.GREEN, null, null, null);

        // add a new series' to the xyplot:
        plot.addSeries(series1, series1Format);
        plot.addSeries(series2, series2Format);
        plot.addSeries(series3, series3Format);

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