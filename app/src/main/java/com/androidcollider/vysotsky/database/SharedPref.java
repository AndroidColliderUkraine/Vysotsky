package com.androidcollider.vysotsky.database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by pseverin on 21.01.15.
 */
public class SharedPref {

    private Context context;
    private SharedPreferences sharedPreferences;
    private final static String APP_PREFERENCES = "VysotskyPref";

    private final static String[] tableNames = new String[]{"Song", "Comment"};

    public SharedPref(Context context){
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setLocalUpdateDates(String table, long time) {
        sharedPreferences.edit().putLong(table, time).apply();
    }

    public ArrayList<Long> getLocalUpdates() {
        ArrayList<Long> localUpdates = new ArrayList<>();
        for (int i = 0; i < tableNames.length; i++) {
            localUpdates.add(sharedPreferences.getLong(tableNames[i], 0));
        }
        return localUpdates;
    }

    public void savePref(String sPrefType, boolean wasStarted) {
        if (sPrefType.equals("wasStarted")) {
            sharedPreferences.edit().
                    putBoolean(sPrefType, wasStarted)
                    .apply();
        }

    }
    public boolean isNotFirstStart() {
        return sharedPreferences.getBoolean("wasStarted", false);
    }

    public int[] getListSettings(){
        int[] listSettings = new int[3];
        listSettings[0] = sharedPreferences.getInt("listSettings_sort",0);
        listSettings[0] = sharedPreferences.getInt("listSettings_favorite",0);
        listSettings[0] = sharedPreferences.getInt("listSettings_search",0);
        return listSettings;
    }

    public void setListSettings(int[] listSettings){
        sharedPreferences.edit().putInt("listSettings_sort",listSettings[0]).apply();
        sharedPreferences.edit().putInt("listSettings_sort",listSettings[1]).apply();
        sharedPreferences.edit().putInt("listSettings_sort",listSettings[2]).apply();
    }

    public void setShowFonts(boolean isShow){
        sharedPreferences.edit().putBoolean("showFonts",isShow).apply();
    }
    public void setShowAcc(boolean isShow){
        sharedPreferences.edit().putBoolean("showAcc",isShow).apply();
    }
    public void setShowScroll(boolean isShow){
        sharedPreferences.edit().putBoolean("showScroll",isShow).apply();
    }
    public void setShowScreen(boolean isShow){
        sharedPreferences.edit().putBoolean("showScreen",isShow).apply();
    }
    public void setShowHelp(boolean isShow){
        sharedPreferences.edit().putBoolean("showHelp",isShow).apply();
    }
    public void setShowHelpDr(boolean isShow){
        sharedPreferences.edit().putBoolean("showHelpDr",isShow).apply();
    }

    public boolean getShowFonts(){
        return sharedPreferences.getBoolean("showFonts", true);
    }
    public boolean getShowAcc(){
        return sharedPreferences.getBoolean("showAcc", true);
    }
    public boolean getShowScroll(){
        return sharedPreferences.getBoolean("showScroll", true);
    }
    public boolean getShowScreen(){
        return sharedPreferences.getBoolean("showScreen", true);
    }
    public boolean getShowHelp(){
        return sharedPreferences.getBoolean("showHelp", true);
    }
    public boolean getShowHelpDr(){
        return sharedPreferences.getBoolean("showHelpDr", true);
    }

}
