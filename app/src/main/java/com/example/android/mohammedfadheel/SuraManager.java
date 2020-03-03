package com.example.android.mohammedfadheel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

class SuraManager {

    final String MEDIA_PATH = "/sdcard/";
    private ArrayList<HashMap<String, String>> surasList = new ArrayList<HashMap<String, String>>();

    public SuraManager(){

    }

    public ArrayList<HashMap<String, String>> getSurasList(String SuraName) {
        File home = new File(MEDIA_PATH);

        ArrayList<Data> list = new ArrayList<Data>();
        for (Data temp : list) {
            HashMap<String, String> sura = new HashMap<String, String>();
            sura.put("name", temp.subject);
            sura.put("suraPath", temp.link);
            surasList.add(sura);
        }

        return surasList;
    }

}
