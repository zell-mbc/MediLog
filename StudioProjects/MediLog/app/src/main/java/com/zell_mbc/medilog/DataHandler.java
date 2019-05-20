package com.zell_mbc.medilog;

/*
public class DataHandler extends ViewModel {
    public ArrayList<String> dataArray = new ArrayList<String>();
    SharedPreferences DB = null;


    public getData(SharedPreferences DataBase) {
//        Toast.makeText(c.getApplicationContext(), "Debug: DB?" + DB, Toast.LENGTH_SHORT).show();
        DB = DataBase;
        String sTmp;
        Map<String, ?> allEntries = DB.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            sTmp = entry.getValue().toString();
            dataArray.add(sTmp.replace(";", " - ") + " kg");
        }

        // Should not be necessary but well...
        Collections.sort(dataArray, Collections.reverseOrder());
        Log.d("------------- DH Debug: ", "Constructor");
    }


    public int size(){
        return dataArray.size();
    }

    public int addEntry(String value){
        // Get date
        Long timeStamp = (long) new Date().getTime();

        // Update DB
        SharedPreferences.Editor editor = DB.edit();
        value = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timeStamp) + "; " + value;

        editor.putString(timeStamp.toString(), value);
        editor.commit();

        // Add to array
        dataArray.add(value.replace(";", " - ") + " kg");
        Log.d("------------- DH Debug: ", "AddEntry");

        return dataArray.size();
        }
}

*/