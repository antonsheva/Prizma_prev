package com.eshelon.prizma_prev;

import static android.content.Context.MODE_PRIVATE;

import static com.eshelon.prizma_prev.C_.PREFS_FILE;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Preferences {
    SharedPreferences settings;
    public Preferences(Context context) {
        settings =  context.getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
    }

    public int getPatternNum(int pattern){
        int val;
        val = settings.getInt("patt"+pattern, -1);
        if(val == -1)val = pattern;
        return val;
    }
    public ArrayList<Integer>getPatternList(){
        ArrayList<Integer>pList = new ArrayList<>();
        pList.add(settings.getInt("patt1", -1));
        pList.add(settings.getInt("patt2", -1));
        pList.add(settings.getInt("patt3", -1));
        pList.add(settings.getInt("patt4", -1));
        return pList;
    }

    public void setPatternNum(int patternButton, int patternDb){
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("patt"+patternButton, patternDb);
        editor.apply();
    }
}
